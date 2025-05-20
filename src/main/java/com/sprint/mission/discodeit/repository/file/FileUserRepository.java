package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "file")
public class FileUserRepository extends AbstractFileRepository<UUID, User> implements UserRepository {


    public FileUserRepository(@Value("${discodeit.repository.file-directory}") String filePath) {
        super(filePath, "/user.ser");
    }

    @Override
    public User createUser(User user) {
        return save(user.getId(), user);
    }

    @Override
    public Optional<User> getUser(UUID id) {
        Map<UUID, User> users = loadFromFile();
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public List<User> getAllUsers() {
        Map<UUID, User> users = loadFromFile();
        return users.values().stream().toList();
    }

    @Override
    public boolean modifyUser(User user) {
        save(user.getId(), user);
        return true;
    }

    @Override
    public boolean deleteUser(UUID id) {
        Map<UUID, User> users = loadFromFile();

        users.remove(id);
        saveToFile(users);
        return true;
    }
}
