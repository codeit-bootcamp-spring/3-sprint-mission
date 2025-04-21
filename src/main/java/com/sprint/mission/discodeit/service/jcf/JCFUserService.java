package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.User; // User클래스 불러오기
import com.sprint.mission.discodeit.service.UserService; // 인터페이스 불러오기

import java.util.*;

public class JCFUserService implements UserService {

    // 데이터를 저장하는 공간 (Key는 UUID(유저ID), Value는 User객체)
    private final Map<UUID, User> data = new HashMap<>();

    @Override // 유저 등록
    public User create(User user) {
        data.put(user.getId(), user); // 유저의 ID를 Key로 해서 map에 저장
        return user;
    }

    @Override // 단일 유저 조회
    public User getById(UUID id) {
        return data.get(id);
    }

    @Override // 전체 유저 조회
    public List<User> getAll() {
        return new ArrayList<>(data.values());
    }

    @Override // 유저 정보 수정
    public User update(User user) {
        data.put(user.getId(), user);
        return user;
    }

    @Override // 유저 삭제
    public void delete(UUID id) {
        data.remove(id);
    }
}
