package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.RoleUpdateRequest;
import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.security.SessionManager;
import com.sprint.mission.discodeit.service.AuthService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class BasicAuthService implements AuthService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final SessionManager sessionManager;

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    @Override
    public UserDto updateRole(RoleUpdateRequest request) {
        return updateRoleInternal(request);
    }

    /**
     * 내부적으로 사용자의 권한을 변경한다.
     *
     * <p>권한 변경 후 해당 사용자의 기존 세션을 모두 무효화한다.</p>
     *
     * @param request 권한 변경 요청 (사용자 ID와 새로운 권한 포함)
     * @return 변경된 사용자 정보를 담은 DTO
     * @throws UserNotFoundException 지정된 ID의 사용자가 없는 경우
     */
    @Transactional
    @Override
    public UserDto updateRoleInternal(RoleUpdateRequest request) {
        UUID userId = request.userId();
        User user = userRepository.findById(userId)
            .orElseThrow(() -> UserNotFoundException.withId(userId));

        Role newRole = request.newRole();
        user.updateRole(newRole);

        // 변경된 권한 반영을 위해 기존 세션 무효화
        sessionManager.invalidateSessionsByUserId(userId);

        return userMapper.toDto(user);
    }

}
