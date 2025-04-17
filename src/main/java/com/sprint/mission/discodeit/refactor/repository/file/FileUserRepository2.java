package com.sprint.mission.discodeit.refactor.repository.file;

import com.sprint.mission.discodeit.refactor.entity.User2;
import com.sprint.mission.discodeit.refactor.repository.UserRepository2;
import com.sprint.mission.discodeit.util.FilePathUtil;
import com.sprint.mission.discodeit.util.FileSerializer;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * packageName    : com.sprint.mission.discodeit.refactor.repository.file
 * fileName       : FileUserRepository2
 * author         : doungukkim
 * date           : 2025. 4. 17.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2025. 4. 17.        doungukkim       최초 생성
 */
public class FileUserRepository2 implements UserRepository2 {
    FilePathUtil filePathUtil = new FilePathUtil();
    FileSerializer fileSerializer = new FileSerializer();


    @Override
    public User2 createUserByName(String name) {
        User2 user = new User2(name);
        Path path = filePathUtil.getUserFilePath(user.getId());
        fileSerializer.writeFile(path, user);
        return user;
    }

    @Override
    public User2 findUserById(UUID userId) {
        Path path = filePathUtil.getUserFilePath(userId);
        return fileSerializer.readFile(path,User2.class);
    }

    @Override
    public List<User2> findAllUsers() {
        Path directory = filePathUtil.getUserDirectory();

        if (!Files.exists(directory)) {
            return new ArrayList<>();
        }

        try {
            List<User2> list = Files.list(directory)
                    .filter(path -> path.toString().endsWith(".ser"))
                    .map(path -> {
                        try (
                                FileInputStream fis = new FileInputStream(path.toFile());
                                ObjectInputStream ois = new ObjectInputStream(fis)
                        ) {
                            Object data = ois.readObject();
                            return (User2) data;
                        } catch (IOException | ClassNotFoundException exception) {
                            throw new RuntimeException(exception);
                        }
                    }).toList();
            return list;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void updateUserById(UUID userId, String name) {
        Path path = filePathUtil.getUserFilePath(userId);
        User2 user = fileSerializer.readFile(path, User2.class);

        fileSerializer.writeFile(path, user);
    }

    @Override
    public void deleteUserById(UUID userId) {
        Path path = filePathUtil.getUserFilePath(userId);
        try{
            Files.delete(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
