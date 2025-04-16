package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;

import java.util.*;

public class JCFUserRepository implements UserRepository {

    // JCF 저장로직 ( 기존 Map 채택 >> map.put()기능 )
    private final Map<UUID, User> userMap = new HashMap<>();


    @Override
    public void save(User user) {
        userMap.put(user.getUserId(), user);
    }

    @Override
    public void saveAll(List<User> users) {
        // 현재 목록 초기화
        userMap.clear();
        // 새로 저장
        for (User user : users) {
            userMap.put(user.getUserId(), user);
        }
    }

    @Override
    public List<User> loadAll() {
        return new ArrayList<>(userMap.values());
    }

    @Override
    public User loadById(UUID id) {
        return userMap.get(id);
    }

    @Override
    public List<User> loadByName(String name) {
        List<User> result = new ArrayList<>();
        for (User user : userMap.values()) {
            if (user.getUserName().equals(name)) {
                result.add(user);
            }
        }
        return result;
    }
}
