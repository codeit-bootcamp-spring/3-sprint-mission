package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;

import java.util.*;

public class JCFUserService implements UserService {
    // 사용자 데이터를 저장하는 Map
    private final Map<UUID, User> data;

    // 생성자: 데이터 저장용 Map 초기화
    public JCFUserService() {
        this.data = new HashMap<>();
    }

    // 사용자 생성 및 저장
    @Override
    public User create(String username) {
        User user = new User(username);
        data.put(user.getId(), user);
        return user;
    }

    // 사용자 이름 수정
    @Override
    public void update(UUID id, String newUsername) {
        User user = data.get(id);
        if (user != null) {
            user.updateUsername(newUsername);
        }
    }

    // 사용자 목록 조회
    @Override
    public List<User> findAll() {
        return new ArrayList<>(data.values());
    }
    // ID로 사용자 조회
    @Override
    public User findById(UUID id) {
        return data.get(id);
    }




    // 사용자 삭제
    @Override
    public void delete(UUID id) {
        data.remove(id);
    }
}
