package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.helper.FilePathProperties;
import com.sprint.mission.discodeit.helper.FileSerializer;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.UUID;


/**
 * packageName    : com.sprint.mission.discodeit.refactor.repository.file fileName       :
 * FileUserRepository2 author         : doungukkim date           : 2025. 4. 17. description    :
 * =========================================================== DATE              AUTHOR NOTE
 * ----------------------------------------------------------- 2025. 4. 17.        doungukkim 최초 생성
 */
@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "file")
@Repository
@RequiredArgsConstructor
public class FileUserRepository implements UserRepository {

    private final FilePathProperties filePathProperties;

    @Override
    public User createUserByName(String name, String email, String password) {
        User user = new User(name, email, password);
        Path path = filePathProperties.getUserFilePath(user.getId());
        FileSerializer.writeFile(path, user);
        return user;
    }

    @Override
    public User createUserByName(String name, String email, String password, UUID profileId) {
        User user = new User(name, email, password, profileId);
        Path path = filePathProperties.getUserFilePath(user.getId());
        FileSerializer.writeFile(path, user);
        return user;
    }


    @Override
    public User findUserById(UUID userId) {
        Path path = filePathProperties.getUserFilePath(userId);

        if (!Files.exists(path)) {
            return null;
        }
        return FileSerializer.readFile(path, User.class);
    }


    @Override
    public List<User> findAllUsers() {
        Path directory = filePathProperties.getUserDirectory();

        if (!Files.exists(directory)) {
            return Collections.emptyList();
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
                            return (User) data;
                        } catch (IOException | ClassNotFoundException exception) {
                            throw new RuntimeException("파일을 읽어오지 못했습니다: FileUserRepository.findAllUsers",
                                    exception);
                        }
                    }).toList();
        } catch (IOException e) {
            throw new RuntimeException("유저들을 리스트로 만드는 과정에 문제 발생: FileChannelRepository.findAllUsers", e);
        }
    }

    @Override
    public void updateProfileIdById(UUID userId, UUID profileId) {
        Path path = filePathProperties.getUserFilePath(userId);
        if (!Files.exists(path)) {
            throw new IllegalStateException("no user in repository");
        }
        User user = FileSerializer.readFile(path, User.class);
        user.setProfileId(profileId);
        FileSerializer.writeFile(path, user);
    }

    @Override
    public void updateNameById(UUID userId, String name) {
        Path path = filePathProperties.getUserFilePath(userId);
        if (!Files.exists(path)) {
            throw new RuntimeException("파일 없음: fileUserRepository.updateUserById");
        }
        User user = FileSerializer.readFile(path, User.class);
        user.setUsername(name);
        FileSerializer.writeFile(path, user);
    }

    @Override
    public void updateEmailById(UUID userId, String email) {
        Path path = filePathProperties.getUserFilePath(userId);
        if (!Files.exists(path)) {
            throw new RuntimeException("파일 없음: fileUserRepository.updateEmailById");
        }
        User user = FileSerializer.readFile(path, User.class);
        user.setEmail(email);
        FileSerializer.writeFile(path, user);
    }

    @Override
    public void updatePasswordById(UUID userId, String password) {
        Path path = filePathProperties.getUserFilePath(userId);
        if (!Files.exists(path)) {
            throw new RuntimeException("파일 없음: fileUserRepository.updateEmailById");
        }
        User user = FileSerializer.readFile(path, User.class);
        user.setPassword(password);
        FileSerializer.writeFile(path, user);
    }

    @Override
    public void deleteUserById(UUID userId) {
        Path path = filePathProperties.getUserFilePath(userId);

        try {
            Files.delete(path);
        } catch (IOException e) {
            throw new RuntimeException("삭제중 오류 발생: FileUserRepository.deleteUserById", e);
        }
    }

    @Override
    public boolean isUniqueUsername(String username) {
        List<User> users = findAllUsers();
        if (users.isEmpty()) {
            return true;
        }
        for (User user : users) {
            if (user.getUsername().equals(username)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean isUniqueEmail(String email) {
        List<User> users = findAllUsers();
        if (users.isEmpty()) {
            return true;
        }

        for (User user : users) {
            if (user.getEmail().equals(email)) {
                return false;
            }
        }
        return true;
    }

    //        userRepository.hasSameName -> 모든 유저에서 이름이 같은 계정이 있는지 확인
//        userRepository.hasSameEmail -> 모든 유저에서 이메일이 같은 계정이 있는지 확인
//        or stream 으로 둘 다 같이 확인하고 하나라도 있으면 return false


    @Override
    public boolean hasSameName(String name) {
        Path directory = filePathProperties.getUserDirectory();

        if (!Files.exists(directory)) {
            return false;
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
                            return (User) data;
                        } catch (IOException | ClassNotFoundException exception) {
                            throw new RuntimeException("파일을 읽어오지 못했습니다: FileUserRepository.findAllUsers",
                                    exception);
                        }
                    })
                    .anyMatch(user -> user.getUsername().equals(name));
        } catch (IOException e) {
            throw new RuntimeException("유저들을 리스트로 만드는 과정에 문제 발생: FileChannelRepository.findAllUsers", e);
        }
    }

    @Override
    public boolean hasSameEmail(String email) {
        Path directory = filePathProperties.getUserDirectory();

        if (!Files.exists(directory)) {
            return false;
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
                            return (User) data;
                        } catch (IOException | ClassNotFoundException exception) {
                            throw new RuntimeException("파일을 읽어오지 못했습니다: FileUserRepository.findAllUsers",
                                    exception);
                        }
                    })
                    .anyMatch(user -> user.getEmail().equals(email));
        } catch (IOException e) {
            throw new RuntimeException("유저들을 리스트로 만드는 과정에 문제 발생: FileChannelRepository.findAllUsers", e);
        }
    }
}
