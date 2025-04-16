package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.UserService;

import java.util.List;
import java.util.UUID;

public class BasicUserService implements UserService {
    // 컴포지션
    private final UserRepository userRepository;

    // 생성자 주입 ( DI : 의존성 주입 ) << 저장 로직 기능 위임 패턴 적용 대상
    public BasicUserService(UserRepository userRepository) {
        if (userRepository == null) {
            throw new NullPointerException("userRepository is null");
        }
        this.userRepository = userRepository;
    }

    @Override
    public void createUser(User user) {

        // 유효성 체크
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }

        if (user.getUserName() == null || user.getUserName().isBlank()) {
            throw new IllegalArgumentException("Username cannot be null or blank");
        }

        // 저장 로직 기능 위임 ( 위임 패턴 활용 )
        userRepository.save(user);
    }

    @Override
    public User readUser(UUID id) {
        return userRepository.loadById(id);
    }

    @Override
    public List<User> readUserByName(String name) {
        return userRepository.loadByName(name);
    }

    @Override
    public List<User> readAllUsers() {
        return userRepository.loadAll();
    }

    @Override
    public User updateUser(UUID id, User user) {

        // 유효성 검사
        if (id == null) {
            throw new IllegalArgumentException("ID cannot be null");
        }
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }
        if (user.getUserName() == null || user.getUserName().isBlank()) {
            throw new IllegalArgumentException("Username cannot be null or empty");
        }

        // 덮어씌우기 위임
        userRepository.save(user);
        return user;
    }

    @Override
    public boolean deleteUser(UUID id) {

        if (id == null) {
            throw new IllegalArgumentException("ID cannot be null");
        }

        List<User> users = userRepository.loadAll();

        // 전체 중 해당 ID의 사용자 제거
        boolean removed = users.removeIf(user -> user.getUserId().equals(id));
        if (removed) {
            // 삭제 완료 시
            userRepository.saveAll(users);
            return true;
        }
        return false;
    }
}
