package com.sprint.mission.discodeit.service.auth;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.entity.dto.auth.LoginRequestDto;
import com.sprint.mission.discodeit.entity.dto.user.UserResponseDto;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService{

    private final UserRepository userRepository;
    private final UserStatusRepository userStatusRepository;

    @Override
    public UserResponseDto login(LoginRequestDto loginRequestDto) {
        List<User> allUsers = userRepository.getAllUsers();

        for (User user : allUsers) {
            if (user.getUserName().equalsIgnoreCase(loginRequestDto.getUsername()) && user.getPassword().equals(loginRequestDto.getPassword())) {
                UserStatus userStatus = userStatusRepository.findByUserId(user.getId()).orElseThrow(() -> new NoSuchElementException(("존재하지 않는 데이터 입니다.")));
                userStatus.update(Instant.now());
                userStatusRepository.updateUserStatus(userStatus);
                return createUserResponseDto(user);
            }
        }
        throw new IllegalArgumentException("id 또는 password가 일치하지 않습니다");
    }

    private UserResponseDto createUserResponseDto(User user) {
        boolean isOnline = userStatusRepository.findByUserId(user.getId())
                .map(UserStatus::isOnline)
                .orElse(false);

        return new UserResponseDto(
                user.getId(),
                user.getUserName(),
                isOnline,
                user.getProfileImage(),
                user.getEmail(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }
}
