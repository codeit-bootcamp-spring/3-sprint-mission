package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.LoginRequest;
import com.sprint.mission.discodeit.entitiy.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BasicAuthService implements AuthService {

    UserRepository userRepository;

    @Override
    public User login(LoginRequest request) {
        List<User> userList = userRepository.read();
        Optional<User> user = userList.stream()
                .filter(u -> u.getUsername().equals(request.username()) && u.getPassword().equals(request.password()))
                .findAny();
        try {
            if (user.isPresent()) {
                return user.get();
            } else
                throw new ClassNotFoundException();
        } catch (ClassNotFoundException e) {
            System.out.println("일치하지 않는 아이디 또는 비밀번호 입니다.");;
        }
    }
}
