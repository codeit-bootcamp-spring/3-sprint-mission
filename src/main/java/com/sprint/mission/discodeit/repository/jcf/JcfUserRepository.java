package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.util.*;

/**
 * packageName    : com.sprint.mission.discodeit.repository.jcf
 * fileName       : JcfUserRepository2
 * author         : doungukkim
 * date           : 2025. 4. 17.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2025. 4. 17.        doungukkim       최초 생성
 */
@Repository
@ConditionalOnProperty(name = "repository.mode", havingValue = "jcf")
public class JcfUserRepository implements UserRepository {

    Map<UUID, User> data = new HashMap<>();

    public User createUserByName(String username, String email, String password) {
        User user = new User(username, email, password);
        data.put(user.getId(), user);
        return user;
    }

    public User createUserByName(String username, String email, String password, UUID profileId) {
        User user = new User(username, email, password, profileId);
        data.put(user.getId(), user);
        return user;
    }

    @Override
    public void updateProfileIdById(UUID userId, UUID profileId) {
        User user = data.get(userId);
        user.setProfileId(profileId);
    }

    public User findUserById(UUID userId) {
        return data.get(userId);
    }

    public List<User> findAllUsers() {
        return data.values().stream().toList();
    }

    public void updateUserById(UUID userId, String name) {
        if (data.get(userId) == null) {
            throw new RuntimeException("파일 없음: JcfUserRepository.updateUserById");
        }
        data.get(userId).setUsername(name);
    }

    public void deleteUserById(UUID userId) {
        data.remove(userId);
    }

    @Override
    public boolean isUniqueUsername(String username) {
        List<User> list = data.values().stream().toList();
        if (list.isEmpty()) {
            return true;
        }
        for (User user : list) {
            if (user.getUsername().equals(username)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean isUniqueEmail(String email) {
        List<User> list = data.values().stream().toList();
        if (list.isEmpty()) {
            return true;
        }
        for (User user : list) {
            if (user.getEmail().equals(email)) {
                return false;
            }
        }
        return true;
    }
}
