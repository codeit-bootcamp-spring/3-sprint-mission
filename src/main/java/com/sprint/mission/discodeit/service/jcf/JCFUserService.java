package com.sprint.mission.discodeit.service.jcf;


import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;

import java.util.*;

/*
*
* [ ] 다음의 조건을 만족하는 서비스 인터페이스의 구현체를 작성하세요.
[ ] 클래스 패키지명: com.sprint.mission.discodeit.service.jcf
[ ] 클래스 네이밍 규칙: JCF[인터페이스 이름]
[ ] Java Collections Framework를 활용하여 데이터를 저장할 수 있는 필드(data)를 final로 선언하고 생성자에서 초기화하세요.
[ ] data 필드를 활용해 생성, 조회, 수정, 삭제하는 메소드를 구현하세요.
* */

public class JCFUserService implements UserService {
    private final Map<UUID, User> data; //database

    public JCFUserService() {
        this.data = new HashMap<>();
    }

    @Override
    public User create(User user) {
        this.data.put(user.getId(), user);

        return user;
    }

    @Override
    public User find(UUID userId) {
        User userNullable = this.data.get(userId);

        return Optional.ofNullable(userNullable).orElseThrow(() -> new NoSuchElementException("User with id " + userId + " not found"));
    }

    // TODO: 예외처리
    @Override
    public List<User> find(String name) {
        List<User> matchedUsers = new ArrayList<>();
        for (Map.Entry<UUID, User> entry : data.entrySet()) {
            if ((entry.getValue().getName()).equals(name)) {
                matchedUsers.add(entry.getValue());
            }
        }

        return matchedUsers;
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(this.data.values());
//        return this.data.values().stream().toList();
    }

    @Override
    public User update(UUID userId, String newName, int newAge, String newEmail, String newPassword) {
        User userNullable = this.data.get(userId);
        User user = Optional.ofNullable(userNullable).orElseThrow(() -> new NoSuchElementException("User with id " + userId + " not found"));
        user.update(newName, newAge, newEmail, newPassword);

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
