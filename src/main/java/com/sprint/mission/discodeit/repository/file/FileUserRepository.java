package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
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
public class FileUserRepository implements UserRepository {
    private final Path path;

    public FileUserRepository(@Value("${storage.dirs.users}") String dir) {
        this.path = Paths.get(dir);
        clearFile();
    }

    @Override
    public void save(User user) {
        String filename = user.getId().toString() + ".ser";
        Path file = path.resolve(filename);

        try (
                OutputStream out = Files.newOutputStream(file);
                ObjectOutputStream oos = new ObjectOutputStream(out)
        ) {
            oos.writeObject(user);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public User loadByName(String name) {
        List<User> users = loadAll();

        for (User user : users) {
            if (user.getName().equals(name)) {
                return user;
            }
        }
        return null;
    }

    @Override
    public User loadByEmail(String email) {
        List<User> users = loadAll();

        for (User user : users) {
            if (user.getEmail().equals(email)) {
                return user;
            }
        }
        return null;
    }

    @Override
    public User loadById(UUID id) {
        Path file = path.resolve(id.toString() + ".ser");
        return deserialize(file);
    }

    @Override
    public List<User> loadAll() {
        List<User> users = new ArrayList<>();

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(path, "*.ser")) {
            for (Path p : stream) {
                users.add(deserialize(p));
            }
        } catch (IOException e) {
            throw new RuntimeException("[User] users 폴더 접근 실패", e);
        }

        return users;
    }

    @Override
    public void deleteById(UUID id) {
        try {
            Path deletePath = path.resolve(id + ".ser");
            Files.deleteIfExists(deletePath);

        } catch (IOException e) {
            throw new RuntimeException("[User] 파일 삭제 실패 (" + id + ")", e);
        }
    }

    private User deserialize(Path file) {
        if (Files.notExists(file)) {
            throw new IllegalArgumentException("[User] 유효하지 않은 파일");
        }

        try (
                InputStream in = Files.newInputStream(file);
                ObjectInputStream ois = new ObjectInputStream(in)
        ) {
            return (User) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("[User] User 파일 로드 실패", e);
        }
    }

    /**
     *  프로그램 시작 시
     *  users 폴더 초기화
     *  users 폴더가 비어있으면 초기화 안 하고 메소드 종료
     */
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