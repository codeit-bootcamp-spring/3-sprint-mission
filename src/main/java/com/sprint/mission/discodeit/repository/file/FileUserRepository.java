package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class FileUserRepository implements UserRepository {
    private final Path databasePath;

    public FileUserRepository() {
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
    public void write(User user) {
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

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public User read(UUID id) {
        Path filePath = this.databasePath.resolve(String.valueOf(id).concat(".ser"));

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

    @Override
    public List<User> readAll() {
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
                            //FIXME
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        } catch (ClassNotFoundException e) {
                            throw new RuntimeException(e);
                        }
                    });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return users;
    }

    @Override
    public boolean delete(UUID id) {
        Path filePath = this.databasePath.resolve(String.valueOf(id).concat(".ser"));
        try {
            if (Files.exists(filePath)) {
                Files.delete(filePath);
                return true;
            } else {
                throw new FileNotFoundException("File does not exist");
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
}
