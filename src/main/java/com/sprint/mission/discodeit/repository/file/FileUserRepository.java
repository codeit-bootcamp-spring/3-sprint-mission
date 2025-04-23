package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.util.FilePathUtil;
import com.sprint.mission.discodeit.util.FileSerializer;
import org.springframework.stereotype.Repository;

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
@Repository
public class FileUserRepository implements UserRepository {
    FilePathUtil filePathUtil = new FilePathUtil();
    FileSerializer fileSerializer = new FileSerializer();


    @Override
    public User createUserByName(String name) {
        User user = new User(name);
        Path path = filePathUtil.getUserFilePath(user.getId());
        fileSerializer.writeFile(path, user);
        return user;
    }

    @Override
    public User findUserById(UUID userId) {
        Path path = filePathUtil.getUserFilePath(userId);
        return fileSerializer.readFile(path, User.class);
    }

    @Override
    public List<User> findAllUsers() {
        Path directory = filePathUtil.getUserDirectory();

        if (!Files.exists(directory)) {
            return new ArrayList<>();
        }

        try {
            List<User> list = Files.list(directory)
                    .filter(path -> path.toString().endsWith(".ser"))
                    .map(path -> {
                        try (
                                FileInputStream fis = new FileInputStream(path.toFile());
                                ObjectInputStream ois = new ObjectInputStream(fis)
                        ) {
                            Object data = ois.readObject();
                            return (User) data;
                        } catch (IOException | ClassNotFoundException exception) {
                            throw new RuntimeException("파일을 읽어오지 못했습니다: FileUserRepository.findAllUsers",exception);
                        }
                    }).toList();
            return list;
        } catch (IOException e) {
            throw new RuntimeException("유저들을 리스트로 만드는 과정에 문제 발생: FileChannelRepository.findAllUsers",e);
        }
    }

    @Override
    public void updateUserById(UUID userId, String name) {
        Path path = filePathUtil.getUserFilePath(userId);
        if (!Files.exists(path)) {
            throw new RuntimeException("파일 없음: fileUserRepository.updateUserById");
        }
        User user = fileSerializer.readFile(path, User.class);
        user.setUsername(name);
        fileSerializer.writeFile(path, user);
    }

    @Override
    public void deleteUserById(UUID userId) {
        Path path = filePathUtil.getUserFilePath(userId);
        try{
            Files.delete(path);
        } catch (IOException e) {
            throw new RuntimeException("삭제중 오류 발생: FileUserRepository.deleteUserById", e);
        }
    }
}
