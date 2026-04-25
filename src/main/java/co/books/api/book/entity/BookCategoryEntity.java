package co.books.api.book.entity;

import co.books.api.category.entity.CategoryEntity;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

/**
 * 도서-카테고리 매핑 엔티티 (N:M 조인 테이블).
 * book_categories 테이블과 매핑되며, 복합키 (bookId, categoryId) 를 가진다.
 * 현재는 부가 컬럼이 없지만, 향후 대표 카테고리 여부나 정렬 순서 등이 추가될 경우
 * 이 엔티티에 컬럼만 더하면 된다.
 */
@Entity
@Table(name = "book_categories")
@Getter
@Setter
public class BookCategoryEntity {

    @EmbeddedId
    private BookCategoryId id = new BookCategoryId();

    @MapsId("bookId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id")
    private BookEntity book;

    @MapsId("categoryId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private CategoryEntity category;

    /** JPA 가 요구하는 기본 생성자 */
    public BookCategoryEntity() {
    }

    /** BookEntity / CategoryEntity 를 지정해 매핑을 생성하는 편의 생성자 */
    public BookCategoryEntity(BookEntity book, CategoryEntity category) {
        this.book = book;
        this.category = category;
        this.id = new BookCategoryId(book.getBookId(), category.getCategoryId());
    }
}
