package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "file")
@Repository
public class FileBinaryContentRepository implements BinaryContentRepository {

    private static final Logger logger = Logger.getLogger(FileBinaryContentRepository.class.getName());

    private final Path DIRECTORY;
    private final String EXTENSION = ".ser";

    public FileBinaryContentRepository(
            @Value("${discodeit.repository.file-directory:data}") String fileDirectory
    ) {
        this.DIRECTORY = Paths.get(System.getProperty("user.dir"), fileDirectory, BinaryContent.class.getSimpleName());
        if (Files.notExists(DIRECTORY)) {
            try {
                Files.createDirectories(DIRECTORY);
            } catch (IOException e) {
                throw new RuntimeException("경로 생성 중 오류 발생 ", e);
            }
        }
    }

    private Path resolvePath(UUID id) {
        return DIRECTORY.resolve(id + EXTENSION);
    }


    //바이너리 컨텐츠 저장
    @Override
    public BinaryContent saveBinaryContent(BinaryContent binaryContent) {
        Path path = resolvePath(binaryContent.getId());
        try (
                FileOutputStream fos = new FileOutputStream(path.toFile());
             ObjectOutputStream oos = new ObjectOutputStream(fos)
        ) {
            oos.writeObject(binaryContent);
            logger.log(Level.INFO, "바이너리 파일이 저장 되었습니다");
        }
        catch (FileNotFoundException e) {
            // 파일 생성 실패 시 메시지
            logger.log(Level.INFO, path + " 경로에 파일을 생성할 수 없습니다 ");
            throw new RuntimeException(e);
        }
        catch (IOException e) {
            // 그 외 IO 예외
            logger.log(Level.INFO, " 바이너리 파일 저장 중 오류 발생 ");
            throw new RuntimeException(e);
        }
        return binaryContent;
    }

    // 파일 Id 로 검색
    @Override
    public Optional<BinaryContent> findById(UUID id) {
        Path path = resolvePath(id);

        if (!Files.exists(path)) {
            return Optional.empty();
        }

        try (
                FileInputStream fis = new FileInputStream(path.toFile());
                ObjectInputStream ois = new ObjectInputStream(fis)
        ){
            BinaryContent binaryContent = (BinaryContent) ois.readObject();
            return Optional.of(binaryContent);
        }
        catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(path + " 파일 읽기에 실패했습니다. ", e);
        }
    }

    // 다건 조회
    @Override
    public List<BinaryContent> findAllByIds(List<UUID> ids) {
        try (Stream<Path> paths = Files.list(DIRECTORY)) {
            return paths
                    .filter(path -> path.toString().endsWith(EXTENSION))
                    .map(path -> {
                        try (
                                FileInputStream fis = new FileInputStream(path.toFile());
                                ObjectInputStream ois = new ObjectInputStream(fis)
                        ) {
                            return (BinaryContent) ois.readObject();
                        } catch (IOException | ClassNotFoundException e) {
                            throw new RuntimeException("파일을 객체로 직렬화 하는 것에 실패했습니다.", e);
                        }
                    })
                    .filter(content -> ids.contains(content.getId()))
                    .toList();
        } catch (IOException e) {
            throw new RuntimeException("폴더의 모든 파일 걍로를 스트림으로 나열 중 오류 발생", e);
        }
    }

    // 바이너리 파일 삭제
    @Override
    public boolean deleteById(UUID id) {
        Path path = resolvePath(id);

        try{
            Files.deleteIfExists(path);
            return true;
        } catch (IOException e){
            throw new RuntimeException("바이너리 파일 삭제 중 오류 발생", e);
        }
    }
}
