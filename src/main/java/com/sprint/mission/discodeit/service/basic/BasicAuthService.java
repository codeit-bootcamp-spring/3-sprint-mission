package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.LoginRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.login.MissingPasswordException;
import com.sprint.mission.discodeit.exception.login.UserNotFoundForLoginException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class BasicAuthService implements AuthService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Transactional(readOnly = true)
    @Override
    public UserDto login(LoginRequest request) {
        log.info("[로그인] 요청: {}", request);

        String username = request.username();
        String password = request.password();

        //1. username 으로 user 찾기
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> {
                log.warn("[로그인 실패] 존재하지 않는 사용자: {}", username);
                return new UserNotFoundForLoginException(username);
            });

        //2. password 일치 여부 확인
        if (!user.getPassword().equals(password)) {
            log.warn("[로그인 실패] 비밀번호 불일치. 입력값: {}", password);
            throw new MissingPasswordException(password);
        }

        log.info("[로그인 성공] userId={}, username={}", user.getId(), user.getUsername());
        return userMapper.toDto(user);
    }
}
