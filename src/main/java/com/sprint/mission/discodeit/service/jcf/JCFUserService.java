package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;

import java.util.*;
import java.util.stream.Collectors;

public class JCFUserService implements UserService {
    private final Map<UUID, User> data = new HashMap<>();

    //----------- 사용자 생성 -----------
    @Override
    public User createUser(String name) {
        // 이름 중복 검사
        if (getUserByName(name) != null) {
            System.out.println("[User] 이름 중복 검사");
            System.out.println("[User] 이미 존재하는 사용자 이름입니다. (" + name + ")");
            return null;
        }

        User user = new User(name);
        data.put(user.getId(), user);
        return user;
    }

    //----------- 단일 사용자 조회 -----------
    @Override
    public User getUser(UUID id) {
        return data.get(id);
    }

    //----------- 모든 사용자 조회 -----------
    @Override
    public List<User> getAllUsers() {
        return data.values().stream().toList();
    }

    //----------- 채널명으로 채널 조회 -----------
    @Override
    public User getUserByName(String name) {
        return data.values().stream()
                .filter(user -> user.getUsername().equals(name))
                .findFirst()
                .orElse(null);
    }

    //----------- 사용자 이름 수정 -----------
    @Override
    public void updateUser(UUID id, String name) {
        User user = data.get(id);
        if (user != null) {
            user.update(name);
        }
    }

    //----------- 사용자 삭제 -----------
    @Override
    public void deleteUser(UUID id) {
        data.remove(id);
    }

    //----------- 사용자 검증 -----------
    @Override
    public boolean existsById(UUID id) {
        return data.containsKey(id);
    }
}
