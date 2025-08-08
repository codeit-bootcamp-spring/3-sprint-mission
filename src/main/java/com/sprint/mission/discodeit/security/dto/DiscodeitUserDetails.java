package com.sprint.mission.discodeit.security.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.entity.Role;
import java.util.Collection;
import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.CredentialsContainer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Getter
@RequiredArgsConstructor
public class DiscodeitUserDetails implements UserDetails, CredentialsContainer {

	private final UserDto userDto;                                // 응답/뷰에서 써도 안전한 정보
	@JsonIgnore
	private String password;

	public DiscodeitUserDetails(UserDto userDto, String password) {
		this.userDto = userDto;
		this.password = password;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		// role이 null이면 기본값 USER
		Role role = userDto.role() == null ? Role.USER : userDto.role();
		// Spring Security에서는 권한 앞에 ROLE_ prefix 붙이는 것이 관례
		return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
	}

	@Override
	@JsonIgnore
	public String getPassword() {
		return password;
	} // 사용자 Password 반환(Bcrypt로 인코딩됨)

	@Override
	public String getUsername() {
		return userDto.username();
	} // 사용자 ID

	@Override
	public boolean isAccountNonExpired() {
		return true;
	} // 계정만료 여부

	@Override
	public boolean isAccountNonLocked() {
		return true;
	} // 계정 NotBan 여부

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	} // Credential 유효기간 내 여부

	@Override
	public boolean isEnabled() {
		return true;
	} // 계정 유효 여부

	@Override
	public void eraseCredentials() {
		this.password = null;
	} // 선택: 인증 후 메모리에서 해시 지움
}
