package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "file")
public class FileUserStatusRepository extends AbstractFileRepository<UUID, UserStatus> implements UserStatusRepository {

    public FileUserStatusRepository(@Value("${discodeit.repository.file-directory}") String filePath) {
        super(filePath, "/userStatus.ser");
    }

    @Override
    public UserStatus createUserStatus(UserStatus userStatus) {
        return save(userStatus.getId(), userStatus);
    }

    @Override
    public Optional<UserStatus> findStatusById(UUID statusId) {
        return Optional.ofNullable(loadFromFile().get(statusId));
    }

    @Override
    public Optional<UserStatus> findByUserId(UUID userId) {
        return loadFromFile().values().stream()
                .filter(status -> status.getUserId().equals(userId))
                .findFirst();
    }


    @Override
    public List<UserStatus> findAllStatus() {
        return new ArrayList<>(loadFromFile().values());
    }

    @Override
    public boolean updateUserStatus(UserStatus userStatus) {
        Map<UUID, UserStatus> userStatuses = loadFromFile();

        userStatuses.put(userStatus.getId(), userStatus);
        saveToFile(userStatuses);
        return true;
    }

    @Override
    public boolean updateByUserId(UserStatus userStatus) {
        Map<UUID, UserStatus> userStatuses = loadFromFile();
        if (!userStatuses.containsKey(userStatus.getId())) {
            return false;
        }
        userStatuses.put(userStatus.getId(), userStatus);
        saveToFile(userStatuses);
        return true;
    }

    @Override
    public boolean deleteUserStatus(UUID statusId) {
        Map<UUID, UserStatus> userStatuses = loadFromFile();
        if (!userStatuses.containsKey(statusId)) {
            return false;
        }
        userStatuses.remove(statusId);
        saveToFile(userStatuses);
        return true;
    }
}
