package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;

import java.io.*;
import java.util.*;

public class FileUserService implements UserService {

    private final String fileName = "src/main/java/com/sprint/mission/discodeit/service/file/users.ser";
    private final File file = new File(fileName);

    @Override
    public void save(User user) { // 저장 로직
        // 파일에서 읽어오기
        Map<UUID, User> data = readFile();

        // User 저장
        data.put(user.getId(), user);

        // User Data 파일에 저장
        try (FileOutputStream fos = new FileOutputStream(file);
             ObjectOutputStream out = new ObjectOutputStream(fos)) {
            out.writeObject(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Optional<User> findById(UUID id) {
        // 파일에서 읽어오기
        Map<UUID, User> data = readFile();

        // 조건을 만족하는 User, NullPointerException 방지
        Optional<User> foundUser = data.entrySet().stream()
                .filter(entry -> entry.getKey().equals(id))
                .map(Map.Entry::getValue)
                .findFirst();

        return foundUser;
    }

    @Override
    public List<User> findAll() {
        // 파일에서 읽어오기
        Map<UUID, User> data = readFile();

        return new ArrayList<>(data.values());
    }

    @Override
    public User update(User user) { // 저장 로직
        // 파일에서 읽어오기
        Map<UUID, User> data = readFile();

        data.put(user.getId(), user);

        try (FileOutputStream fos = new FileOutputStream(file);
             ObjectOutputStream out = new ObjectOutputStream(fos)) {
            out.writeObject(data);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return user;
    }

    @Override
    public void deleteById(UUID id) { // 저장 로직
        Map<UUID, User> data = readFile();

        data.remove(id);

        try (FileOutputStream fos = new FileOutputStream(file);
             ObjectOutputStream out = new ObjectOutputStream(fos)) {
            out.writeObject(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Map<UUID, User> readFile() {
        Map<UUID, User> data = new HashMap<>();

        if (file.exists()) {
            try (FileInputStream fis = new FileInputStream(file);
                 ObjectInputStream in = new ObjectInputStream(fis)) {
                data = (Map<UUID, User>)in.readObject();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

        return data;
    }
}
