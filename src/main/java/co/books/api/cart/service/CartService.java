package co.books.api.cart.service;

import co.books.api.cart.dto.CartListItemDto;
import co.books.api.cart.repo.CartItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 장바구니 서비스.
 * 회원의 장바구니에 도서를 추가하는 등록 로직과 목록 조회 로직을 제공한다.
 */
@Service
@RequiredArgsConstructor
public class CartService {

    private final CartItemRepository cartItemRepository;

    /**
     * 장바구니에 도서를 등록한다.
     * cart_items 의 (user_id, book_id) 유니크 제약을 이용한 UPSERT 로,
     * 동일 도서가 이미 담겨 있으면 기존 수량에 누적하고, 없으면 새 행을 삽입한다.
     * 동시 요청 시에도 단일 SQL 로 원자적으로 처리되어 중복 키 위반이 발생하지 않는다.
     */
    @Transactional
    public void add(String userId, String bookId, int quantity) {
        cartItemRepository.upsertCartItem(userId, bookId, quantity);
    }

    /**
     * 로그인 사용자의 장바구니 목록을 조회한다.
     * 도서 정보(title, author)는 books 테이블과 조인해 함께 반환한다.
     */
    @Transactional(readOnly = true)
    public List<CartListItemDto> getList(String userId) {
        return cartItemRepository.findCartListByUserId(userId);
    }

    /**
     * 장바구니에 담긴 도서의 수량을 새 값으로 갱신한다.
     * 해당 도서가 사용자의 장바구니에 없으면 false 를 반환한다.
     */
    @Transactional
    public boolean modify(String userId, String bookId, int quantity) {
        int affected = cartItemRepository.updateQuantity(userId, bookId, quantity);
        return affected > 0;
    }

    /**
     * 장바구니에서 특정 도서를 삭제한다.
     * 해당 도서가 사용자의 장바구니에 없으면 false 를 반환한다.
     */
    @Transactional
    public boolean remove(String userId, String bookId) {
        int affected = cartItemRepository.deleteByUserIdAndBookId(userId, bookId);
        return affected > 0;
    }
}
