package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;

import java.util.*;

public class JCFUserService implements UserService { // 왜 abstract를 해야만 하는가?
    private final Map<UUID, User> data;


    public JCFUserService() {
        this.data = new HashMap<>(); // HashMap이 UUID로 검색 용이
    }

    @Override
    public User createUser(String RRN, String name, int age, String email) { // C
        User user = new User(RRN, name, age, email); // ID랑 user 저장
        this.data.put(user.getId(), user);

        return user;
    }

    @Override
    public User findById(UUID id) { // R
        User userNullable = this.data.get(id);

        return Optional.ofNullable(userNullable)
                .orElseThrow(() -> new NoSuchElementException( id + "ID를 가진 사용자를 찾을 수 없습니다."));
    }

    @Override
    public List<User> readAllUsers() {
        return this.data.values().stream().toList();
    }; // R 전체 조회

    @Override
    public User updateUser(UUID id, String newName, String newEmail) { // U
        User userNullable = this.data.get(id);
        User user = Optional.ofNullable(userNullable)
                .orElseThrow(() -> new NoSuchElementException(id + "ID를 가진 사용자를 찾을 수 없습니다."));
        user.updateUser(newName, newEmail);

        return user;
    }

    @Override
    public void deleteUser(UUID id) { // D
        if (!this.data.containsKey(id)) {
            throw new NoSuchElementException(id + "ID를 가진 사용자를 찾을 수 없습니다.");
        }
        this.data.remove(id);
    };
}
