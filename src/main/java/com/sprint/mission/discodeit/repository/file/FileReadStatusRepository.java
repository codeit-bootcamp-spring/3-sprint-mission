package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "file")
public class FileReadStatusRepository implements ReadStatusRepository {
    private final Path DIRECTORY;
    private final String EXTENSION = ".ser";

    public FileReadStatusRepository() {

        //  현재디렉토리/data/userDB 디렉토리를 저장할 path로 설정
        this.DIRECTORY = Paths.get(System.getProperty("user.dir"), "data", ReadStatus.class.getSimpleName());
        //  지정한 path에 디렉토리 없으면 생성
        if (!Files.exists(this.DIRECTORY)) {
            try {
                Files.createDirectories(this.DIRECTORY);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    // TODO : 나중에 Util로 빼기
    private Path resolvePath(UUID id) {
        // 객체를 저장할 파일 path 생성
        return this.DIRECTORY.resolve(id + EXTENSION);
    }

    @Override
    public ReadStatus save(ReadStatus readStatus) {
        Path filePath = this.resolvePath(readStatus.getId());

        try (
                // 파일과 연결되는 스트림 생성
                FileOutputStream fos = new FileOutputStream(filePath.toFile());
                // 객체를 직렬화할 수 있게 바이트 출력 스트림을 감쌈
                ObjectOutputStream oos = new ObjectOutputStream(fos);
        ) {

            oos.writeObject(readStatus);
            return readStatus;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<ReadStatus> findById(UUID readStatusId) {
        // 객체가 저장된 파일 path
        Path filePath = this.resolvePath(readStatusId);

        try (
                // 파일과 연결되는 스트림 생성
                FileInputStream fis = new FileInputStream(String.valueOf(filePath));
                // 객체를 역직렬화할 수 있게 바이트 입력 스트림을 감쌈
                ObjectInputStream ois = new ObjectInputStream(fis);
        ) {
            ReadStatus readStatusNullable = (ReadStatus) ois.readObject();
            return Optional.ofNullable(readStatusNullable);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public List<ReadStatus> findAllByChannelId(UUID channelId) {
        return this.findAll().stream().filter((readStatus) -> readStatus.getChannelId().equals(channelId)).toList();
    }

    @Override
    public List<ReadStatus> findAll() {
        List<ReadStatus> readStatuses = new ArrayList<>();
        try {
            Files.list(this.DIRECTORY).filter(Files::isRegularFile)
                    .forEach((path) -> {
                        try ( // 파일과 연결되는 스트림 생성
                              FileInputStream fis = new FileInputStream(String.valueOf(path));
                              // 객체를 역직렬화할 수 있게 바이트 입력 스트림을 감쌈
                              ObjectInputStream ois = new ObjectInputStream(fis);
                        ) {
                            ReadStatus userStatus = (ReadStatus) ois.readObject();
                            readStatuses.add(userStatus);
                        } catch (IOException | ClassNotFoundException e) {
                            throw new RuntimeException(e);
                        }

                    });
            return readStatuses;
        } catch (IOException e) {
            return List.of();
        }
    }

    @Override
    public boolean existsById(UUID readStatusId) {
        Path path = resolvePath(readStatusId);
        return Files.exists(path);
    }

    @Override
    public void deleteById(UUID readStatusId) {
        // 객체가 저장된 파일 path
        Path filePath = this.resolvePath(readStatusId);
        try {
            if (Files.exists(filePath)) {
                Files.delete(filePath);
            } else {
                throw new FileNotFoundException("File does not exist");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
