package com.sprint.mission.discodeit.dto.data;

import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.entity.User;
import java.util.UUID;

public record UserDto(
	UUID id,
	String username,
	String email,
	BinaryContentDto profile,
	Boolean online,
	Role role
) {

	// 엔티티 → DTO 변환 (online은 status 없으면 false로, role은 기본 USER)
	public static UserDto fromEntity(User user) {
		if (user == null) {
			return null;
		}
		return new UserDto(
			user.getId(),
			user.getUsername(),
			user.getEmail(),
			BinaryContentDto.fromEntity(user.getProfile()),
			false,
			user.getRole()
		);
	}

	// 로그인 인증용 factory
	public static UserDto forAuth(User user) {
		return new UserDto(
			user.getId(),
			user.getUsername(),
			user.getEmail(),
			null,                 // profile 건드리지 않음 (LAZY 안전)
			false,                // online 계산 안 함
			user.getRole()
		);
	}

	// 유저 온라인 확인용 메서드
	public UserDto withOnline(Boolean online) {
		return new UserDto(id, username, email, profile, online, role);
	}
}
