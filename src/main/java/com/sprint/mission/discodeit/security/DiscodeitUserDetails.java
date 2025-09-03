package com.sprint.mission.discodeit.security;

import com.sprint.mission.discodeit.dto.data.UserDto;
import java.util.Collection;
import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * Spring Security UserDetails 구현체.
 *
 * <p>DTO(UserDto)를 기반으로 인증 정보(username, password, 권한)를 제공.</p>
 *
 * <p>권한(Role)은 "ROLE_" 접두사를 붙여 {@link SimpleGrantedAuthority}로 반환.</p>
 */
@EqualsAndHashCode(of = "userDto")
@Getter
@RequiredArgsConstructor
public class DiscodeitUserDetails implements UserDetails {

    private final UserDto userDto;
    private final String password;

    /**
     * 사용자의 권한 목록 반환.
     *
     * @return 권한 목록 (ROLE_ 접두사가 붙은 단일 권한)
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // UserDto의 role을 기반으로 SimpleGrantedAuthority 생성
        return List.of(new SimpleGrantedAuthority("ROLE_" + userDto.role().name()));
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return userDto.username();
    }
}
