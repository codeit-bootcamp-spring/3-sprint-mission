package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.dto.request.RoleUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public UserDto updateRole(RoleUpdateRequest request) {
        User user = userRepository.findById(request.userId())
                .orElseThrow(() -> UserNotFoundException.withId(request.userId()));
        user.updateRole(request.newRole());
        return userMapper.toDto(user);
    }
}
