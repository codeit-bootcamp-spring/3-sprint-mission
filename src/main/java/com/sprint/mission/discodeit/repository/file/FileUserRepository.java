package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.util.*;

@Repository
@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "file")
public class FileUserRepository implements UserRepository {

    @Value("${discodeit.repository.file-directory}")
    private String FILE_DIRECTORY;
    private final String FILENAME = "userRepo.ser";

    private File getFile() {
        return new File(FILE_DIRECTORY, FILENAME);
    }

    @Override
    public User save(User user) {
        // 파일에서 읽어오기
        Map<UUID, User> data = loadFile();

        data.put(user.getId(), user);

        saveFile(data);

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
    public List<User> findByNameContaining(String name) {
        // 파일에서 읽어오기
        Map<UUID, User> data = loadFile();

        return data.values().stream()
                .filter(user -> user.getName().contains(name))
                .toList();
    }

    @Override
    public Optional<User> findByName(String name) {
        // 파일에서 읽어오기
        Map<UUID, User> data = loadFile();

        Optional<User> foundUser = data.values().stream()
                .filter(user -> user.getName().equals(name))
                .findFirst();

        return foundUser;
    }

    @Override
    public Optional<User> findByEmail(String email) {
        // 파일에서 읽어오기
        Map<UUID, User> data = loadFile();

        Optional<User> foundUser = data.values().stream()
                .filter(user -> user.getEmail().equals(email))
                .findFirst();

        return foundUser;
    }

    @Override
    public Optional<User> findByNameAndPassword(String name, String password) {
        // 파일에서 읽어오기
        Map<UUID, User> data = loadFile();

        Optional<User> foundUser = data.values().stream()
                .filter(user -> user.getName().equals(name) && user.getPassword().equals(password))
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
        saveFile(data);
    }

    private Map<UUID, User> loadFile() {
        Map<UUID, User> data = new HashMap<>();

        if (getFile().exists()) {
            try (FileInputStream fis = new FileInputStream(getFile());
                 ObjectInputStream in = new ObjectInputStream(fis)) {
                data = (Map<UUID, User>)in.readObject();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

        return data;
    }

    private void saveFile(Map<UUID, User> data) {
        try (FileOutputStream fos = new FileOutputStream(getFile());
             ObjectOutputStream out = new ObjectOutputStream(fos)) {
            out.writeObject(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
