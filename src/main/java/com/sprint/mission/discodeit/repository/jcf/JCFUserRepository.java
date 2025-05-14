package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entitiy.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

@Repository
@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "JCF",matchIfMissing = true)
public class JCFUserRepository implements UserRepository {


    private final CopyOnWriteArrayList<User> data = new CopyOnWriteArrayList<>();

    @Override
    public User save(User user) {
        data.add(user);
        return user;
    }

    @Override
    public Boolean duplicateCheck(User user) {
        //user의 name이나 Email이 중복인지 검사
        if(data.stream().anyMatch((c) -> c.getUsername().equals(user.getUsername())) || data.stream().anyMatch((c) -> c.getEmail().equals(user.getEmail())))
            //중복이면 true
            return true;
        else
            //중복이 아니면 false return
            return false;
    }

    @Override
    public List<User> read() {
        return data;
    }

    @Override
    public Optional<User> readById(UUID id) {
        return data.stream()
                .filter(user -> user.getId().equals(id))
                .findAny();
    }

    @Override
    public void update(UUID id, User user) {
        data.stream()
                .filter(u -> u.getId().equals(id))
                .forEach(u-> {
                    u.setUpdatedAt(Instant.now());
                    u.setProfileId(user.getProfileId());
                    u.setEmail(user.getEmail());
                    u.setUsername(user.getUsername());
                    u.setPassword(user.getPassword());
                });
    }

    @Override
    public void delete(UUID userId) {
        data.removeIf(user -> user.getId().equals(userId));
    }

}
