package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.dto.entity.BinaryContent;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.dto.entity.User;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository("jcfUserRepository")
public class JCFUserRepository implements UserRepository {
    private final Map<UUID, User> users = new HashMap<>();

    @Override
    public void save(User user) {
        users.put(user.getId(), user);
    }

    @Override
    public User loadByName(String name) {
        return users.values().stream()
                .filter(u -> u.getName().equals(name))
                .findFirst()
                .orElse(null);
    }

    @Override
    public User loadByEmail(String email) {
        return null;
    }

    @Override
    public User loadById(UUID id) {
        return users.get(id);
    }

    @Override
    public List<User> loadAll() {
        return users.values().stream().toList();
    }

//    @Override
//    public BinaryContent update(UUID userId, BinaryContentCreateRequest binaryContentCreateRequest) {
//        return null;
//    }

    @Override
    public void deleteById(UUID id) {
        if (!users.containsKey(id)) {
            throw new NoSuchElementException("[User] 유효하지 않은 사용자입니다. (" + id + ")");
        }

        users.remove(id);
    }
}
