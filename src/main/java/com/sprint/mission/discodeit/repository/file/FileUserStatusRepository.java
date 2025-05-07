package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserStatusRepository;

import java.io.*;
import java.util.*;

public class FileUserStatusRepository implements UserStatusRepository {

    private String fileName;
    private File file;

    public FileUserStatusRepository(String filePath) {
        this.fileName = "src/main/java/com/sprint/mission/discodeit/" + filePath + "/userStatusRepo.ser";
        this.file = new File(fileName);
    }

    @Override
    public void save(UserStatus userStatus) {
        // 파일에서 읽어오기
        Map<UUID, UserStatus> data = loadFile();

        data.put(userStatus.getId(), userStatus);

        try (FileOutputStream fos = new FileOutputStream(file);
             ObjectOutputStream out = new ObjectOutputStream(fos)) {
            out.writeObject(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Optional<UserStatus> findById(UUID id) {
        // 파일에서 읽어오기
        Map<UUID, UserStatus> data = loadFile();

        Optional<UserStatus> foundUserStatus = data.entrySet().stream()
                .filter(entry -> entry.getKey().equals(id))
                .map(Map.Entry::getValue)
                .findFirst();

        return foundUserStatus;
    }

    @Override
    public Optional<UserStatus> findByUserId(UUID userId) {
        // 파일에서 읽어오기
        Map<UUID, UserStatus> data = loadFile();

        Optional<UserStatus> foundUserStatus = data.values().stream()
                .filter(userStatus -> userStatus.getUserId().equals(userId))
                .findFirst();

        return foundUserStatus;
    }

    @Override
    public List<UserStatus> findAll() {
        // 파일에서 읽어오기
        Map<UUID, UserStatus> data = loadFile();

        return new ArrayList<>(data.values());
    }

    @Override
    public void deleteById(UUID id) {
        // 파일에서 읽어오기
        Map<UUID, UserStatus> data = loadFile();

        data.remove(id);

        try (FileOutputStream fos = new FileOutputStream(file);
             ObjectOutputStream out = new ObjectOutputStream(fos)) {
            out.writeObject(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteByUserId(UUID userId) {
        // 파일에서 읽어오기
        Map<UUID, UserStatus> data = loadFile();

        data.entrySet().removeIf(entry -> entry.getValue().getUserId().equals(userId));

        try (FileOutputStream fos = new FileOutputStream(file);
             ObjectOutputStream out = new ObjectOutputStream(fos)) {
            out.writeObject(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Map<UUID, UserStatus> loadFile() {
        Map<UUID, UserStatus> data = new HashMap<>();

        if (file.exists()) {
            try (FileInputStream fis = new FileInputStream(file);
                 ObjectInputStream in = new ObjectInputStream(fis)) {
                data = (Map<UUID, UserStatus>)in.readObject();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

        return data;
    }
}
