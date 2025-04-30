package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entitiy.ReadStatus;
import com.sprint.mission.discodeit.entitiy.User;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "File")
public class FileReadStatusRepository implements ReadStatusRepository {


    private static final Path FILE_PATH = Paths.get("src/main/java/com/sprint/mission/discodeit/repository/file/data/readstatuses.ser");


    //File*Repository에서만 사용, 파일을 읽어들여 리스트 반환
    public List<ReadStatus> readFiles() {
        try {
            if (!Files.exists(FILE_PATH) || Files.size(FILE_PATH) == 0) {
                return new ArrayList<>();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        List<ReadStatus> readStatuses = new ArrayList<>();
        try (ObjectInputStream reader = new ObjectInputStream(new FileInputStream(FILE_PATH.toFile()))) {
            while(true) {
                try {
                    readStatuses.add((ReadStatus) reader.readObject());
                } catch (EOFException e) {
                    break;
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return readStatuses;
    }


    //File*Repository에서만 사용, 만들어 놓은 리스트를 인자로 받아 파일에 쓰기
    public void writeFiles(List<ReadStatus> readStatuses) {
        try {
            Files.createDirectories(FILE_PATH.getParent());
            try (ObjectOutputStream writer = new ObjectOutputStream(new FileOutputStream(FILE_PATH.toFile()))) {
                for (ReadStatus readStatus : readStatuses) {
                    writer.writeObject(readStatus);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public ReadStatus save(ReadStatus readStatus) {
        List<ReadStatus> readStatuses = readFiles();
        readStatuses.add(readStatus);
        writeFiles(readStatuses);
        return readStatus;
    }

    @Override
    public List<ReadStatus> read() {
        List<ReadStatus> readStatuses = readFiles();
        return readStatuses;
    }

    @Override
    public Optional<ReadStatus> readById(UUID id) {
        List<ReadStatus> readStatuses = readFiles();
        Optional<ReadStatus> readStatus = readStatuses.stream()
                .filter((u)->u.getId().equals(id))
                .findAny();
        return readStatus;
    }

    @Override
    public List<ReadStatus> readByUserId(UUID userId) {
        List<ReadStatus> readStatuses = readFiles();
        List<ReadStatus> findByUserIdList = readStatuses.stream()
                .filter(readStatus->readStatus.getUserId().equals(userId))
                .toList();
        return findByUserIdList;
    }

    @Override
    public List<ReadStatus> readByChannelId(UUID channelId) {
        List<ReadStatus> readStatuses = readFiles();
        List<ReadStatus> findByChannelIdList = readStatuses.stream()
                .filter(readStatus->readStatus.getChannelId().equals(channelId))
                .toList();
        return findByChannelIdList;
    }

    @Override
    public void update(UUID id, ReadStatus readStatus) {
        List<ReadStatus> readStatuses = readFiles();
        readStatuses.stream()
                .filter((c)->c.getId().equals(id))
                .forEach((c)->{
                    c.setUpdatedAt(Instant.now());
                });
        writeFiles(readStatuses);
    }

    @Override
    public void delete(UUID readStatusId) {
        List<ReadStatus> readStatuses = readFiles();
        List<ReadStatus> ReadStatus = readStatuses.stream()
                .filter((c) -> !c.getId().equals(readStatusId))
                .toList();
        writeFiles(readStatuses);
    }
}
