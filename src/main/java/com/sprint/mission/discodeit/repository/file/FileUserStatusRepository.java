package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.util.FilePathUtil;
import com.sprint.mission.discodeit.util.FileSerializer;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Stream;

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
@Repository("fileUserStatusRepository")
@RequiredArgsConstructor
public class FileUserStatusRepository implements UserStatusRepository {
    private final FilePathUtil filePathUtil;

    // 추가 필요
    @Override
    public Instant onlineTime(UUID userStatusId) {
        return null;
    }


    @Override
    public boolean isOnline(UUID userStatusId) {
        Path path = filePathUtil.getUserStatusFilePath(userStatusId);

        if (!Files.exists(path)) {
            throw new RuntimeException("UserStatus 파일 없음: " + path.toAbsolutePath());
        }
        UserStatus userStatus = FileSerializer.readFile(path, UserStatus.class);
        Instant lastLoginTime = userStatus.getUpdatedAt();
        Duration duration = Duration.between(lastLoginTime, Instant.now());
        return duration.toMinutes() < 5;
    }

    @Override
    public UserStatus createUserStatus(UUID userId) {
        if (findUserStatusByUserId(userId) != null) {
            return findUserStatusByUserId(userId);
        }

        UserStatus userStatus = new UserStatus(userId);
        Path path = filePathUtil.getUserStatusFilePath(userStatus.getId());

        FileSerializer.writeFile(path, userStatus);
        return userStatus;
    }


    @Override
    public UserStatus findUserStatusByUserId(UUID userId) {
        Path directory = filePathUtil.getUserStatusDirectory();
        if (!Files.exists(directory)) {
            return null;
        }

        try (Stream<Path> paths = Files.list(directory)) {
            return paths
                    .filter(e -> e.toString().endsWith(".ser"))
                    .map(path -> {
                        try {
                            UserStatus userStatus = FileSerializer.readFile(path, UserStatus.class);
                            return userStatus;
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .filter(Objects::nonNull)
                    .filter(userStatus -> userStatus.getUserId().equals(userId))
                    .findFirst()
                    .orElse(null);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public List<UserStatus> findAllUserStatus() {
        Path directory = filePathUtil.getUserStatusDirectory();
        if (!Files.exists(directory)) {
            return new ArrayList<>();
        }

        try {
            return Files.list(directory)
                    .filter(path -> path.toString().endsWith(".ser"))
                    .map(path -> {
                        try (
                                FileInputStream fis = new FileInputStream(path.toFile());
                                ObjectInputStream ois = new ObjectInputStream(fis)
                        ) {
                            Object data = ois.readObject();
                            return (UserStatus) data;
                        } catch (IOException | ClassNotFoundException exception) {
                            throw new RuntimeException("파일을 읽어오지 못했습니다: FileUserStatusRepository.findAllUsers",exception);
                        }
                    }).toList();
        } catch (IOException e) {
            throw new RuntimeException("userState를 리스트로 만드는 과정에 문제 발생: FileUserStatusRepository.findAllUsers",e);
        }
    }

    @Override
    public void deleteUserStatusById(UUID userStatusId) {
        Objects.requireNonNull(userStatusId, "userStatus입력 없음: FileUserStatusRepository.deleteUserStatusById");
        Path path = filePathUtil.getUserStatusFilePath(userStatusId);

        if (!Files.exists(path)) {
            throw new RuntimeException("삭제 하려는 유저상태 없음");
        }

        try{
            Files.delete(path);
        } catch (IOException e) {
            throw new RuntimeException("삭제중 오류 발생"+e);
        }

    }
}
