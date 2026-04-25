package co.books.api.category.repo;

import co.books.api.category.entity.CategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 카테고리 리포지토리.
 * categories 테이블에 대한 기본 CRUD 와 slug 조회, 표시 순서 정렬 조회를 제공한다.
 */
@Repository
public interface CategoryRepository extends JpaRepository<CategoryEntity, Integer> {

    /** URL 식별자(slug) 로 카테고리 단건 조회 */
    Optional<CategoryEntity> findBySlug(String slug);

    /** 메뉴 표시 순서대로 전체 카테고리 조회 */
    List<CategoryEntity> findAllByOrderByDisplayOrderAsc();
}
