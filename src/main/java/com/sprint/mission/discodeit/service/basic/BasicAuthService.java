package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.RoleUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.security.service.SessionInvalidationService;
import com.sprint.mission.discodeit.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class BasicAuthService implements AuthService {

	private final UserRepository userRepository;
	private final SessionInvalidationService sessionInvalidationService;

	@Transactional
	@PreAuthorize("hasRole('ADMIN')")
	@Override
	public UserDto updateUserRole(RoleUpdateRequest request) {
		log.debug("사용자 권한 수정 시작: id={}, 새로운권한={}", request.userId(), request.newRole());
		// 사용자 조회
		User user = userRepository.findById(request.userId())
			.orElseThrow(() -> {
				UserNotFoundException exception = UserNotFoundException.withId(request.userId());
				return exception;
			});
		// Role 변경
		user.updateRole(request.newRole());
		userRepository.save(user);
		int expired = sessionInvalidationService.expireUserSessionsByUserId(user.getId());
		log.info("사용자 권한 수정 완료: id={}, 권한={}, 만료된세션={}", request.userId(), request.newRole(),
			expired);

		return UserDto.fromEntity(user);
	}
}
