package co.books.api.cart.contrl;

import co.books.api.cart.dto.CartAddRequest;
import co.books.api.cart.dto.CartAddResponse;
import co.books.api.cart.dto.CartDeleteResponse;
import co.books.api.cart.dto.CartListResponse;
import co.books.api.cart.dto.CartUpdateRequest;
import co.books.api.cart.dto.CartUpdateResponse;
import co.books.api.cart.service.CartService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 장바구니 관련 REST API.
 * 기본 경로: /api/v1/carts
 * 모든 엔드포인트는 로그인된 사용자만 접근 가능하며,
 * 비로그인 요청은 SecurityConfig 의 anyRequest().authenticated() 규칙에 의해 401 로 차단된다.
 */
@RestController
@RequestMapping("/api/v1/carts")
@RequiredArgsConstructor
@Slf4j
public class CartController {

    private final CartService cartService;

    /**
     * 장바구니 등록.
     * 인증된 사용자 ID 를 기준으로 요청 본문의 도서/수량을 장바구니에 추가한다.
     *
     * JwtAuthenticationFilter 는 Authentication 의 principal 을 userId(String) 로 설정하므로,
     * 컨트롤러에서도 String 타입으로 받는다.
     */
    @PostMapping
    public ResponseEntity<CartAddResponse> add(
            @AuthenticationPrincipal String userId,
            @RequestBody CartAddRequest request) {

        try {
            int quantity = (request.quantity() == null || request.quantity() < 1) ? 1 : request.quantity();
            cartService.add(userId, request.bookId(), quantity);
            return ResponseEntity.ok(CartAddResponse.ok());
        } catch (Exception e) {
            log.error("장바구니 등록 실패: userId={}, bookId={}", userId, request.bookId(), e);
            return ResponseEntity.ok(CartAddResponse.fail());
        }
    }

    /**
     * 장바구니 리스트 조회.
     * 인증된 사용자의 장바구니에 담긴 도서들을 (담은 시점 최신순으로) 반환한다.
     */
    @GetMapping
    public ResponseEntity<CartListResponse> list(
            @AuthenticationPrincipal String userId) {
        return ResponseEntity.ok(
                CartListResponse.ok(cartService.getList(userId)));
    }

    /**
     * 장바구니 수량 변경.
     * 요청 본문의 bookId 가 사용자의 장바구니에 존재하면 quantity 로 갱신한다.
     * 해당 도서가 장바구니에 없거나 예외가 발생하면 실패 응답을 반환한다.
     */
    @PatchMapping
    public ResponseEntity<CartUpdateResponse> update(
            @AuthenticationPrincipal String userId,
            @RequestBody CartUpdateRequest request) {

        try {
            if (request.bookId() == null || request.quantity() == null || request.quantity() < 1) {
                return ResponseEntity.ok(CartUpdateResponse.fail());
            }
            boolean updated = cartService.modify(userId, request.bookId(), request.quantity());
            return ResponseEntity.ok(updated ? CartUpdateResponse.ok() : CartUpdateResponse.fail());
        } catch (Exception e) {
            log.error("장바구니 수량 변경 실패: userId={}, bookId={}", userId, request.bookId(), e);
            return ResponseEntity.ok(CartUpdateResponse.fail());
        }
    }

    /**
     * 장바구니 도서 삭제.
     * 경로의 bookId 가 사용자의 장바구니에 존재하면 해당 항목을 제거한다.
     * 해당 도서가 장바구니에 없거나 예외가 발생하면 실패 응답을 반환한다.
     */
    @DeleteMapping("/{bookId}")
    public ResponseEntity<CartDeleteResponse> delete(
            @AuthenticationPrincipal String userId,
            @PathVariable String bookId) {

        try {
            boolean removed = cartService.remove(userId, bookId);
            return ResponseEntity.ok(removed ? CartDeleteResponse.ok() : CartDeleteResponse.fail());
        } catch (Exception e) {
            log.error("장바구니 삭제 실패: userId={}, bookId={}", userId, bookId, e);
            return ResponseEntity.ok(CartDeleteResponse.fail());
        }
    }
}
