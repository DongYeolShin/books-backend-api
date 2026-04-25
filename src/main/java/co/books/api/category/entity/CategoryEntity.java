package co.books.api.category.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

/**
 * 도서 카테고리 엔티티.
 * categories 테이블과 매핑되며, 메뉴 표시 순서(displayOrder) 기준으로 정렬된다.
 */
@Entity
@Table(name = "categories")
@Getter
@Setter
public class CategoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private Integer categoryId;

    /** 카테고리명 (예: 웹프로그래밍) */
    @Column(nullable = false, unique = true, length = 50)
    private String name;

    /** URL용 이름 (예: web) */
    @Column(nullable = false, unique = true, length = 50)
    private String slug;

    /** 메뉴 표시 순서 */
    @Column(name = "display_order", nullable = false)
    private Integer displayOrder = 0;
}
