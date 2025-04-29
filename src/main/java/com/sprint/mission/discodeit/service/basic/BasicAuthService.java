package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.LoginRequest;
import com.sprint.mission.discodeit.dto.UserResponse;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BasicAuthService implements AuthService {
    private final UserRepository userRepository;
//    private final UserStatusRepository userStatusRepository;

    @Override
    public UserResponse login(LoginRequest loginRequest) throws Exception {
        Optional<User> userNullable = this.userRepository.findAll().stream().filter((user) ->
                user.getEmail().equals(loginRequest.email())
        ).findFirst();
        User user = userNullable.orElseThrow(() -> new NoSuchElementException("User with email " + loginRequest.email() + " not found"));

        if (!user.getPassword().equals(loginRequest.password())) {
            throw new Exception("이메일 또는 비밀번호가 틀렸습니다.");
        }

        //TODO : 레포 만들고 실제값 넣어야함
//        UserStatus userStatus = UserStatusRepository.findByUserId(user.getId())
        return new UserResponse(user.getId(), user.getName(), user.getEmail(), user.getProfileId(), new UserStatus(user.getId()));
    }
}
