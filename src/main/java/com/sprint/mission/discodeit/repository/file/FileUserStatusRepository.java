package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Repository
@Profile("file")
public class FileUserStatusRepository implements UserStatusRepository {
    private final Path path;

    public FileUserStatusRepository(@Value("${storage.dirs.userStatuses}") String dir) {
        this.path = Paths.get(dir);
        clearFile();
    }

    @Override
    public UserStatus save(UserStatus userStatus) {
        String filename = userStatus.getUserId().toString() + ".ser";
        Path file = path.resolve(filename);

        try (
                OutputStream out = Files.newOutputStream(file);
                ObjectOutputStream oos = new ObjectOutputStream(out)
        ) {
            oos.writeObject(userStatus);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return userStatus;
    }

    @Override
    public UserStatus loadById(UUID id) {
        Path file = path.resolve(id.toString() + ".ser");
        return deserialize(file);
    }

    @Override
    public List<UserStatus> loadAll() {
        List<UserStatus> userStatuses = new ArrayList<>();

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(path, "*.ser")) {
            for (Path p : stream) {
                userStatuses.add(deserialize(p));
            }
        } catch (IOException e) {
            throw new RuntimeException("[UserStatus] userStatus 폴더 접근 실패", e);
        }

        return userStatuses;
    }

    @Override
    public void deleteByUserId(UUID userId) {
        try {
            Path deletePath = path.resolve(userId + ".ser");
            Files.deleteIfExists(deletePath);

        } catch (IOException e) {
            throw new RuntimeException("[UserStatus] 파일 삭제 실패 (" + userId + ")", e);
        }
    }

    private UserStatus deserialize(Path file) {
        if (Files.notExists(file)) {
            throw new IllegalArgumentException("[UserStatus] 유효하지 않은 파일");
        }

        try (
                InputStream in = Files.newInputStream(file);
                ObjectInputStream ois = new ObjectInputStream(in)
        ) {
            return (UserStatus) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("[UserStatus] UserStatus 파일 로드 실패", e);
        }
    }

    private void clearFile() {
        try {
            if (Files.exists(path)) {
                try (DirectoryStream<Path> stream = Files.newDirectoryStream(path)) {
                    for (Path filePath : stream) {
                        Files.deleteIfExists(filePath);
                    }
                }
            } else {
                Files.createDirectories(path);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
