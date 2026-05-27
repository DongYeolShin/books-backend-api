package co.books.api.user.service;

import co.books.api.book.entity.BookEntity;
import co.books.api.book.repo.BookRepository;
import co.books.api.common.exception.NotFoundException;
import co.books.api.order.entity.OrderEntity;
import co.books.api.order.entity.OrderItemEntity;
import co.books.api.order.repo.OrderItemRepository;
import co.books.api.order.repo.OrderRepository;
import co.books.api.user.dto.MyInfoDto;
import co.books.api.user.dto.MyPageData;
import co.books.api.user.dto.MyPageResponse;
import co.books.api.user.dto.RecentOrderDto;
import co.books.api.user.entity.UserEntity;
import co.books.api.user.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 회원 서비스.
 * 마이페이지 조회 등 회원 관련 비즈니스 로직을 처리한다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final BookRepository bookRepository;

    private static final DateTimeFormatter BIRTH_FORMATTER = DateTimeFormatter.ofPattern("yyyy.MM.dd");
    private static final ZoneId SEOUL = ZoneId.of("Asia/Seoul");

    /**
     * 마이페이지 정보를 조회한다.
     *
     * @param userId 인증된 사용자 ID
     * @throws NotFoundException userId 에 해당하는 사용자가 없는 경우
     */
    @Transactional(readOnly = true)
    public MyPageResponse getMyPage(String userId) {
        // 1. 사용자 조회
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("사용자를 찾을 수 없습니다."));

        // 2. 내 정보 DTO 변환 (birthDate null-safe)
        String birth = user.getBirthDate() != null
                ? user.getBirthDate().format(BIRTH_FORMATTER)
                : null;
        MyInfoDto myInfo = new MyInfoDto(
                user.getName(),
                birth,
                user.getGender(),
                user.getPhone(),
                user.getEmail(),
                user.getAddress(),
                user.getAddressDetail(),
                user.getPoints()
        );

        // 3. 최근 주문 5건 조회 (orderedAt DESC)
        List<OrderEntity> orders = orderRepository.findTop5ByUserIdOrderByOrderedAtDesc(userId);

        // 4. 각 주문의 첫 번째 OrderItem 조회
        Map<String, Optional<OrderItemEntity>> firstItemMap = orders.stream()
                .collect(Collectors.toMap(
                        OrderEntity::getOrderId,
                        o -> orderItemRepository.findByOrderId(o.getOrderId()).stream().findFirst()
                ));

        // 5. 필요한 bookId 수집 후 일괄 조회
        List<String> bookIds = firstItemMap.values().stream()
                .filter(Optional::isPresent)
                .map(opt -> opt.get().getBookId())
                .distinct()
                .toList();
        Map<String, BookEntity> bookMap = bookRepository.findAllById(bookIds)
                .stream().collect(Collectors.toMap(BookEntity::getBookId, b -> b));

        // 6. 주문 목록 → RecentOrderDto 변환
        List<RecentOrderDto> recentOrders = orders.stream()
                .map(order -> {
                    // orderedAt Asia/Seoul 기준 yyyy-MM-dd
                    String orderDate = order.getOrderedAt()
                            .atZoneSameInstant(SEOUL)
                            .toLocalDate()
                            .toString();

                    // 첫 번째 상품의 도서 정보 (없으면 빈 문자열)
                    Optional<OrderItemEntity> firstItem = firstItemMap.get(order.getOrderId());
                    BookEntity book = firstItem
                            .map(item -> bookMap.get(item.getBookId()))
                            .orElse(null);
                    String title = book != null ? book.getTitle() : "";
                    String author = book != null ? book.getAuthor() : "";

                    // 주문 상태 한글 라벨
                    String statusLabel = switch (order.getStatus()) {
                        case PENDING -> "주문 대기";
                        case PAID -> "결제 완료";
                        case SHIPPED -> "배송중";
                        case DELIVERED -> "배송 완료";
                        case CANCELLED -> "취소됨";
                        case FAILED -> "결제 실패";
                    };

                    return new RecentOrderDto(
                            order.getOrderId(),
                            orderDate,
                            title,
                            author,
                            order.getTotalAmount(),
                            statusLabel
                    );
                })
                .toList();

        return MyPageResponse.ok(new MyPageData(myInfo, recentOrders));
    }
}
