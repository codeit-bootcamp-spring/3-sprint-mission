package com.sprint.mission.discodeit.security;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import com.sprint.mission.discodeit.dto.data.UserDto;

import java.util.Collection;
import java.util.List;

@Getter
@RequiredArgsConstructor
public class DiscodeitUserDetails implements UserDetails {
    private final UserDto userDto;   // com.sprint.mission.discodeit.dto.data.UserDto (record)
    private final String password;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // 권한 검증 없다면 빈 리스트
        return List.of();
    }

    @Override
    public String getUsername() {     // record 접근자 사용
        return userDto.username();
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override public boolean isAccountNonExpired()  { return true; }
    @Override public boolean isAccountNonLocked()   { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled()            { return true; }
}