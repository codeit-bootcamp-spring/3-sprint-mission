package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.entity.dto.userStatus.UserStatusCreateRequestDto;
import com.sprint.mission.discodeit.entity.dto.userStatus.UserStatusUpdateRequestDto;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.UUID;

@Repository
@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "file")
public class FileUserStatusRepository extends AbstractFileRepository<UUID, UserStatus> implements UserStatusRepository {

    public FileUserStatusRepository(@Value("${discodeit.repository.file-directory}") String filePath) {
        super(filePath, "/userStatus.ser");
    }

    @Override
    public UserStatus createUserStatus(UserStatusCreateRequestDto userStatusCreateRequestDto) {
        UserStatus status = new UserStatus(userStatusCreateRequestDto.getUserId(), userStatusCreateRequestDto.getNowTime());
        return save(status.getId(), status);
    }

    @Override
    public UserStatus findStatusById(UUID statusId) {
        Map<UUID, UserStatus> userStatuses = loadFromFile();
        if (!userStatuses.containsKey(statusId)) {
            throw new NoSuchElementException("존재하지 않는 데이터입니다.");
        }
        return userStatuses.get(statusId);
    }

    @Override
    public List<UserStatus> findAllStatus() {
        return loadFromFile().values().stream().toList();
    }

    @Override
    public boolean updateUserStatus(UserStatusUpdateRequestDto userStatusUpdateRequestDto) {
        Map<UUID, UserStatus> userStatuses = loadFromFile();
        UserStatus userStatus = userStatuses.get(userStatusUpdateRequestDto.getUserStatusId());

        if (userStatus == null) {
            throw new NoSuchElementException("존재하지 않는 데이터입니다.");
        }

        userStatus.setLastActivityAt(userStatusUpdateRequestDto.getNowTime());
        saveToFile(userStatuses);
        return true;
    }

    @Override
    public UserStatus updateByUserId(UUID userId) {
        Map<UUID, UserStatus> userStatuses = loadFromFile();
        for (UserStatus userStatus : userStatuses.values()) {
            if (userStatus.getUserId().equals(userId)) {
                userStatus.setLastActivityAt(Instant.now());
                saveToFile(userStatuses);
                return userStatus;
            }
        }
        throw new NoSuchElementException("존재하지 않는 데이터입니다.");
    }

    @Override
    public boolean deleteUserStatus(UUID statusId) {
        Map<UUID, UserStatus> userStatuses = loadFromFile();
        if (userStatuses.containsKey(statusId)) {
            userStatuses.remove(statusId);
            saveToFile(userStatuses);
            return true;
        }
        return false;
    }
}
