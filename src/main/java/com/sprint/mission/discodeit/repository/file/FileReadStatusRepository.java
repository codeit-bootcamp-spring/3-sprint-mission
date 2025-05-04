package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;

import java.io.*;
import java.util.*;

public class FileReadStatusRepository implements ReadStatusRepository {
    private static final String FILE_PATH = "data/read-statuses.ser";
    private Map<UUID, ReadStatus> data;

    public FileReadStatusRepository() {
        this.data = load();
    }

    @Override
    public ReadStatus save(ReadStatus readStatus) {
        data.put(readStatus.getId(), readStatus);
        saveToFile();
        return readStatus;
    }

    @Override
    public Optional<ReadStatus> findById(UUID id) {
        return Optional.ofNullable(data.get(id));
    }

    @Override
    public List<ReadStatus> findAll() {
        return new ArrayList<>(data.values());
    }

    @Override
    public void deleteById(UUID id) {
        data.remove(id);
        saveToFile();
    }

    public List<ReadStatus> findAllByUserId(UUID userId) {
        List<ReadStatus> result = new ArrayList<>();
        for (ReadStatus rs : data.values()) {
            if (rs.getUserId().equals(userId)) {
                result.add(rs);
            }
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    private Map<UUID, ReadStatus> load() {
        File file = new File(FILE_PATH);
        if (!file.exists()) return new HashMap<>();
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (Map<UUID, ReadStatus>) ois.readObject();
        } catch (Exception e) {
            return new HashMap<>();
        }
    }

    private void saveToFile() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_PATH))) {
            oos.writeObject(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}