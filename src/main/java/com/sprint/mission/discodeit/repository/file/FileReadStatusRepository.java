package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.util.*;

@Repository
@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "file")
public class FileReadStatusRepository implements ReadStatusRepository {

    @Value("${discodeit.repository.file-directory}")
    private String FILE_DIRECTORY;
    private final String FILENAME = "readStatusRepo.ser";

    private File getFile() {
        return new File(FILE_DIRECTORY, FILENAME);
    }

    @Override
    public void save(ReadStatus readStatus) {
        // 파일에서 읽어오기
        Map<UUID, ReadStatus> data = loadFile();

        data.put(readStatus.getId(), readStatus);

        try (FileOutputStream fos = new FileOutputStream(getFile());
             ObjectOutputStream out = new ObjectOutputStream(fos)) {
            out.writeObject(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Optional<ReadStatus> findByChannelIdAndUserId(UUID channelId, UUID userId) {
        // 파일에서 읽어오기
        Map<UUID, ReadStatus> data = loadFile();

        Optional<ReadStatus> foundReadStatus = data.values().stream()
                .filter(readStatus ->
                        readStatus.getChannelId().equals(channelId) && readStatus.getUserId().equals(userId)
                )
                .findFirst();

        return foundReadStatus;
    }

    @Override
    public Optional<ReadStatus> findById(UUID id) {
        // 파일에서 읽어오기
        Map<UUID, ReadStatus> data = loadFile();

        Optional<ReadStatus> foundReadStatus = data.entrySet().stream()
                .filter(entry -> entry.getKey().equals(id))
                .map(Map.Entry::getValue)
                .findFirst();

        return foundReadStatus;
    }

    @Override
    public List<ReadStatus> findAll() {
        // 파일에서 읽어오기
        Map<UUID, ReadStatus> data = loadFile();

        return new ArrayList<>(data.values());
    }

    @Override
    public List<ReadStatus> findAllByUserId(UUID userId) {
        // 파일에서 읽어오기
        Map<UUID, ReadStatus> data = loadFile();

        return data.values().stream()
                .filter(readStatus -> readStatus.getUserId().equals(userId))
                .toList();
    }

    @Override
    public void deleteById(UUID id) {
        // 파일에서 읽어오기
        Map<UUID, ReadStatus> data = loadFile();

        data.remove(id);

        try (FileOutputStream fos = new FileOutputStream(getFile());
             ObjectOutputStream out = new ObjectOutputStream(fos)) {
            out.writeObject(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteByChannelId(UUID channelId) {
        // 파일에서 읽어오기
        Map<UUID, ReadStatus> data = loadFile();

        data.entrySet().removeIf(entry -> entry.getValue().getChannelId().equals(channelId));

        try (FileOutputStream fos = new FileOutputStream(getFile());
             ObjectOutputStream out = new ObjectOutputStream(fos)) {
            out.writeObject(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteByChannelIdAndUserId(UUID channelId, UUID userId) {
        // 파일에서 읽어오기
        Map<UUID, ReadStatus> data = loadFile();

        data.entrySet().removeIf(entry -> entry.getValue().getChannelId().equals(channelId)
                && entry.getValue().getUserId().equals(userId));

        try (FileOutputStream fos = new FileOutputStream(getFile());
             ObjectOutputStream out = new ObjectOutputStream(fos)) {
            out.writeObject(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
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
}
