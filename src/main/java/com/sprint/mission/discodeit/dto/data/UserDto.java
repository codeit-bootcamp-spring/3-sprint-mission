package com.sprint.mission.discodeit.dto.data;

import com.sprint.mission.discodeit.entity.User;
import java.util.List;
import java.util.UUID;

public record UserDto(
	UUID id,
	String username,
	String email,
	BinaryContentDto profile,
	Boolean online,
	List<String> roles
) {

	// 엔티티 → DTO 변환 (online은 status 없으면 false로, roles는 기본 ROLE_USER)
	public static UserDto fromEntity(User user) {
		if (user == null) {
			return null;
		}
		return new UserDto(
			user.getId(),
			user.getUsername(),
			user.getEmail(),
			BinaryContentDto.fromEntity(user.getProfile()),
			user.getStatus() != null && Boolean.TRUE.equals(user.getStatus().isOnline()),
			List.of("ROLE_USER")
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
			List.of("ROLE_USER")  // 권한 기본값
		);
	}
}
