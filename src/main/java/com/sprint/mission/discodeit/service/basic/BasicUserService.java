package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;

import java.util.*;

public class BasicUserService implements UserService {
    private final Map<UUID, User> users = new HashMap<>();

    // 사용자 생성
    @Override
    public User create(String name) {
        User user = new User(name);                // 새 사용자 객체 생성
        users.put(user.getId(), user);             // Map에 저장
        return user;                               // 생성된 사용자 반환
    }

    // 사용자 조회
    @Override
    public User findById(UUID id) {
        return users.get(id);                      // 해당 ID의 사용자 반환 (없으면 null)
    }

    // 전체 사용자 조회
    @Override
    public List<User> findAll() {
        return new ArrayList<>(users.values());    // Map의 값들을 리스트로 변환
    }

    // 사용자 이름 수정
    @Override
    public User update(UUID id, String newName) {
        User user = users.get(id);                 // 기존 사용자 조회
        if (user != null) {
            user.setName(newName);                 // 이름 변경
            user.updateUpdatedAt();                // 수정 시간 갱신
        }
        return user;                               // 수정된 사용자 반환
    }

    // 사용자 삭제
    @Override
    public void delete(UUID id) {
        users.remove(id);                     // 해당 ID의 사용자 삭제
    }
}