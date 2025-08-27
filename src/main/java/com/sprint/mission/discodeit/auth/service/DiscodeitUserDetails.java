package com.sprint.mission.discodeit.auth.service;

import com.sprint.mission.discodeit.dto.response.UserDto;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * 사용자 세부 정보 구현체
 *
 * Spring Security의 UserDetails 인터페이스 구현체로 '로그인 인증' 및 '권한 검사' 등 모든 보안 로직의 '사용자 핵심 정보'를 보관하는 역할을 한다.
 * 여기서 핵심은 Security가 직접적으로 DB의 User 엔티티를 참조하지 않고, UserDetails라는 래퍼 객체로 감싼 후 인증을 수행한다.
 * 이제부터 인증(로그인) 성공 시, 해당 객체는 SecurityContext에 저장되고, 필요할 때마다 꺼내서 참조한다.
 */
@Getter
@RequiredArgsConstructor
public class DiscodeitUserDetails implements UserDetails {

    private final UserDto userDto;
    private final String password;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + userDto.role().name()));
    }

    // SpEL에서 접근하기 위함
    public UUID getId() {
        return userDto.id();
    }

    @Override
    public String getUsername() {
        return userDto.username();
    }

    /* 설명. 사용자 정보 비교 메서드로,
     *  equals()와 hashCode() 메서드를 재정의하여 사용자 정보 비교 시 사용된다.
     *  주로 세션 관리 시 사용되며, 세션 관리 시 사용자 정보 비교 시 사용된다.
     *  (https://docs.spring.io/spring-security/reference/servlet/authentication/session-management.html#ns-concurrent-sessions)
     * */
    @Override
    public boolean equals(Object o) {
        // 자기 자신과 비교
        if (this == o) return true;
        // 타입 비교
        if (!(o instanceof DiscodeitUserDetails that)) return false;
        // 사용자 이름 비교
        return Objects.equals(userDto.username(), userDto.username());
    }

    @Override
    public int hashCode() {
        // 사용자 이름 해시 코드 반환
        return Objects.hashCode(userDto.username());
    }
}
