package com.sprint.mission.discodeit.dto.data;

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
        System.out.println("[DiscodeitUserDetails] getAuthorities 실행");
        return List.of(
            new SimpleGrantedAuthority("ROLE_" + userDto.getRole())
        );
    }

    @Override
    public String getPassword() {
        System.out.println("[DiscodeitUserDetails] getPassword 실행");
        return password;
    }

    @Override
    public String getUsername() {
        System.out.println("[DiscodeitUserDetails] getUsername 실행");
        return userDto.getEmail();
    }

    /*세션 동일성을 보장하기 위해 equals(), hashcode() 메소드 오버라이딩*/
    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof DiscodeitUserDetails other)) {
            return false;
        }
        // userDto의 Id 같다고 인정
        return (Objects.equals(this.userDto.getId(),
            other.getUserDto().getId()));
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.userDto.getId(), this.userDto.getEmail());
    }
}
