package co.books.api.book.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * 도서 정보 엔티티.
 * books 테이블과 매핑되며, BookCategoryEntity 를 통해 카테고리와 N:M 관계를 맺는다.
 */
@Entity
@Table(name = "books")
@Getter
@Setter
public class BookEntity {

    /** 도서 ID (수동 지정하는 문자열 PK) */
    @Id
    @Column(name = "book_id", length = 100)
    private String bookId;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(length = 200)
    private String subtitle;

    @Column(nullable = false, length = 100)
    private String author;

    @Column(length = 100)
    private String publisher;

    @Column(unique = true, length = 20)
    private String isbn;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "image_url", length = 500)
    private String imageUrl;

    @Column(name = "original_price", nullable = false)
    private Integer originalPrice;

    @Column(name = "sale_price", nullable = false)
    private Integer salePrice;

    @Column(nullable = false)
    private Integer stock = 0;

    @Column(name = "page_count")
    private Integer pageCount;

    @Column(name = "publish_date")
    private LocalDate publishDate;

    /** 베스트셀러 여부 ('Y' / 'N') */
    @Column(name = "is_bestseller", nullable = false, length = 1)
    private String isBestseller = "N";

    /** 신간 여부 ('Y' / 'N') */
    @Column(name = "is_new", nullable = false, length = 1)
    private String isNew = "N";

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    /** 도서-카테고리 N:M 매핑 (book_categories 조인 테이블, BookCategoryEntity 가 소유) */
    @OneToMany(mappedBy = "book", fetch = FetchType.LAZY,
            cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<BookCategoryEntity> bookCategories = new HashSet<>();
}
