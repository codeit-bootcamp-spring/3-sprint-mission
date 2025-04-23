package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;

import java.util.*;
import java.util.stream.Collectors;

// JCF 활용, 데이터를 저장할 수 있는 필드(data)를 final 로 선언, 생성자에서 초기화
// data 필드를 활용해 생성, 조회, 수정, 삭제하는 메소드를 구현( < 오버라이드? )
public class JCFUserService implements UserService {
    private final Map<UUID, User> data;

    public JCFUserService() {
        this.data = new HashMap<>();
    }

    @Override
    public User create(String userName, String pwd, String email, String phoneNumber, String StatusMessage) {
        User user = new User(userName, pwd, email, phoneNumber, StatusMessage);
        this.data.put(user.getUserId(), user);

        return user;
    }

    @Override
    public User find(UUID userId) {
        User userNullable = this.data.get(userId);

        return Optional.ofNullable(userNullable)
                .orElseThrow(() -> new NoSuchElementException("User with id " + userId + " not found"));
    }

    @Override
    public List<User> findAll() {
        return this.data.values().stream().toList();
    }

    @Override
    public User update(UUID id, String newUserName, String newPwd, String newEmail, String newPhoneNumber, String newStatusMessage) {
        User userNullable = this.data.get(id);
        User user = Optional.ofNullable(userNullable)
                .orElseThrow(() -> new NoSuchElementException("User with id " + id + " not found"));
        user.update(newUserName, newPwd ,newEmail, newPhoneNumber, newStatusMessage);

        return user;
    }

    @Override
    public void delete(UUID userId) {
        if (!this.data.containsKey(userId)) {
            throw new NoSuchElementException("User with id " + userId + " not found");
        }
        this.data.remove(userId);
    }
}