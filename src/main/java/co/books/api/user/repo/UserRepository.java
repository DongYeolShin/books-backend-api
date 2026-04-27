package co.books.api.user.repo;

import co.books.api.user.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 회원 리포지토리.
 * users 테이블에 대한 기본 CRUD 와 이메일 조회, 중복 체크를 제공한다.
 */
@Repository
public interface UserRepository extends JpaRepository<UserEntity, String> {

    /** 이메일로 회원 단건 조회 */
    Optional<UserEntity> findByEmail(String email);

    /** 이메일 중복 여부 확인 */
    boolean existsByEmail(String email);
}
