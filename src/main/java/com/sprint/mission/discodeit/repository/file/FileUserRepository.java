package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.util.FilePathUtil;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * packageName    : com.sprint.mission.discodeit.repository.file
 * fileName       : FileUserRepository
 * author         : doungukkim
 * date           : 2025. 4. 15.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2025. 4. 15.        doungukkim       최초 생성
 */
public class FileUserRepository {
    FilePathUtil filePathUtil = new FilePathUtil();
    public UUID saveUser(String username) {
        User user = new User(username);

        // save in file
        try (
                FileOutputStream fos = new FileOutputStream(filePathUtil.getUserFilePath(user.getId()).toFile());
                ObjectOutputStream oos = new ObjectOutputStream(fos)
        ) {
            oos.writeObject(user);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return user.getId();
    }

    public User findUserById(Path path) {
        User user;

        if (Files.exists(path)) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path.toFile()))) {
                user = (User) ois.readObject();
                return user;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }

    public List<User> findAllUsers() {
        Path userDirectory = filePathUtil.getUserDirectory();
        if (Files.exists(userDirectory)) {
            try {
                List<User> list = Files.list(userDirectory)
                        .filter(path -> path.toString().endsWith(".ser"))
                        .map(path -> {
                            try (
                                    FileInputStream fis = new FileInputStream(path.toFile());
                                    ObjectInputStream ois = new ObjectInputStream(fis)
                            ) {
                                Object data = ois.readObject();
                                return (User) data;
                            } catch (IOException | ClassNotFoundException exception) {
                                throw new RuntimeException(exception);
                            }
                        })
                        .toList();

                return list;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            return new ArrayList<>();
        }
    }

    public void updateUsernameByIdAndName(UUID userId, String newName) {
        Path path = filePathUtil.getUserFilePath(userId);
        User user;

        // 파일 확인
        if (Files.exists(path)) {
            // 역직렬화
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path.toFile()))) {
                user = (User) ois.readObject();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            // 수정
            user.setUsername(newName);

            // 직렬화
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(path.toFile()))) {
                oos.writeObject(user);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void deleteUserById(UUID userId) {
        Path path = filePathUtil.getUserFilePath(userId);
        // 파일 확인
        try {
            Files.delete(path);
            // ADD: DELETE USER IN CHANNEL
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void addChannelInUserByIdAndChannelId(UUID userId, UUID channelId) {
        Path path = filePathUtil.getUserFilePath(userId);
        User user;
        if (Files.exists(path)) {
            try {
                ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path.toFile()));
                user = (User) ois.readObject();
            } catch (ClassNotFoundException | IOException e) {
                throw new RuntimeException(e);
            }

            List<UUID> channelIds = user.getChannelIds();
            channelIds.add(channelId);
            user.setChannelIds(channelIds);

            try{
                ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(path.toFile()));
                oos.writeObject(user);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public List<UUID> findChannelIdsInId(UUID userId) {
        Path path = filePathUtil.getUserFilePath(userId);
        User user;
        if (!Files.exists(path)) {
            return null;
        }
        try{
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path.toFile()));
            user = (User) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return user.getChannelIds();
    }

    public void deleteChannelIdInUser(UUID channelId, UUID userId) {
        Path path = filePathUtil.getUserFilePath(userId);
        User user;
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path.toFile()))) {
            user = (User) ois.readObject();
            user.getChannelIds().removeIf(id -> id.equals(channelId));
        } catch (ClassNotFoundException | IOException e) {
            throw new RuntimeException(e);
        }

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(path.toFile()))) {
            oos.writeObject(user);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
