package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Repository
public class FileReadStatusRepository implements ReadStatusRepository {
    private static final Path DIRECTORY = Paths.get(System.getProperty("user.dir"), "data", "readStatus");

    public FileReadStatusRepository() {
        init();
    }

    // 저장할 경로의 파일 초기화
    public static Path init() {
        if (!Files.exists(DIRECTORY)) {
            try {
                Files.createDirectories(DIRECTORY);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        return DIRECTORY;
    }

    public static ReadStatus load(Path filePath) {
        if (!Files.exists(filePath)) {
            return null;
        }
        try (
                FileInputStream fis = new FileInputStream(filePath.toFile());
                ObjectInputStream ois = new ObjectInputStream(fis)
        ) {
            Object data = ois.readObject();
            return (ReadStatus) data;
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("파일 로딩 실패", e);
        }
    }

    @Override
    public ReadStatus save(ReadStatus readStatus) {
        Path filePath = Paths.get(String.valueOf(DIRECTORY), readStatus.getId()+".ser");
        try(
                FileOutputStream fos = new FileOutputStream(filePath.toFile());
                ObjectOutputStream oos = new ObjectOutputStream(fos);
        ) {
            oos.writeObject(readStatus);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return readStatus;
    }

    @Override
    public List<ReadStatus> findAll() {
        if (!Files.exists(DIRECTORY)) {
            return new ArrayList<>();
        } else {
            List<ReadStatus> data = new ArrayList<>();
            File[] files = DIRECTORY.toFile().listFiles();
            if (files != null) {
                for (File file : files) {
                    data.add(load(file.toPath()));
                }
            }
            return data;
        }
    }

    @Override
    public ReadStatus find(UUID id) {
        return load(Paths.get(String.valueOf(DIRECTORY), id+".ser"));
    }

    @Override
    public void delete(UUID id) throws IOException {
        Files.delete(Paths.get(String.valueOf(DIRECTORY), id+".ser"));
    }
}
