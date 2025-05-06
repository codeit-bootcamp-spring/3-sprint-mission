package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserService;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class BasicUserService implements UserService {
    private final static BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
    private final UserRepository userRepository;
    private final UserStatusRepository userStatusRepository;
    private final BinaryContentRepository binaryContentRepository;

    public BasicUserService(UserRepository userRepository, UserStatusRepository userStatusRepository, Optional<BinaryContentRepository> binaryContentRepository) {
        this.userRepository = userRepository;
        this.userStatusRepository = userStatusRepository;
        this.binaryContentRepository = binaryContentRepository.orElse(null);

    }

    @Override
    public User create(String username, String email, String password, String name) throws IOException {
        if (!isDuplicated(username) && !isDuplicated(email)) {
            User newUser = new User(username, email, password, name);

            userRepository.save(newUser);
            return newUser;
        } else {
            throw new IllegalArgumentException("이미 존재하는 사용자 입니다.");
        }
    }

    // 아이디, 이메일 중복체크
    public boolean isDuplicated(String args) {
        return userRepository.findAll().stream()
                .anyMatch(u -> u.getUsername().equals(args) || u.getEmail().equals(args));
    }

    @Override
    public User find(UUID id) {
        if (userRepository.existsId(id)) {
            return userRepository.find(id);
        } else {
            throw new NoSuchElementException("해당 사용자가 존재하지 않습니다.");
        }

    }

    @Override
    public User findByUsername(String username) {
        if (userRepository.existsUsername(username)) {
            return userRepository.findByUsername(username);
        } else {
            throw new NoSuchElementException("해당 사용자가 존재하지 않습니다.");
        }
    }

    @Override
    public List<User> findByName(String name) {
        if (userRepository.existsName(name)) {
            return userRepository.findByName(name);
        } else {
            throw new NoSuchElementException("해당 사용자가 존재하지 않습니다.");
        }
    }

    @Override
    public User findByEmail(String email) {
        if (userRepository.existsEmail(email)) {
            return userRepository.findByEmail(email);
        } else {
            throw new NoSuchElementException("해당 사용자가 존재하지 않습니다.");
        }
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public void updateName(UUID id, String newName) throws IOException, IllegalAccessException {
        if (userRepository.existsId(id)) {
            User user = userRepository.find(id);
            user.updateName(newName);
            userRepository.save(user);
        } else if (!userRepository.existsId(id)) {
            throw new NoSuchElementException("해당 사용자가 존재하지 않습니다.");
        } else {
            throw new IllegalArgumentException("잘못된 접근입니다.");
        }

    }

    public boolean checkUser(UUID id) throws IllegalAccessException {
        if (userRepository.existsId(id)) {
            return true;
        } else if (!userRepository.existsId(id)) {
            throw new NoSuchElementException("해당 사용자가 존재하지 않습니다.");
        } else {
            throw new IllegalArgumentException("잘못된 접근입니다.");
        }
    }

    @Override
    public void updateEmail(UUID id, String newEmail) throws IOException, IllegalAccessException {
        if (userRepository.existsId(id)) {
            User user = userRepository.find(id);
            user.updateEmail(newEmail);
            userRepository.save(user);
        } else if (!userRepository.existsId(id)) {
            throw new NoSuchElementException("해당 사용자가 존재하지 않습니다.");
        } else {
            throw new IllegalArgumentException("잘못된 접근입니다.");
        }
    }

    @Override
    public void updatePassword(UUID id, String newPassword) throws IOException, IllegalAccessException {
        if (userRepository.existsId(id)) {
            User user = userRepository.find(id);
            user.updatePassword(newPassword);
            userRepository.save(user);
        } else if (!userRepository.existsId(id)) {
            throw new NoSuchElementException("해당 사용자가 존재하지 않습니다.");
        }

    }

    @Override
    public void updateProfile(UUID id, UUID newProfileId) throws IOException, IllegalAccessException {
        if (userRepository.existsId(id)) {
            User user = userRepository.find(id);
            user.updateProfile(newProfileId);
            userRepository.save(user);
        } else if (!userRepository.existsId(id)) {
            throw new NoSuchElementException("해당 사용자가 존재하지 않습니다.");
        }

    }

    @Override
    public void delete(UUID id) throws IOException, IllegalAccessException {
        if (userRepository.existsId(id)) {
            userRepository.delete(id);

            // 관련 도메인 삭제
            binaryContentRepository.deleteByUserId(id);
            userStatusRepository.deleteByUserId(id);

        } else if (!userRepository.existsId(id)) {
            throw new NoSuchElementException("해당 사용자가 존재하지 않습니다.");
        } else {
            throw new IllegalArgumentException("잘못된 접근입니다.");
        }
    }
}
