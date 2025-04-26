package com.sprint.mission.discodeit.repository.jcf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;

public class JCFUserRepository implements UserRepository {
    private static JCFUserRepository instance;
    private final Map<UUID, User> data = new HashMap<>();

    private JCFUserRepository() {}

    public static JCFUserRepository getInstance() {
        if (instance == null) {
            instance = new JCFUserRepository();
        }
        return instance;
    }

    // 테스트용 메서드
    public static void clearInstance() {
        if (instance != null) {
            instance.clearData();
            instance = null;
        }
    }

    // 데이터 초기화 메서드
    public void clearData() {
        data.clear();
    }

    @Override
    public User save(User user) {
        data.put(user.getUserId(), user);
        return user;
    }

    @Override
    public User findById(UUID userId) {
        return data.get(userId);
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(data.values());
    }

    @Override
    public void deleteById(UUID userId) {
        data.remove(userId);
    }
}
