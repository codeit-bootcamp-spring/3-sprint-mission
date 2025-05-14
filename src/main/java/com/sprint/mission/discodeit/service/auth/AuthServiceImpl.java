package com.sprint.mission.discodeit.service.auth;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.dto.auth.LoginRequestDto;
import com.sprint.mission.discodeit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService{
    private final UserRepository userRepository;


    @Override
    public Optional<User> login(LoginRequestDto loginRequestDto) {
        List<User> allUsers = userRepository.getAllUsers();

        for (User user : allUsers) {
            if (user.getUserName().equalsIgnoreCase(loginRequestDto.getUsername()) && user.getPassword().equals(loginRequestDto.getPassword())) {
                return Optional.of(user);
            }
        }
        throw new IllegalArgumentException("id 또는 password가 일치하지 않습니다");
    }
}
