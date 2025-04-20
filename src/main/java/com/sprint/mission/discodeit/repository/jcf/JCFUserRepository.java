package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;

import java.util.*;

public class JCFUserRepository implements UserRepository {
    private final Map<UUID, User> storage = new HashMap<>();

    // 사용자 저장 (신규 생성 또는 수정 시 사용)
    @Override
    public void save(User user) {
        storage.put(user.getId(), user); // 동일한 ID가 있으면 덮어쓰기
    }

    // 사용자 조회
    @Override
    public User findById(UUID id) {
        return storage.get(id); // 해당 ID의 사용자 반환 (없으면 null)
    }

    // 모든 사용자 조회
    @Override
    public List<User> findAll() {
        return new ArrayList<>(storage.values()); // Map의 값들을 리스트로 변환
    }

    // 사용자 정보 업데이트
    @Override
    public void update(User user) {
        storage.put(user.getId(), user); // ID 기준으로 덮어쓰기
    }

    // 사용자 삭제
    @Override
    public void delete(UUID id) {
        storage.remove(id); // 해당 ID의 사용자 삭제
    }
}