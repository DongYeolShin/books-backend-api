package co.books.api.book.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

/**
 * book_categories 테이블의 복합 기본키.
 * (book_id, category_id) 조합으로 한 쌍의 매핑을 식별한다.
 */
@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class BookCategoryId implements Serializable {

    @Column(name = "book_id", length = 100)
    private String bookId;

    @Column(name = "category_id")
    private Integer categoryId;
}
