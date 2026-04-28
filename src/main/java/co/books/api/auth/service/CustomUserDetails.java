package co.books.api.auth.service;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

/**
 * 회원 이름 등 도메인 정보를 함께 노출하기 위한 UserDetails 확장.
 * Spring Security 의 {@link User} 를 상속하여 username/password/권한 처리는 그대로 활용한다.
 */
public class CustomUserDetails extends User {

    /** 회원 이름 (UserEntity.name) */
    private final String name;

    public CustomUserDetails(String username,
                             String password,
                             String name,
                             Collection<? extends GrantedAuthority> authorities) {
        super(username, password, authorities);
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
