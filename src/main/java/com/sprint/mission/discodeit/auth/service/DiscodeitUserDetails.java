package com.sprint.mission.discodeit.auth.service;

import com.sprint.mission.discodeit.dto.user.UserResponseDto;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Getter
@RequiredArgsConstructor
public class DiscodeitUserDetails implements UserDetails {

    private final UserResponseDto userResponseDto;
    private final String password;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + userResponseDto.role().name()));
    }

    @Override
    public String getUsername() {
        return userResponseDto.username();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DiscodeitUserDetails that)) {
            return false;
        }
        // 사용자 이름 비교
        return Objects.equals(userResponseDto.username(), that.userResponseDto.username());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(userResponseDto.username());
    }
}
