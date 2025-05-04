package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

public class FileBinaryContentRepository implements BinaryContentRepository {

    private final Path DIRECTORY;
    private final String EXTENSION = ".ser"; // 파일 확장자

    public FileBinaryContentRepository() {
        this.DIRECTORY = Paths.get(System.getProperty("user.dir"), "file-data-map", BinaryContent.class.getSimpleName());
        if (Files.notExists(DIRECTORY)) {
            try {
                Files.createDirectories(DIRECTORY); // 디렉토리가 없으면 생성
            } catch (IOException e) {
                throw new RuntimeException("Error creating directory", e);
            }
        }
    }

    private Path resolvePath(UUID id) {
        return DIRECTORY.resolve(id + EXTENSION); // UUID를 기반으로 파일 경로 생성
    }

    @Override
    public BinaryContent save(BinaryContent binaryContent) {
        Path path = resolvePath(binaryContent.getId());
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(path.toFile()))) {
            oos.writeObject(binaryContent); // BinaryContent 객체를 파일에 저장
        } catch (IOException e) {
            throw new RuntimeException("Error writing BinaryContent to file", e);
        }
        return binaryContent;
    }

    @Override
    public BinaryContent findById(UUID id) {
        Path path = resolvePath(id);
        if (Files.exists(path)) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path.toFile()))) {
                return (BinaryContent) ois.readObject(); // 파일에서 BinaryContent 객체 읽기
            } catch (IOException | ClassNotFoundException e) {
                throw new RuntimeException("Error reading BinaryContent from file", e);
            }
        }
        return null; // 해당 파일이 없으면 null 반환
    }

    @Override
    public List<BinaryContent> findAllByIdIn(List<UUID> ids) {
        List<BinaryContent> binaryContents = new ArrayList<>();
        for (UUID id : ids) {
            BinaryContent binaryContent = findById(id);
            if (binaryContent != null) {
                binaryContents.add(binaryContent);
            }
        }
        return binaryContents;
    }

    @Override
    public List<BinaryContent> findAll() {
        List<BinaryContent> binaryContents = new ArrayList<>();
        try {
            Files.list(DIRECTORY) // 디렉토리 내 모든 파일 탐색
                    .filter(path -> path.toString().endsWith(EXTENSION)) // 확장자가 ".ser"인 파일만 필터링
                    .forEach(path -> {
                        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path.toFile()))) {
                            binaryContents.add((BinaryContent) ois.readObject()); // 파일에서 BinaryContent 객체 읽기
                        } catch (IOException | ClassNotFoundException e) {
                            e.printStackTrace(); // 예외 처리
                        }
                    });
        } catch (IOException e) {
            throw new RuntimeException("Error listing files", e);
        }
        return binaryContents; // 모든 BinaryContent 객체를 반환
    }

    @Override
    public boolean existsById(UUID id) {
        Path path = resolvePath(id);
        return Files.exists(path); // 해당 id의 파일이 존재하는지 확인
    }

    @Override
    public void delete(UUID id) {
        Path path = resolvePath(id);
        try {
            Files.delete(path); // 해당 id의 파일을 삭제
        } catch (IOException e) {
            throw new RuntimeException("Error deleting BinaryContent", e);
        }
    }
}

