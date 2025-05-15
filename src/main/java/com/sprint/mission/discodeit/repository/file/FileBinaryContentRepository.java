package com.sprint.mission.discodeit.repository.file;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;

@Repository
public class FileBinaryContentRepository implements BinaryContentRepository {

    private final Path dataDirectory;

    public FileBinaryContentRepository(@Value("${discodeit.repository.file-directory}") String baseDir) {
        this.dataDirectory = Paths.get(System.getProperty("user.dir"), baseDir, "binarycontents");
        if (!Files.exists(dataDirectory)) {
            try {
                Files.createDirectories(dataDirectory);
            } catch (IOException e) {
                throw new RuntimeException("바이너리 콘텐츠 데이터 디렉토리 생성 실패", e);
            }
        }
    }

    private Path getBinaryContentPath(UUID id) {
        return dataDirectory.resolve(id.toString() + ".ser");
    }

    private void saveBinaryContent(BinaryContent binaryContent) {
        Path binaryContentPath = getBinaryContentPath(binaryContent.getId());
        try (FileOutputStream fos = new FileOutputStream(binaryContentPath.toFile());
            ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(binaryContent);
        } catch (IOException e) {
            throw new RuntimeException("바이너리 콘텐츠 저장 실패: " + binaryContent.getId(), e);
        }
    }

    private BinaryContent loadBinaryContent(Path path) {
        try (FileInputStream fis = new FileInputStream(path.toFile());
            ObjectInputStream ois = new ObjectInputStream(fis)) {
            return (BinaryContent) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("바이너리 콘텐츠 로드 실패: " + path, e);
        }
    }

    @Override
    public BinaryContent save(BinaryContent binaryContent) {
        saveBinaryContent(binaryContent);
        return binaryContent;
    }

    @Override
    public Optional<BinaryContent> findById(UUID id) {
        Path binaryContentPath = getBinaryContentPath(id);
        if (Files.exists(binaryContentPath)) {
            return Optional.of(loadBinaryContent(binaryContentPath));
        }
        return Optional.empty();
    }

    @Override
    public List<BinaryContent> findAllByIdIn(List<UUID> ids) {
        return ids.stream()
                .map(this::getBinaryContentPath)
                .filter(Files::exists)
                .map(this::loadBinaryContent)
                .collect(Collectors.toList());
    }

    @Override
    public boolean existsById(UUID id) {
        return Files.exists(getBinaryContentPath(id));
    }

    @Override
    public void deleteById(UUID id) {
        try {
            Files.deleteIfExists(getBinaryContentPath(id));
        } catch (IOException e) {
            throw new RuntimeException("바이너리 콘텐츠 삭제 실패: " + id, e);
        }
    }
} 