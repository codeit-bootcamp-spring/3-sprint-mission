package com.sprint.mission.discodeit.security.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.entity.Role;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
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
		// Spring 표준 'ROLE_*' 형태로 String변환
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

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof DiscodeitUserDetails that)) {
			return false;
		}

		// 1) PK 우선 비교 (예: Long/UUID) — 동일 인물 보장
		Object thisKey = userDto.id();         // ★ UserDto에 id()가 있다고 가정 (없으면 uuid() 등 프로젝트 PK로 변경)
		Object thatKey = that.userDto.id();

		if (thisKey != null && thatKey != null) {
			return Objects.equals(thisKey, thatKey);
		}

		// 2) PK가 아직 없는(미영속) 상황 대비: username 정규화로 임시 비교
		String thisName = this.getUsername() == null ? null : this.getUsername().toLowerCase();
		String thatName = that.getUsername() == null ? null : that.getUsername().toLowerCase();
		return Objects.equals(thisName, thatName);
	}

	@Override
	public int hashCode() {
		Object key = userDto.id();             // ★ PK가 있으면 그걸로 해시
		if (key != null) {
			return Objects.hash(key);
		}

		String name = this.getUsername() == null ? null : this.getUsername().toLowerCase();
		return Objects.hash(name);             // ★ fallback: username 기반 해시
	}
}
