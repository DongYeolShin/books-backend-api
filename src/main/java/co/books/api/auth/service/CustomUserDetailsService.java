package co.books.api.auth.service;

import co.books.api.user.entity.UserEntity;
import co.books.api.user.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

/**
 * Spring Security 인증에 사용할 UserDetailsService 구현체.
 * UsernamePasswordAuthenticationFilter 가 전달하는 username 을 user_id 로 간주하고
 * UserRepository 를 통해 회원을 조회한다.
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity user = userRepository.findById(username)
                .orElseThrow(() -> new UsernameNotFoundException("존재하지 않는 회원입니다. userId=" + username));

        // 권한 체계가 아직 없으므로 기본 ROLE_USER 만 부여한다.
        // 회원 이름을 함께 노출하기 위해 CustomUserDetails 로 감싼다.
        return new CustomUserDetails(
                user.getUserId(),
                user.getPasswd(),
                user.getName(),
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
    }
}
