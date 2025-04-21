package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;

import java.util.*;

// Java Collections Framework 기반 사용자 서비스 구현 클래스
public class JCFUserService implements UserService {

    // 사용자 데이터를 저장할 Map (key: UUID, value: User)
    private final Map<UUID, User> data = new HashMap<>();

    @Override
    public User create(String name) {
        // 새로운 사용자 생성 후 저장소에 추가
        User user = new User(name);
        data.put(user.getId(), user);
        return user;
    }

    @Override
    public User findById(UUID id) {
        // 주어진 ID에 해당하는 사용자 반환 (없으면 null)
        return data.get(id);
    }

    @Override
    public List<User> findAll() {
        // 저장된 모든 사용자 리스트로 반환
        return new ArrayList<>(data.values());
    }

    @Override
    public User update(UUID id, String newName) {
        // ID로 사용자를 찾아 이름을 변경
        User user = data.get(id);
        if (user != null) {
            user.updateName(newName);
        }
        return user; // null 또는 수정된 사용자 반환
    }

    @Override
    public void delete(UUID id) {
        // ID에 해당하는 사용자 삭제
        data.remove(id);
    }
}
