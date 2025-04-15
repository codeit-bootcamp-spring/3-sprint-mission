package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;

import java.util.*;

public class JCFUserService implements UserService { // 왜 abstract를 해야만 하는가?
    private final HashMap<UUID, User> data;


    public JCFUserService() {
        this.data = new HashMap<>(); // HashMap이 UUID로 검색 용이
    }

    @Override
    public void createUser(User user) { // C
        data.put(user.getId(), user); // ID랑 user 저장
    }

    @Override
    public User readUser(UUID id) {
        return data.get(id);
    } // R

    @Override
    public List<User> readAllUsers() {
        return new ArrayList<>(data.values());
    }; // R 전체 조회

    @Override
    public void updateUser(UUID id, String newName) { // U
        User user = data.get(id);
        if (user != null) {
            user.updateName(newName);
            System.out.println("이름을 " + newName + "으로 수정했습니다.");
        } else {
            System.out.println("해당 사용자가 존재하지 않습니다.");
        }
    };

    @Override
    public void deleteUser(UUID id) { // D
        User user = data.get(id);
        data.remove(user.getId()); // ID를 불러와서 없앰
    };
}
