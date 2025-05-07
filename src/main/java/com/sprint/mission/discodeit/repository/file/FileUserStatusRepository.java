package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserStatusRepository;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class FileUserStatusRepository implements UserStatusRepository {

    private final Path DIRECTORY;
    private final String EXTENSION = ".ser";

    public FileUserStatusRepository() { // 생성자에서 File의 이름과 Path를 생성하고 Directory 없으면 생성
        this.DIRECTORY = Paths.get(System.getProperty("userStatus.dir"), "file-data-map", UserStatus.class.getSimpleName());
        if (Files.notExists(DIRECTORY)) {
            try {
                Files.createDirectories(DIRECTORY);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private Path resolvePath(UUID id) {return DIRECTORY.resolve(id + EXTENSION);} // id만 있으면 EXTENSION을 합쳐 Path를 반환
    
    @Override
    public UserStatus save(UserStatus userStatus) {
        Path path = resolvePath(userStatus.getId());
        try (
                ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(path.toFile()));
                ) {
            oos.writeObject(userStatus);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        
        return userStatus;
    }

    @Override
    public Optional<UserStatus> findByUserId(UUID userId) {
        UserStatus userStatusNullable = null; // 초기화
        Path path = resolvePath(userId); // 경로 계산

        if (Files.exists(path)) { // 파일 존재유무
            try (
                    ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path.toFile()));
                    )
            {
                userStatusNullable = (UserStatus) ois.readObject(); // 파일에서 UserStatus 객체 read
                return Optional.ofNullable(userStatusNullable); // Optional 처리
            } catch (ClassNotFoundException | IOException e) {
                e.printStackTrace();
                return Optional.empty();
            }
        }

        return Optional.empty();
    }

    @Override
    public Optional<UserStatus> find(UUID id) {
        UserStatus userStatusNullable = null;
        Path path = resolvePath(id);
        if (Files.exists(path)) {
            try (
                    ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path.toFile()));
            )
            {
                userStatusNullable = (UserStatus) ois.readObject();
            } catch (ClassNotFoundException | IOException e) {
                throw new RuntimeException(e);
            }
        }

        return Optional.ofNullable(userStatusNullable);
    }

    @Override
    public List<UserStatus> findAll() {
        try {
            return Files.list(DIRECTORY)
                    .filter(path -> path.toString().endsWith(EXTENSION))
                    .map(path -> {
                        try (
                                ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path.toFile()));
                        ) {
                                    return (UserStatus) ois.readObject();
                        } catch (IOException | ClassNotFoundException e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .toList();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean existsByUserId(UUID userId) {
        Path path = resolvePath(userId);
        return Files.exists(path);
    }

    @Override
    public void delete(UUID userId) {
        Path path = resolvePath(userId);
        try {
            Files.delete(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
