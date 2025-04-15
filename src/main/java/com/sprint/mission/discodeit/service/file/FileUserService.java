package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.util.FilePathUtil;
import com.sprint.mission.discodeit.entity.User;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


/**
 * packageName    : com.sprint.mission.discodeit.service.file
 * fileName       : FileUserService
 * author         : doungukkim
 * date           : 2025. 4. 14.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2025. 4. 14.        doungukkim       최초 생성
 */
public class FileUserService implements UserService {
    FilePathUtil filePathUtil =new FilePathUtil();


//    -------------------interface-------------------
//    UUID registerUser(String username);
//    User findUserById(UUID userId);
//    List<User> findAllUsers();
//    void updateUsername(UUID userId,String newName);
//    void deleteUser(UUID userId);
//    void addChannelInUser(UUID userId, UUID channelId); - TESTED BY FileChannelService.createChannel()
//    List<UUID> findChannelIdsInId(UUID userId); -NOT TESTED
//    -------------------------------------------------

    @Override
    public UUID registerUser(String username) {
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


    @Override
    public User findUserById(UUID userId) {
        Path path = filePathUtil.getUserFilePath(userId);
        if (!Files.exists(path)) {
            return null;
        }

        // 리스트를 전부 불러오기 때문에 비효율적임 리펙토링 필요함
        List<User> data = findAllUsers();
        return data.stream()
                .filter(user -> user.getId().equals(userId)).findAny().orElse(null);
    }


    @Override
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



    @Override
    public void updateUsername(UUID userId,String newName) {
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

    @Override
    public void deleteUser(UUID userId){
        Path path = filePathUtil.getUserFilePath(userId);
        // 파일 확인
        try {
            Files.delete(path);
            // ADD: DELETE USER IN CHANNEL
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // NOT TESTED BECAUSE OF NO EXISTING CHANNELS
    @Override
    public void addChannelInUser(UUID userId, UUID channelId) {
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

    // METHOD NEVER TESTED BECAUSE OF NO EXISTING CHANNELS
    @Override
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








//    // original
//    public void save(User user) {
//        try (
//                FileOutputStream fos = new FileOutputStream(filePathUtil.getUserFilePath(user.getId()).toFile());
//                ObjectOutputStream oos = new ObjectOutputStream(fos)
//        ) {
//            oos.writeObject(user);
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    // original
//    public List<User> load() {
//        Path userDirectory = filePathUtil.getUserDirectory();
//        if (Files.exists(userDirectory)) {
//            try {
//                List<User> list = Files.list(userDirectory)
//                        .filter(path -> path.toString().endsWith(".ser"))
//                        .map(path -> {
//                            try (
//                                    FileInputStream fis = new FileInputStream(path.toFile());
//                                    ObjectInputStream ois = new ObjectInputStream(fis)
//                            ) {
//                                Object data = ois.readObject();
//                                return (User) data;
//                            } catch (IOException | ClassNotFoundException exception) {
//                                throw new RuntimeException(exception);
//                            }
//                        })
//                        .toList();
//
//                return list;
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            }
//        } else {
//            return new ArrayList<>();
//        }
//    }
}





