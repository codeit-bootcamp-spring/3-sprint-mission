package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.LoginRequest;
import com.sprint.mission.discodeit.dto.response.UserDto;
import com.sprint.mission.discodeit.entity.OnlineStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;
import java.util.logging.Logger;

@RequiredArgsConstructor
@Service
public class BasicAuthService implements AuthService {

    private static final Logger logger = Logger.getLogger(BasicAuthService.class.getName()); // 필드로 Logger 선언

    private  final UserStatusRepository fileUserStatusRepository;
    private final UserRepository userRepository;

    @Override
    public UserDto login(LoginRequest request) {

        String username = request.userName();
        String password = request.userPassword();

        Optional<User> userResult = userRepository.findUserByName(username);
        if(userResult.isEmpty()){
            logger.warning("해당하는 이름의 유저가 존재하지 않습니다: " + username);
            return null;
        }

        User user = userResult.get();

        if (!Objects.equals(user.getUserPassword(), password)){
            logger.warning("비밀번호가 일치하지 않습니다 ");
            return null;
        }

        // UserStatus 조회
        Optional<UserStatus> foundStatusResult = fileUserStatusRepository.findStatus(user.getId());
        OnlineStatus isOnline = foundStatusResult.map(UserStatus::getStatus)
                .orElse(OnlineStatus.Unknown);


        return new UserDto(
                user.getId(),
                user.getCreatedAt(),
                user.getUpdatedAt(),
                user.getName(),
                user.getUserEmail(),
                user.getProfileId(),
                isOnline
        );
    }
}

