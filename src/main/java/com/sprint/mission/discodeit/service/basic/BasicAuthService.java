package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.LoginRequest;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.AuthService;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.exception.user.InvalidPasswordException;

/**
 * 사용자 인증(로그인) 기능을 제공하는 서비스 구현체입니다.
 *
 * <p>{@link AuthService} 인터페이스를 구현하며,
 * 사용자 이름과 비밀번호를 검증하여 로그인 처리를 수행합니다.</p>
 *
 * <p>비밀번호가 일치하지 않거나, 사용자명이 존재하지 않으면
 * 예외를 발생시킵니다.</p>
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class BasicAuthService implements AuthService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    private static final String SERVICE_NAME = "[AuthService] ";

    /**
     * 사용자 로그인 요청을 처리합니다.
     *
     * <p>지정된 사용자명이 존재하지 않으면 {@link NoSuchElementException} 을 발생시키며,
     * 비밀번호가 일치하지 않으면 {@link IllegalArgumentException} 을 발생시킵니다.</p>
     *
     * @param loginRequest 로그인 요청 정보 (사용자명, 비밀번호)
     * @return 로그인 성공 시 사용자 정보 DTO
     * @throws NoSuchElementException 사용자명이 존재하지 않을 경우
     * @throws IllegalArgumentException 비밀번호가 일치하지 않을 경우
     */
    @Override
    @Transactional
    public UserDto login(LoginRequest loginRequest) {
        long startTime = System.currentTimeMillis();
        log.info(SERVICE_NAME + "로그인 처리 시작: username={}", loginRequest.username());

        String username = loginRequest.username();
        String password = loginRequest.password();

        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> {
                    log.error(SERVICE_NAME + "로그인 실패 - 사용자 없음: username={}", username);
                    return new UserNotFoundException("해당 사용자를 찾을 수 없습니다.");
                });
        if (!user.getPassword().equals(password)) {
            log.error(SERVICE_NAME + "로그인 실패 - 비밀번호 불일치: username={}", username);
            throw new InvalidPasswordException("비밀번호가 일치하지 않습니다.");
        }

        UserDto userDto = userMapper.toDto(user);

        long endTime = System.currentTimeMillis();
        log.info(SERVICE_NAME + "로그인 성공: {}", userDto);
        log.info(SERVICE_NAME + "로그인 처리 시간: {} ms", endTime - startTime);

        return userDto;
    }
}
