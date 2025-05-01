package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.dto.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Repository("fileBinaryContentRepository")
public class FileBinaryContentRepository implements BinaryContentRepository {
    private final Path profileImagesDir = Paths.get("data/binaryContents/profileImages");
    private final Path attachmentsDir = Paths.get("data/binaryContents/attachments");

    public FileBinaryContentRepository() {
        clearFile();
    }

    @Override
    public void save(BinaryContent binaryContent) {
        Path path = (binaryContent.getChannelId() == null) ? profileImagesDir : attachmentsDir;
        Path file = path.resolve((path == profileImagesDir ? binaryContent.getUserId() : binaryContent.getId()) + ".ser");

        try (OutputStream out = Files.newOutputStream(file);
            ObjectOutputStream oos = new ObjectOutputStream(out)) {
                oos.writeObject(binaryContent);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public BinaryContent loadByUserId(UUID userId) {
        Path file = profileImagesDir.resolve(userId + ".ser");
        return deserialize(file);
    }

    @Override
    public BinaryContent loadById(UUID id) {
        Path file = attachmentsDir.resolve(id + ".ser");
        return deserialize(file);
    }

    private BinaryContent deserialize(Path file) {
        if (!Files.exists(file)) {
            throw new IllegalArgumentException("[BinaryContent] 유효하지 않은 파일");
        }

        try (
            InputStream in = Files.newInputStream(file);
            ObjectInputStream ois = new ObjectInputStream(in)
        ) {
            return (BinaryContent) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("[BinaryContent] BinaryContent 파일 로드 실패", e);
        }
    }

    @Override
    public void delete(UUID id, UUID userId) {
        try {
            Path profileFile = profileImagesDir.resolve(userId + ".ser");
            Files.deleteIfExists(profileFile);

        } catch (IOException e) {
            throw new RuntimeException("[BinaryContent] 파일 삭제 실패 (" + id + ")", e);
        }
    }

    private void clearFile() {
        try {
            Files.createDirectories(profileImagesDir);
            Files.createDirectories(attachmentsDir);

            deleteDirectoryContents(profileImagesDir);
            deleteDirectoryContents(attachmentsDir);
        } catch (IOException e) {
            throw new RuntimeException("[BinaryContent] 폴더 초기화 실패", e);
        }
    }

    private void deleteDirectoryContents(Path dir) throws IOException {
        if (!Files.exists(dir) || !Files.isDirectory(dir)) return;
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir)) {
            for (Path entry : stream) {
                Files.deleteIfExists(entry);
            }
        }
    }
}
