package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.user.UserDto;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * PackageName  : com.sprint.mission.discodeit.service.basic
 * FileName     : CustomUserDetails
 * Author       : dounguk
 * Date         : 2025. 8. 5.
 */

@Getter
@RequiredArgsConstructor
public class DiscodeitUserDetails implements UserDetails {

    private static final String ROLE = "ROLE_";

    private final UserDto userDto;
    private final String password;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(ROLE + userDto.role()));
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return userDto.username();
    }



    public UserDto getUser() {
        return userDto;
    }

    public UUID getUserId() {
        return userDto.id();
    }

    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return UserDetails.super.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return UserDetails.super.isEnabled();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DiscodeitUserDetails that)) return false;
        // 사용자 이름 비교
        return Objects.equals(userDto.username(), that.userDto.username());
    }

    @Override
    public int hashCode() {
        return Objects.hash(userDto.username());
    }
}
