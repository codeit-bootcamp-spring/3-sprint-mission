package com.sprint.mission.discodeit.Repository.file;

import com.sprint.mission.discodeit.Repository.UserRepository;
import com.sprint.mission.discodeit.entity.User;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class FileUserRepository implements UserRepository {
    private static final String DIR = "data/users/";
    private static final Path indexFilePath = Paths.get("data/users/index.ser");

    public void init() {
        if (!Files.exists(Paths.get(DIR))) {
            try {
                Files.createDirectories(Path.of(DIR));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void save(User user) {
        try (
            FileOutputStream fos = new FileOutputStream(DIR + user.getId() + ".ser");
            ObjectOutputStream oos = new ObjectOutputStream(fos);
        ) {
            oos.writeObject(user);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public User loadByIndex(String name) {
        try (
                ObjectInputStream ois = new ObjectInputStream(new FileInputStream(indexFilePath.toFile()))
        ) {
            //return ois.readObject();
            return null;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public User loadById(UUID id) {
        if (Files.exists(Path.of(DIR))) {
            try (ObjectInputStream in = new ObjectInputStream(
                    new FileInputStream(DIR + id + ".ser"))) {
                return (User) in.readObject();
            } catch (IOException | ClassNotFoundException e) {
                return null;
            }
        } else {
            return null;
        }

    }

    @Override
    public List<User> loadAll() {
        if (Files.exists(Path.of(DIR))) {
            try {
                List<User> users = Files.list(Paths.get(DIR))
                        .map( path -> {
                            try (
                                    FileInputStream fis = new FileInputStream(path.toFile());
                                    ObjectInputStream ois = new ObjectInputStream(fis)
                            ) {
                                Object data = ois.readObject();
                                return (User) data;
                            } catch (IOException | ClassNotFoundException e) {
                                throw new RuntimeException(e);
                            }
                        })
                        .toList();
                return users;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            return new ArrayList<>(); //null로 리턴 시, nullPointerException
        }
    }

    @Override
    public void deleteById(UUID id) {
        File file = new File(DIR + id + ".ser");
        if (file.exists()) file.delete();
    }
}
