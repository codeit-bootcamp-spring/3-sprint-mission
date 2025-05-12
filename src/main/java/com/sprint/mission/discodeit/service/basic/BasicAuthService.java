package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.User.UserLoginRequestDTO;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class BasicAuthService implements AuthService {

    private final UserRepository userRepository;

    /**
     * 유저명, 패스워드가 일치하는 유저를 리턴
     *
     * @param loginRequestDTO 로그인 정보
     * @return 인증된 유저
     * @throws NoSuchElementException 유저명/패스워드 불일치하는 경우
     */
    @Override
    public User login(UserLoginRequestDTO loginRequestDTO) {
        String userName = loginRequestDTO.userName();
        String password = loginRequestDTO.password();

        User user = userRepository.findByUserName(userName)
                .orElseThrow(()-> new NoSuchElementException("일치하는 유저가 없습니다."));

        // 패스워드 일치 확인
        if (!user.getPassword().equals(password)) {
            throw new NoSuchElementException("패스워드가 일치하지 않습니다.");
        }
        return user;
    }
}
