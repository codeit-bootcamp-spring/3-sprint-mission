package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Repository
@Profile("file")
public class FileBinaryContentRepository implements BinaryContentRepository {
    private final Path path;

    public FileBinaryContentRepository(@Value("${storage.dirs.binaryContents}") String dir) {
        this.path = Paths.get(dir);
        clearFile();
    }

    @Override
    public void save(BinaryContent binaryContent) {
        String filename = binaryContent.getId().toString() + ".ser";
        Path file = path.resolve(filename);

        try (
                OutputStream out = Files.newOutputStream(file);
                ObjectOutputStream oos = new ObjectOutputStream(out)
        ) {
            oos.writeObject(binaryContent);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public BinaryContent loadById(UUID id) {
        Path file = path.resolve(id.toString() + ".ser");
        return deserialize(file);
    }
//
//    public BinaryContent loadByUserId(UUID userId) {
//
//    }

    @Override
    public List<BinaryContent> loadAll() {
        List<BinaryContent> binaryContents = new ArrayList<>();

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(path, "*.ser")) {
            for (Path p : stream) {
                binaryContents.add(deserialize(p));
            }
        } catch (IOException e) {
            throw new RuntimeException("[BinaryContent] profileImages 폴더 읽기 실패", e);
        }

        return binaryContents;
    }

    @Override
    public void delete(UUID id) {
        try {
            Path deletePath = path.resolve(id + ".ser");
            Files.deleteIfExists(deletePath);

        } catch (IOException e) {
            throw new RuntimeException("[BinaryContent] 파일 삭제 실패 (" + id + ")", e);
        }
    }

    private BinaryContent deserialize(Path file) {
        if (Files.notExists(file)) {
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

    private void clearFile() {
        try {
            if (Files.exists(path)) {
                try (DirectoryStream<Path> stream = Files.newDirectoryStream(path)) {
                    for (Path filePath : stream) {
                        Files.deleteIfExists(filePath);
                    }
                }
            } else {
                Files.createDirectories(path);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
