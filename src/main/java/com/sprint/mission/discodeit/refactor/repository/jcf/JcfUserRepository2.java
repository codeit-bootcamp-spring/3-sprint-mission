package com.sprint.mission.discodeit.refactor.repository.jcf;

import com.sprint.mission.discodeit.refactor.entity.User2;
import com.sprint.mission.discodeit.refactor.repository.UserRepository2;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * packageName    : com.sprint.mission.discodeit.refactor.repository.jcf
 * fileName       : JcfUserRepository2
 * author         : doungukkim
 * date           : 2025. 4. 17.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2025. 4. 17.        doungukkim       최초 생성
 */
public class JcfUserRepository2 implements UserRepository2 {

    Map<UUID, User2> data = new HashMap<>();

    public User2 createUserByName(String name) {
        User2 user = new User2(name);
        data.put(user.getId(), user);
        return user;
    }
    public User2 findUserById(UUID userId) {
        return data.get(userId);
    }

    public List<User2> findAllUsers() {
        return data.values().stream().toList();
    }

    public void updateUserById(UUID userId, String name) {
        data.get(userId).setUsername(name);
    }

    public void deleteUserById(UUID userId) {
        data.remove(userId);
    }
}
