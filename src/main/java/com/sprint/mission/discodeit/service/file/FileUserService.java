package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class FileUserService implements UserService {
    private final Path databasePath;

    public FileUserService() {

        try {
            //  현재디렉토리/data/userDB 디렉토리를 저장할 path로 설정
            this.databasePath = Paths.get(System.getProperty("user.dir"), "data", "userDB");
            //  지정한 path에 디렉토리 없으면 생성
            if (!Files.exists(this.databasePath)) {
                try {
                    Files.createDirectories(this.databasePath);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public User create(User user) {
        // 객체를 저장할 파일 path 생성
        Path filePath = this.databasePath.resolve(String.valueOf(user.getId()).concat(".ser"));
        // 파일 생성
        File myObj = new File(String.valueOf(filePath));

        try (
                // 파일과 연결되는 스트림 생성
                FileOutputStream fos = new FileOutputStream(myObj);
                // 객체를 직렬화할 수 있게 바이트 출력 스트림을 감쌈
                ObjectOutputStream oos = new ObjectOutputStream(fos);
        ) {
            oos.writeObject(user);
            return user;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public User find(UUID userId) {
        // 객체가 저장된 파일 path
        Path filePath = this.databasePath.resolve(String.valueOf(userId).concat(".ser"));

        try (
                // 파일과 연결되는 스트림 생성
                FileInputStream fis = new FileInputStream(String.valueOf(filePath));
                // 객체를 역직렬화할 수 있게 바이트 입력 스트림을 감쌈
                ObjectInputStream ois = new ObjectInputStream(fis);
        ) {
            User user = (User) ois.readObject();
            return user;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    // TODO : 이름검색을 어떻게 하지? 모든 리스트 읽어서 이름 필터링해서 가져와야하나?
    @Override
    public List<User> find(String name) {
        return List.of();
    }

    @Override
    public List<User> findAll() {
        List<User> users = new ArrayList<>();

        try {
            Files.walk(this.databasePath).filter(Files::isRegularFile)
                    .forEach((path) -> {

                        try ( // 파일과 연결되는 스트림 생성
                              FileInputStream fis = new FileInputStream(String.valueOf(path));
                              // 객체를 역직렬화할 수 있게 바이트 입력 스트림을 감쌈
                              ObjectInputStream ois = new ObjectInputStream(fis);
                        ) {
                            User user = (User) ois.readObject();
                            users.add(user);
                        } catch (IOException | ClassNotFoundException e) {
                            throw new RuntimeException(e);
                        }

                    });

            return users;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public User update(UUID userId, String newName, int newAge, String newEmail, String newPassword) {

        try {
            User user = this.find(userId);
            user.update(newName, newAge, newEmail, newPassword);
            this.create(user);
            
            return user;
        } catch (RuntimeException e) {
            throw e;
        }

    }

    @Override
    public void delete(UUID userId) {
        // 객체가 저장된 파일 path
        Path filePath = this.databasePath.resolve(String.valueOf(userId).concat(".ser"));

        try {
            if (Files.exists(filePath)) {
                Files.delete(filePath);
            } else {
                throw new FileNotFoundException("File does not exist");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
