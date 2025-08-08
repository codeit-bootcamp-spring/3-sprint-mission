package com.sprint.mission.discodeit.auth.service;

import com.sprint.mission.discodeit.dto.data.UserDto;
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

    private final UserDto userDto;
    private final String password;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        String authorities = "ROLE_" + userDto.role().name();
        return List.of(new SimpleGrantedAuthority(authorities));
    }

    @Override
    public String getUsername() {
        return userDto.username();
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
        if (this == o) return true;
        if (!(o instanceof DiscodeitUserDetails that)) return false;
        return Objects.equals(this.userDto.id(), that.userDto.id());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.userDto.id());
    }
}
