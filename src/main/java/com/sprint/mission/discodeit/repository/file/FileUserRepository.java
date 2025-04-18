package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

public class FileUserRepository implements UserRepository {

    private final String FILEPATH = "users.ser";
    private List<User> users;

    public FileUserRepository() {
        users = loadUsers();
        if (users == null) {
            users = new ArrayList<>();
        } else {
            int max = users.stream().mapToInt(User::getNumber).max().orElse(0);
            User.setCounter(max + 1);
        }
    }

    @SuppressWarnings("unchecked")
    private List<User> loadUsers() {
        File file = new File(FILEPATH);
        if (!file.exists()) {
            return null;
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (List<User>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void saveUsers() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILEPATH))) {
            oos.writeObject(users);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void saveUser(User user) {
        users.add(user);
        saveUsers();
    }

    @Override
    public void updateUser(User user) {
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getNumber() == user.getNumber()) {
                users.set(i, user);
                saveUsers();
                return;
            }
        }
    }

    @Override
    public void deleteUser(int userNumber) {
        users.removeIf(u -> u.getNumber() == userNumber);
        saveUsers();
    }

    @Override
    public User findUser(int userNumber) {
        return users.stream()
                .filter(u -> u.getNumber() == userNumber)
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<User> findAllUser() {
        return users;
    }
}