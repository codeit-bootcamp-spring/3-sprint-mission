package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "file")
@Repository
public class FileUserRepository implements UserRepository {
    private final Path DIRECTORY;
    private final String EXTENSION = ".ser";

    public FileUserRepository(
            @Value("${discodeit.repository.file-directory:data}") String fileDirectory){
        this.DIRECTORY = Paths.get(System.getProperty("user.dir"),fileDirectory,User.class.getSimpleName());
        if (Files.notExists(DIRECTORY)) {
            try {
                Files.createDirectories(DIRECTORY);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private Path resolvePath(UUID id){
        return DIRECTORY.resolve(id+EXTENSION);
    }

    @Override
    public void save(User user) {
        Path path = resolvePath(user.getId());
        try (
                FileOutputStream fos = new FileOutputStream(path.toFile());
                ObjectOutputStream oos = new ObjectOutputStream(fos)
        ) {
            oos.writeObject(user);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public User findById(UUID userId) {
        User user = null;
        Path targetUserFile = resolvePath(userId);
        if( Files.exists(targetUserFile)){
            try (
                    FileInputStream fis = new FileInputStream(targetUserFile.toFile());
                    ObjectInputStream ois = new ObjectInputStream(fis)
            ) {
                user = (User) ois.readObject();
            } catch (IOException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
        return user;
    }

    @Override
    public List<User> findAll() {
        try {
            return Files.list(DIRECTORY)
                    .filter(path -> path.toString().endsWith(EXTENSION))
                    .map(path -> {
                        try (
                                FileInputStream fis = new FileInputStream(path.toFile());
                                ObjectInputStream ois = new ObjectInputStream(fis)
                        ) {
                            return (User) ois.readObject();
                        } catch (IOException | ClassNotFoundException e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .toList();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean isExistUsername(String username) {
        return findAll().stream().anyMatch(u -> u.getUsername().equalsIgnoreCase(username));
    }

    @Override
    public boolean isExistEmail(String email) {
        return findAll().stream().anyMatch(u -> u.getEmail().equalsIgnoreCase(email));
    }

    @Override
    public void delete(UUID userId) {
        Path path = resolvePath(userId);
        try {
            Files.delete(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
