package co.books.api.order.service;

import co.books.api.book.dto.PageInfo;
import co.books.api.book.entity.BookEntity;
import co.books.api.book.repo.BookRepository;
import co.books.api.common.exception.NotFoundException;
import co.books.api.order.dto.CreateOrderRequest;
import co.books.api.order.dto.CreateOrderResponse;
import co.books.api.order.dto.OrderItemRequest;
import co.books.api.order.dto.OrderListItemDto;
import co.books.api.order.dto.OrderListResponse;
import co.books.api.order.entity.OrderEntity;
import co.books.api.order.entity.OrderItemEntity;
import co.books.api.order.entity.OrderStatus;
import co.books.api.order.repo.OrderItemRepository;
import co.books.api.order.repo.OrderRepository;
import co.books.api.user.entity.UserEntity;
import co.books.api.user.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 주문 생성 서비스.
 *
 * <p>금액은 모두 books.sale_price 기준으로 서버에서 재계산하며,
 * 클라이언트가 보낸 priceAtPurchase 는 사용하지 않는다(★위변조 방지).</p>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private static final DateTimeFormatter ORDER_ID_DATE_FMT = DateTimeFormatter.ofPattern("yyyyMMdd");

    /** 구매목록 페이지당 행 수. */
    private static final int ORDER_LIST_PAGE_SIZE = 10;

    private static final ZoneId SEOUL = ZoneId.of("Asia/Seoul");

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    /**
     * 주문을 생성한다.
     *
     * <ol>
     *   <li>포인트 검증 (users.points &gt;= usedPoints)</li>
     *   <li>books 일괄 조회 (없는 bookId 가 있으면 400)</li>
     *   <li>총 금액 재계산 (sale_price 기준)</li>
     *   <li>orderId 채번, 주문/주문상세 INSERT, 포인트 차감</li>
     * </ol>
     */
    @Transactional
    public CreateOrderResponse createOrder(CreateOrderRequest request, String userId) {
        int usedPoints = request.usedPoints() == null ? 0 : request.usedPoints();

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("사용자를 찾을 수 없습니다."));
        if (usedPoints > 0 && (user.getPoints() == null || user.getPoints() < usedPoints)) {
            throw new IllegalArgumentException("보유 포인트가 부족합니다.");
        }

        List<String> bookIds = request.items().stream().map(OrderItemRequest::bookId).toList();
        List<BookEntity> books = bookRepository.findAllById(bookIds);
        if (books.size() != bookIds.stream().distinct().count()) {
            throw new IllegalArgumentException("존재하지 않는 도서가 포함되어 있습니다.");
        }
        Map<String, BookEntity> bookMap = new HashMap<>();
        for (BookEntity b : books) bookMap.put(b.getBookId(), b);

        // 상품 금액 합계 = sum(수량 × 판매가)
        int productTotal = 0;
        for (OrderItemRequest item : request.items()) {
            BookEntity book = bookMap.get(item.bookId());
            if (book == null) {
                throw new IllegalArgumentException("존재하지 않는 도서가 포함되어 있습니다: " + item.bookId());
            }
            productTotal += book.getSalePrice() * item.quantity();
        }
        int totalAmount = productTotal - usedPoints;
        if (totalAmount < 0) {
            throw new IllegalArgumentException("결제 금액이 0 미만이 될 수 없습니다.");
        }

        String orderId = generateOrderId();
        String orderName = buildOrderName(request.items(), bookMap);

        OrderEntity order = new OrderEntity();
        order.setOrderId(orderId);
        order.setUserId(userId);
        order.setReceiver(request.receiver());
        order.setPhone(request.phone());
        order.setTotalAmount(totalAmount);
        order.setStatus(OrderStatus.PENDING);
        order.setShippingAddress(request.shippingAddress());
        order.setShippingDetailAddress(
                request.shippingDetailAddress() == null ? "" : request.shippingDetailAddress());
        order.setUsedPoints(usedPoints);
        orderRepository.save(order);

        for (OrderItemRequest item : request.items()) {
            BookEntity book = bookMap.get(item.bookId());
            OrderItemEntity oi = new OrderItemEntity();
            oi.setOrderId(orderId);
            oi.setBookId(item.bookId());
            oi.setQuantity(item.quantity());
            oi.setPriceAtPurchase(book.getSalePrice());
            orderItemRepository.save(oi);
        }

        if (usedPoints > 0) {
            user.setPoints(user.getPoints() - usedPoints);
        }

        log.info("주문 생성: orderId={}, userId={}, totalAmount={}, usedPoints={}",
                orderId, userId, totalAmount, usedPoints);

        return new CreateOrderResponse(orderId, totalAmount, orderName);
    }

    /**
     * 본인의 구매목록(주문내역) 페이징 조회.
     *
     * <p>대상 상태는 결제완료(PAID) / 배송중(SHIPPED) / 배송완료(DELIVERED) 이며,
     * 주문취소(CANCELLED) 및 결제실패(FAILED) 는 제외한다.
     * orderedAt 역순 정렬, 페이지당 {@value #ORDER_LIST_PAGE_SIZE} 건.</p>
     *
     * <p>각 주문의 대표 이미지는 해당 주문의 첫 번째 OrderItem 의 도서 imageUrl 을 사용한다.
     * 주문명은 첫 책 제목 (다건이면 "{첫 책 제목} 외 N-1건") 으로 조립한다.</p>
     */
    @Transactional(readOnly = true)
    public OrderListResponse getMyOrders(String userId, Integer page) {
        int safePage = (page == null || page < 1) ? 1 : page;
        Pageable pageable = PageRequest.of(safePage - 1, ORDER_LIST_PAGE_SIZE);

        Page<OrderEntity> orderPage = orderRepository.findCompletedByUserId(userId, pageable);
        List<OrderEntity> orders = orderPage.getContent();

        // 각 주문의 전체 OrderItem 을 한 번씩 조회 (첫 번째 항목 + 총 개수에 사용)
        Map<String, List<OrderItemEntity>> itemsByOrderId = orders.stream()
                .collect(Collectors.toMap(
                        OrderEntity::getOrderId,
                        o -> orderItemRepository.findByOrderId(o.getOrderId())
                ));

        // 표시에 필요한 첫 번째 책 정보만 일괄 조회
        List<String> firstBookIds = itemsByOrderId.values().stream()
                .map(items -> items.isEmpty() ? null : items.get(0).getBookId())
                .filter(java.util.Objects::nonNull)
                .distinct()
                .toList();
        Map<String, BookEntity> bookMap = bookRepository.findAllById(firstBookIds).stream()
                .collect(Collectors.toMap(BookEntity::getBookId, b -> b));

        List<OrderListItemDto> data = orders.stream()
                .map(order -> {
                    List<OrderItemEntity> items = itemsByOrderId.get(order.getOrderId());
                    Optional<OrderItemEntity> first = items.stream().findFirst();
                    BookEntity firstBook = first.map(it -> bookMap.get(it.getBookId())).orElse(null);

                    String orderName;
                    if (firstBook == null) {
                        orderName = "";
                    } else if (items.size() <= 1) {
                        orderName = firstBook.getTitle();
                    } else {
                        orderName = firstBook.getTitle() + " 외 " + (items.size() - 1) + "건";
                    }
                    String imageUrl = firstBook != null ? firstBook.getImageUrl() : null;

                    String orderDate = order.getOrderedAt()
                            .atZoneSameInstant(SEOUL)
                            .toLocalDate()
                            .toString();

                    String statusLabel = switch (order.getStatus()) {
                        case PENDING -> "주문 대기";
                        case PAID -> "결제 완료";
                        case SHIPPED -> "배송중";
                        case DELIVERED -> "배송 완료";
                        case CANCELLED -> "취소됨";
                        case FAILED -> "결제 실패";
                    };

                    return new OrderListItemDto(
                            order.getOrderId(),
                            orderDate,
                            orderName,
                            order.getTotalAmount(),
                            statusLabel,
                            imageUrl
                    );
                })
                .toList();

        PageInfo pageInfo = new PageInfo(safePage, orderPage.getTotalElements());
        return OrderListResponse.ok(data, pageInfo);
    }

    /**
     * 주문 ID 채번: "ord_" + yyyyMMdd + "_" + UUID 앞 8자리.
     */
    private String generateOrderId() {
        String date = LocalDate.now().format(ORDER_ID_DATE_FMT);
        String suffix = UUID.randomUUID().toString().replace("-", "").substring(0, 8);
        return "ord_" + date + "_" + suffix;
    }

    /**
     * 주문명을 생성한다. 단건이면 책 제목, 다건이면 "{첫 책 제목} 외 N-1건".
     */
    private String buildOrderName(List<OrderItemRequest> items, Map<String, BookEntity> bookMap) {
        BookEntity first = bookMap.get(items.get(0).bookId());
        String firstTitle = first.getTitle();
        if (items.size() == 1) {
            return firstTitle;
        }
        return firstTitle + " 외 " + (items.size() - 1) + "건";
    }
}
