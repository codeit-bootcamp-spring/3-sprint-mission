package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.data.UserDto;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

@Getter
@RequiredArgsConstructor
public class DiscodeitUserDetails implements UserDetails {

    private final UserDto userDto;
    private final String password;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + userDto.role().name()));
    }

    @Override
    public String getUsername() {
        return userDto.username();
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;

        if (obj == null) return false;

        if (getClass() != obj.getClass()) return false;

        DiscodeitUserDetails other = (DiscodeitUserDetails) obj;
        return Objects.equals(userDto.id(), other.userDto.id()) &&
                Objects.equals(userDto.username(), other.userDto.username());
    }

    @Override
    public int hashCode() {
        return Objects.hash(userDto.id(), userDto.username());
    }

    @Override
    public String toString() {
        return "DiscodeitUserDetails(userDto=" + userDto + ", password=[PROTECTED])";
    }
}
