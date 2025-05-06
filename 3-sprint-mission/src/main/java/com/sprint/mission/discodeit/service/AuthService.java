package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.file.FileUserRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFUserRepository;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
public class AuthService {
    private static final BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
    private final UserRepository userRepository;

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User login(String username, String password) throws IOException, IllegalAccessException {

        if (userRepository.existsUsername(username)) {
            User user = userRepository.findByUsername(username);

            if (user.getPassword().equals(password)) {

                return user;
            } else {

                throw new IllegalAccessException("비밀번호가 틀립니다.");
            }
        } else {

            throw new NoSuchElementException("존재하지 않는 사용자입니다.");
        }

    }

}
