package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.util.FileSaveManager;
import jakarta.annotation.PostConstruct;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

@Repository
@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "file")
public class FileReadStatusRepository implements ReadStatusRepository {

    @Value("${discodeit.repository.file-directory}")
    private String FILE_DIRECTORY;
    private final String FILENAME = "readStatusRepo.ser";
    private final Map<UUID, ReadStatus> data = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        Map<UUID, ReadStatus> loaded = FileSaveManager.loadFromFile(getFile());
        if (loaded != null) {
            data.putAll(loaded);
        }
    }

    private File getFile() {
        return new File(FILE_DIRECTORY, FILENAME);
    }

    @Override
    public void save(ReadStatus readStatus) {
        data.put(readStatus.getId(), readStatus);

        FileSaveManager.saveToFile(getFile(), data);
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

        FileSaveManager.saveToFile(getFile(), data);
    }

    @Override
    public void deleteByChannelId(UUID channelId) {
        data.entrySet().removeIf(entry -> entry.getValue().getChannelId().equals(channelId));

        FileSaveManager.saveToFile(getFile(), data);
    }

    @Override
    public void deleteByChannelIdAndUserId(UUID channelId, UUID userId) {
        data.entrySet().removeIf(entry -> entry.getValue().getChannelId().equals(channelId)
                && entry.getValue().getUserId().equals(userId));

       FileSaveManager.saveToFile(getFile(), data);
    }
}
