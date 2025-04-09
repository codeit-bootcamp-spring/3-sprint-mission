package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class JCFUserService implements UserService {
    private final Map<UUID, User> data = new HashMap<UUID, User>();

    // 등록
    @Override
    public User createUser(User user) {
        data.put(user.getId(), user);
        return user;
    }

    // 단건 조회
    @Override
    public User getUser(UUID id) {
        return data.get(id);
    }

    // 전체 조회
    @Override
    public List<User> getAllUsers() {
        return new ArrayList<>(data.values());
    }

    // 이름 수정
    @Override
    public User updateUser(User user, String newName) {
        if (newName != null && !newName.isEmpty()) {
            user.updateName(newName);
        }
        data.put(user.getId(), user);
        return user;
    }

    // 삭제
    @Override
    public User deleteUser(User user) {
        return data.remove(user.getId());
    }
}
