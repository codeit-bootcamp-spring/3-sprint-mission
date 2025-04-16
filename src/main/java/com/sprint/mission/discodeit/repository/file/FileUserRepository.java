package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;

import java.io.*;
import java.util.*;

public class FileUserRepository implements UserRepository {

    private static final String USER_FILE_REPOSITORY_PATH = "src/main/java/com/sprint/mission/discodeit/repository/data/User.txt";
    File file = new File(USER_FILE_REPOSITORY_PATH);

    // 저장
    @Override
    public void save(User user) {

        // 방어 코드
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        }

        // 객체 직렬화( 저장 )
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
            oos.writeObject(user);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    // 전체 저장


    @Override
    public void saveAll(List<User> users) {

        // 방어 코드
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        }

        // 객체 직렬화 ( 전체 저장 )
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
            oos.writeObject(users);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    // 전체 정보 불러오기
    @Override
    public List<User> loadAll() {

        // 역직렬화 ( 불러오기 )
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (List<User>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    // 단일 정보 불러오기( ID )
    @Override
    public User loadById(UUID id) {
        List<User> users = loadAll();
        for (User user : users) {
            if (user.getUserId().equals(id)) {
                return user;
            }
        }
        return null;
    }

    // 정보 불러오기 ( Name )
    @Override
    public List<User> loadByName(String name) {
        List<User> users = loadAll();
        for (User user : users) {
            if (user.getUserName().equals(name)) {
                return users;
            }
        }
        return null;
    }
}
