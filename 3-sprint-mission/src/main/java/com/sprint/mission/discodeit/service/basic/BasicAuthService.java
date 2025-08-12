package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.RoleUpdateRequest;
import com.sprint.mission.discodeit.entity.DiscodeitUserDetails;
import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class BasicAuthService implements AuthService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final SessionRegistry sessionRegistry;

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    @Override
    public UserDto updateUserRole(RoleUpdateRequest request) {
        UUID userId = request.userId();
        Role newRole = request.newRole();

        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        user.updateRole(newRole);
        User updatedUser = userRepository.save(user);

        // 권한 변경 후 해당 사용자 세션 강제 만료
        expireUserSessions(userId);

        return userMapper.toDto(updatedUser);
    }

    public void expireUserSessions(UUID userId) {
        sessionRegistry.getAllPrincipals().forEach(principal -> {
            if (principal instanceof DiscodeitUserDetails usreDetails &&
                    usreDetails.getUserDto().id().equals(userId)) {

                sessionRegistry.getAllSessions(principal, false)
                        .forEach(sessionInfo -> sessionInfo.expireNow());
            }
        });
    }
}
