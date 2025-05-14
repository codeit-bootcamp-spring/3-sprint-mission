package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Repository
@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "file")
public class FileReadStatusRepository implements ReadStatusRepository {

    @Value("${discodeit.repository.file-directory}")
    private String FILE_DIRECTORY;
    private final String FILENAME = "readStatusRepo.ser";
    private final Map<UUID, ReadStatus> data = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        data.putAll(loadFile());
    }

    private File getFile() {
        return new File(FILE_DIRECTORY, FILENAME);
    }

    @Override
    public void save(ReadStatus readStatus) {
        data.put(readStatus.getId(), readStatus);

        saveFile();
    }

    @Override
    public Optional<ReadStatus> findByChannelIdAndUserId(UUID channelId, UUID userId) {
        Optional<ReadStatus> foundReadStatus = data.values().stream()
                .filter(readStatus ->
                        readStatus.getChannelId().equals(channelId) && readStatus.getUserId().equals(userId)
                )
                .findFirst();

        return foundReadStatus;
    }

    @Override
    public Optional<ReadStatus> findById(UUID id) {
        Optional<ReadStatus> foundReadStatus = data.entrySet().stream()
                .filter(entry -> entry.getKey().equals(id))
                .map(Map.Entry::getValue)
                .findFirst();

        return foundReadStatus;
    }

    @Override
    public List<ReadStatus> findAll() {
        return new ArrayList<>(data.values());
    }

    @Override
    public List<ReadStatus> findAllByUserId(UUID userId) {
        return data.values().stream()
                .filter(readStatus -> readStatus.getUserId().equals(userId))
                .toList();
    }

    @Override
    public void deleteById(UUID id) {
        data.remove(id);

        saveFile();
    }

    @Override
    public void deleteByChannelId(UUID channelId) {
        data.entrySet().removeIf(entry -> entry.getValue().getChannelId().equals(channelId));

        saveFile();
    }

    @Override
    public void deleteByChannelIdAndUserId(UUID channelId, UUID userId) {
        data.entrySet().removeIf(entry -> entry.getValue().getChannelId().equals(channelId)
                && entry.getValue().getUserId().equals(userId));

        saveFile();
    }

    private Map<UUID, ReadStatus> loadFile() {
        Map<UUID, ReadStatus> data = new HashMap<>();

        if (getFile().exists()) {
            try (FileInputStream fis = new FileInputStream(getFile());
                 ObjectInputStream in = new ObjectInputStream(fis)) {
                data = (Map<UUID, ReadStatus>)in.readObject();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

        return data;
    }

    private synchronized void saveFile() {
        try (FileOutputStream fos = new FileOutputStream(getFile());
             ObjectOutputStream out = new ObjectOutputStream(fos)) {
            out.writeObject(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
