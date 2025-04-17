package com.sprint.mission.discodeit.service.file;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class FileDataStore<T extends Serializable> {
    private final String filePath;

    public FileDataStore(String filePath) {
        this.filePath = filePath;
    }

    public Map<UUID, T> load() {
        File file = new File(filePath);
        if (!file.exists()) return new HashMap<>();

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (Map<UUID, T>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("데이터 로딩 실패: " + filePath, e);
        }
    }

    public void save(Map<UUID, T> data) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath))) {
            oos.writeObject(data);
        } catch (IOException e) {
            throw new RuntimeException("데이터 저장 실패: " + filePath, e);
        }
    }
}
