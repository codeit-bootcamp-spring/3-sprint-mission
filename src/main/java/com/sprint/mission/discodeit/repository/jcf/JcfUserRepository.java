package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;

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
public class JcfUserRepository implements UserRepository {

    Map<UUID, User> data = new HashMap<>();

    public User createUserByName(String name) {
        User user = new User(name);
        data.put(user.getId(), user);
        return user;
    }
    public User findUserById(UUID userId) {
        return data.get(userId);
    }

    public List<User> findAllUsers() {
        return data.values().stream().toList();
    }

    public void updateUserById(UUID userId, String name) {
        if (data.get(userId) == null) {
            throw new RuntimeException("파일 없음: JcfChannelRepository.updateChannel");
        }
        data.get(userId).setUsername(name);
    }

    public void deleteUserById(UUID userId) {
        data.remove(userId);
    }
}
