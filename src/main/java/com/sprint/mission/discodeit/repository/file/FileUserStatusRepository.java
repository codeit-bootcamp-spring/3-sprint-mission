package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.util.FilePathUtil;
import com.sprint.mission.discodeit.util.FileSerializer;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.nio.file.Path;
import java.time.Instant;
import java.util.UUID;

/**
 * packageName    : com.sprint.mission.discodeit.repository.file
 * fileName       : FileUserStatusRepository
 * author         : doungukkim
 * date           : 2025. 4. 24.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2025. 4. 24.        doungukkim       최초 생성
 */
@Primary
@Repository
@RequiredArgsConstructor
public class FileUserStatusRepository implements UserStatusRepository {
    private final FilePathUtil filePathUtil;

    // 추가 필요
    @Override
    public Instant onlineTime(UUID userStatusId) {
        return null;
    }

    // 추가 필요
    @Override
    public boolean isOnline(UUID userStatusId) {
        return false;
    }

    @Override
    public UserStatus createUserStatus(UUID userId) {
        UserStatus userStatus = new UserStatus(userId);
        Path path = filePathUtil.getUserStatusFilePath(userStatus.getUserId());
        FileSerializer.writeFile(path, userStatus);
        return userStatus;
    }
}
