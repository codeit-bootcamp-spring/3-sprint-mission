package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class BasicUserService implements UserService {

    private final UserRepository userRepository;

    // 저장 로직을 사용하기 위해 userRepository 주입 -> 관심사 분리
    public BasicUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void save(User user) {
        userRepository.save(user);
    }

    @Override
    public Optional<User> findById(UUID id) {
        return userRepository.findById(id);
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public User update(User user) {
        return userRepository.save(user);
    }

    @Override
    public void deleteById(UUID id) {
        userRepository.deleteById(id);
    }

    // 친구 추가 기능
    public void addFriend(User user1, User user2) {
        Optional<User> u1 = userRepository.findById(user1.getId());
        Optional<User> u2 = userRepository.findById(user2.getId());

        // 존재하지 않는 사용자인 경우 예외 처리
        if (u1.isEmpty() || u2.isEmpty()) {
            throw new IllegalArgumentException("존재하지 않는 사용자입니다.");
        }

        // 두 User 각각의 friendList에 추가
        if (!u1.get().getFriends().contains(u2.get().getId())) {
            u1.get().getFriends().add(u2.get().getId());
        }

        if (!u2.get().getFriends().contains(u1.get().getId())) {
            u2.get().getFriends().add(u1.get().getId());
        }

        // 변경사항 적용
        userRepository.save(u1.get());
        userRepository.save(u2.get());
    }

    // 친구 삭제 기능
    public void deleteFriend(User user1, User user2) {
        Optional<User> u1 = userRepository.findById(user1.getId());
        Optional<User> u2 = userRepository.findById(user2.getId());

        // 존재하지 않는 사용자인 경우 예외 처리
        if (u1.isEmpty() || u2.isEmpty()) {
            throw new IllegalArgumentException("존재하지 않는 사용자입니다.");
        }

        // 두 User 각각의 friendList에서 제거
        u1.get().getFriends().remove(u2.get().getId());
        u2.get().getFriends().remove(u1.get().getId());

        // 변경사항 적용
        userRepository.save(u1.get());
        userRepository.save(u2.get());
    }
}
