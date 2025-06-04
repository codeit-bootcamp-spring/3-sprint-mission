package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.annotation.Logging;
import com.sprint.mission.discodeit.dto.User.LoginRequest;
import com.sprint.mission.discodeit.dto.User.UserDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.AuthService;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Logging
@RequiredArgsConstructor
public class BasicAuthService implements AuthService {

    private final UserRepository userRepository;

    private final UserMapper userMapper;

    /**
     * 유저명, 패스워드가 일치하는 유저 인증
     *
     * @param loginRequest 로그인 요청 DTO
     * @return 인증된 유저
     * @throws NoSuchElementException 유저명/패스워드 불일치하는 경우
     */
    @Override
    @Transactional
    public UserDto login(LoginRequest loginRequest) {
        String username = loginRequest.username();
        String password = loginRequest.password();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 유저입니다."));

        if (!user.getPassword().equals(password)) {
            throw new NoSuchElementException("패스워드가 일치하지 않습니다.");
        }

        return userMapper.toDto(user);
    }
}
