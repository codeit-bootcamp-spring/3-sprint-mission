package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

public class JCFUserService implements UserService {
    private final Map<UUID, User> data;

    public JCFUserService() {
        this.data = new HashMap<>();
    }

    // 등록
    @Override
    public User createUser(String name) {
        User user = new User(name);

        data.put(user.getId(), user);
        return user;
    }

    // 단건 조회
    @Override
    public User getUser(UUID id) {
        User userNullable = this.data.get(id);

        return Optional.ofNullable(userNullable)
                .orElseThrow(() -> new NoSuchElementException("User with id " + id + " not found"));
    }

    // 전체 조회
    @Override
    public List<User> getAllUsers() {
        return new ArrayList<>(data.values());
    }

    // 이름 수정
    @Override
    public User updateUser(User user, String newName) {
        user.updateName(newName);
        return user;
    }

    // 삭제
    @Override
    public User deleteUser(User user) {
        return data.remove(user.getId());
    }
}
