package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;

import java.io.*;
import java.util.*;

public class FileUserRepository implements UserRepository {

    private final String fileName = "src/main/java/com/sprint/mission/discodeit/repository/file/userRepo.ser";
    private final File file = new File(fileName);

    @Override
    public User save(User user) {
        // 파일에서 읽어오기
        Map<UUID, User> data = loadFile();

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
    public Optional<User> findById(UUID userId) {
        // 파일에서 읽어오기
        Map<UUID, User> data = loadFile();

        Optional<User> foundUser = data.entrySet().stream()
                .filter(entry -> entry.getKey().equals(userId))
                .map(Map.Entry::getValue)
                .findFirst();

        return foundUser;
    }

    @Override
    public List<User> findByName(String name) {
        // 파일에서 읽어오기
        Map<UUID, User> data = loadFile();

        return data.values().stream()
                .filter(user -> user.getName().contains(name))
                .toList();
    }

    @Override
    public Optional<User> findByEmail(String email) {
        // 파일에서 읽어오기
        Map<UUID, User> data = loadFile();

        Optional<User> foundUser = data.entrySet().stream()
                .filter(entry -> entry.getKey().equals(email))
                .map(Map.Entry::getValue)
                .findFirst();

        return foundUser;
    }

    @Override
    public List<User> findAll() {
        // 파일에서 읽어오기
        Map<UUID, User> data = loadFile();

        return new ArrayList<>(data.values());
    }

    @Override
    public void deleteById(UUID userId) {
        Map<UUID, User> data = loadFile();

        data.remove(userId);

        // User 삭제 후 파일에 덮어쓰기
        try (FileOutputStream fos = new FileOutputStream(file);
             ObjectOutputStream out = new ObjectOutputStream(fos)) {
            out.writeObject(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Map<UUID, User> loadFile() {
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
