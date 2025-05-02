package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
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
public class FileBinaryContentRepository implements BinaryContentRepository {
    private final Path DIRECTORY;
    private final String EXTENSION = ".ser";

    public FileBinaryContentRepository() {

        //  현재디렉토리/data/userDB 디렉토리를 저장할 path로 설정
        this.DIRECTORY = Paths.get(System.getProperty("user.dir"), "data", BinaryContent.class.getSimpleName());
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
    public BinaryContent save(BinaryContent binaryContent) {
        Path filePath = this.resolvePath(binaryContent.getId());

        try (
                // 파일과 연결되는 스트림 생성
                FileOutputStream fos = new FileOutputStream(filePath.toFile());
                // 객체를 직렬화할 수 있게 바이트 출력 스트림을 감쌈
                ObjectOutputStream oos = new ObjectOutputStream(fos);
        ) {

            oos.writeObject(binaryContent);
            return binaryContent;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<BinaryContent> findById(UUID binaryContentId) {
        // 객체가 저장된 파일 path
        Path filePath = this.resolvePath(binaryContentId);

        try (
                // 파일과 연결되는 스트림 생성
                FileInputStream fis = new FileInputStream(String.valueOf(filePath));
                // 객체를 역직렬화할 수 있게 바이트 입력 스트림을 감쌈
                ObjectInputStream ois = new ObjectInputStream(fis);
        ) {
            BinaryContent binaryContentNullable = (BinaryContent) ois.readObject();
            return Optional.ofNullable(binaryContentNullable);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<BinaryContent> findAll() {
        List<BinaryContent> binaryContentList = new ArrayList<>();
        try {
            Files.list(this.DIRECTORY).filter(Files::isRegularFile)
                    .forEach((path) -> {
                        try ( // 파일과 연결되는 스트림 생성
                              FileInputStream fis = new FileInputStream(String.valueOf(path));
                              // 객체를 역직렬화할 수 있게 바이트 입력 스트림을 감쌈
                              ObjectInputStream ois = new ObjectInputStream(fis);
                        ) {
                            BinaryContent binaryContent = (BinaryContent) ois.readObject();
                            binaryContentList.add(binaryContent);
                        } catch (IOException | ClassNotFoundException e) {
                            throw new RuntimeException(e);
                        }

                    });
            return binaryContentList;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean existsById(UUID binaryContentId) {
        Path path = resolvePath(binaryContentId);
        return Files.exists(path);
    }

    @Override
    public void deleteById(UUID binaryContentId) {
        // 객체가 저장된 파일 path
        Path filePath = this.resolvePath(binaryContentId);
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
