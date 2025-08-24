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

/**
 * Spring Security의 UserDetails 인터페이스를 구현한 사용자 상세 정보 클래스입니다.
 * 
 * <p>애플리케이션의 사용자 정보를 Spring Security에서 사용할 수 있는 형태로 
 * 래핑하여 인증 및 권한 관리를 지원합니다.</p>
 * 
 * <p>주요 기능:</p>
 * <ul>
 *   <li>사용자 기본 정보 제공 (사용자명, 비밀번호)</li>
 *   <li>사용자 권한 정보 제공</li>
 *   <li>사용자 계정 상태 관리</li>
 *   <li>사용자 식별을 위한 equals/hashCode 구현</li>
 * </ul>
 * 
 * @author HuInDoL
 * @since 1.0.0
 * @see UserDetails
 */
@Getter
@RequiredArgsConstructor
public class DiscodeitUserDetails implements UserDetails {

    private final UserDto userDto;
    private final String password;

    /**
     * 사용자의 권한 정보를 반환합니다.
     * 
     * @return 사용자 권한 컬렉션
     */
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

    /**
     * 사용자 계정이 만료되지 않았는지 확인합니다.
     * 
     * @return 항상 true (만료되지 않음)
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * 사용자 계정이 잠기지 않았는지 확인합니다.
     * 
     * @return 항상 true (잠기지 않음)
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * 사용자 자격 증명이 만료되지 않았는지 확인합니다.
     * 
     * @return 항상 true (만료되지 않음)
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * 사용자 계정이 활성화되어 있는지 확인합니다.
     * 
     * @return 항상 true (활성화됨)
     */
    @Override
    public boolean isEnabled() {
        return true;
    }

    /**
     * 사용자 객체의 동등성을 비교합니다.
     * 
     * @param obj 비교할 객체
     * @return 동등한 경우 true, 그렇지 않으면 false
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;

        if (obj == null) return false;

        if (getClass() != obj.getClass()) return false;

        DiscodeitUserDetails other = (DiscodeitUserDetails) obj;
        return Objects.equals(userDto.id(), other.userDto.id()) &&
                Objects.equals(userDto.username(), other.userDto.username());
    }

    /**
     * 사용자 객체의 해시 코드를 반환합니다.
     * 
     * @return 해시 코드
     */
    @Override
    public int hashCode() {
        return Objects.hash(userDto.id(), userDto.username());
    }

    /**
     * 사용자 객체의 문자열 표현을 반환합니다.
     * 
     * @return 사용자 정보 문자열 (비밀번호는 보호됨)
     */
    @Override
    public String toString() {
        return "DiscodeitUserDetails(userDto=" + userDto + ", password=[PROTECTED])";
    }
}
