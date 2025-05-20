package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Repository
@Profile("file")
public class FileReadStatusRepository implements ReadStatusRepository {
    private final Path path;

    public FileReadStatusRepository(@Value("${storage.dirs.readStatuses}") String dir) {
        this.path = Paths.get(dir);
        clearFile();
    }

    @Override
    public void save(ReadStatus readStatus) {
        String filename = readStatus.getUserId().toString() + "_" + readStatus.getId() + ".ser";
        Path file = path.resolve(filename);

        try (
                OutputStream out = Files.newOutputStream(file);
                ObjectOutputStream oos = new ObjectOutputStream(out)
        ) {
            oos.writeObject(readStatus);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ReadStatus loadById(UUID id) {
        String suffix = "_" + id.toString() + ".ser";

        try (Stream<Path> files = Files.list(path)) {
            Optional<Path> match = files
                    .filter(p -> p.getFileName().toString().endsWith(suffix))
                    .findFirst();

            Path file = match.orElseThrow(() ->
                    new IllegalArgumentException("[ReadStatus] 유효하지 않은 readStatus 파일 (" + id + ".ser)")
            );

            return deserialize(file);
        } catch (IOException e) {
            throw new RuntimeException("[ReadStatus] 파일 로드 중 오류 발생", e);
        }
    }

    @Override
    public List<ReadStatus> loadAllByUserId(UUID id) {
        if (Files.notExists(path)) {
            return Collections.emptyList();
        }

        String prefix = id.toString() + "_";

        try (Stream<Path> stream = Files.list(path)) {
            return stream
                    .filter(p -> {
                        String name = p.getFileName().toString();
                        return name.startsWith(prefix) && name.endsWith(".ser");
                    })
                    .map(this::deserialize)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException("[ReadStatus] 사용자 상태 목록 조회 실패", e);
        }
    }

    @Override
    public void deleteByUserId(UUID userId) {
        String idString = userId.toString();
        try (Stream<Path> files = Files.list(path)) {
            files
                    .filter(p -> {
                        String name = p.getFileName().toString();
                        return name.contains(idString) && name.endsWith(".ser");
                    })
                    .forEach(p -> {
                        try {
                            Files.deleteIfExists(p);
                        } catch (IOException e) {
                            throw new RuntimeException("[ReadStatus] 파일 삭제 실패 (" + p.getFileName() + ")", e);
                        }
                    });
        } catch (IOException e) {
            throw new RuntimeException("[ReadStatus] ReadStatus 폴더 접근 실패", e);
        }
    }

    private ReadStatus deserialize(Path file) {
        if (Files.notExists(file)) {
            throw new IllegalArgumentException("[ReadStatus] 유효하지 않은 파일");
        }

        try (
                InputStream in = Files.newInputStream(file);
                ObjectInputStream ois = new ObjectInputStream(in)
        ) {
            return (ReadStatus) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("[ReadStatus] ReadStatus 파일 로드 실패", e);
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
