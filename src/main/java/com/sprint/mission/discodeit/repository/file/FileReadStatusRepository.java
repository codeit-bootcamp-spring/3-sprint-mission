package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class FileReadStatusRepository implements ReadStatusRepository {

    private final Path DIRECTORY;
    private final String EXTENSION = ".ser";

    public FileReadStatusRepository() { // 생성자에서 File의 이름과 Path를 생성하고 Directory 없으면 생성
        this.DIRECTORY = Paths.get(System.getProperty("readStatus.dir"), "file-data-map", ReadStatus.class.getSimpleName());
        if (Files.notExists(DIRECTORY)) {
            try {
                Files.createDirectories(DIRECTORY);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private Path resolvePath(UUID id) {return DIRECTORY.resolve(id + EXTENSION);} // id만 있으면 EXTENSION을 합쳐 Path를 반환

    @Override
    public ReadStatus save(ReadStatus readStatus) {
        Path path = resolvePath(readStatus.getId());
        try (
                ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(path.toFile()));
        ) {
            oos.writeObject(readStatus);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return readStatus;
    }

    @Override
    public ReadStatus findById(UUID id) {
        ReadStatus readStatusNullable = null;
        Path path = resolvePath(id);
        if (Files.exists(path)) {
            try (
                    ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path.toFile()));
                    )
            {
                readStatusNullable = (ReadStatus) ois.readObject();
            } catch (IOException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }

        return readStatusNullable;
    }

    @Override
    public List<ReadStatus> findByChannelId(UUID channelId) {
        // 1. 반환 리스트 초기화
        List<ReadStatus> readStatusList = new ArrayList<>();

        // 2. channelId에 해당하는 파일 경로 계산
        Path path = resolvePath(channelId);

        // 3. 파일이 존재하면 파일에서 데이터 읽기
        if (Files.exists(path)) {
            try (
                    ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path.toFile()));
            )
            {
                Object o = ois.readObject();
//                readStatusList = (List<ReadStatus>) ois.readObject();
                // 5. readObject가 List<ReadStatus>라면, 데이터를 읽어 리스트에 저장
                if (o instanceof List<?>) {
                    List<?> tempList = (List<?>) o;
                    // 리스트가 ReadStatus 객체들로 구성되어 있으면
                    if (!tempList.isEmpty() && tempList.get(0) instanceof ReadStatus) {
                        readStatusList = (List<ReadStatus>) tempList;
                    }
                }

            } catch (IOException | ClassNotFoundException e) {
                // 예외 발생 시 예외 메시지 출력 (optional: 로깅 추가)
                e.printStackTrace();
                // 실패 시 빈 리스트 반환
                return readStatusList;
            }
        }

        // 6. load한 ReadStatus 리스트 반환
        return readStatusList;
    }

    @Override
    public List<ReadStatus> findAll() {
        try {
            return Files.list(DIRECTORY)
                    .filter(path -> path.toString().endsWith(EXTENSION))
                    .map(path -> {
                        try (
                                ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path.toFile()));
                        ) {
                            return (ReadStatus) ois.readObject();
                        } catch (IOException | ClassNotFoundException e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .toList();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ReadStatus findByUserIdAndChannelId(UUID userId, UUID channelId) {
        // 해당 채널 ID와 관련된 파일 경로
        Path path = resolvePath(channelId);

        // 파일 존재 유무 확인
        if (Files.exists(path)) {
            try (
                    ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path.toFile()));
                    ) {
                List<ReadStatus> readStatusList = (List<ReadStatus>) ois.readObject();
                // userId와 channelId가 일치하는 ReadStatus 찾기
                return readStatusList.stream()
                        .filter(readStatus -> readStatus.getUserId().equals(userId) && readStatus.getChannelId().equals(channelId))
                        .findFirst()
                        .orElse(null); // 없으면 null 반환
            } catch (IOException | ClassNotFoundException e) {
                throw new RuntimeException("Error reading from file", e);
            }
        }
        return null; // 파일이 없으면 null 반환
    }

    @Override
    public List<ReadStatus> findByUserId(UUID userId) {
        // 1. 반환 리스트 초기화
        List<ReadStatus> readStatusList = new ArrayList<>();

        // 2. userId에 해당하는 파일 경로 계산
        Path path = resolvePath(userId);

        // 3. 파일이 존재하면 파일에서 데이터 읽기
        if (Files.exists(path)) {
            try (
                    ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path.toFile()));
            )
            {
                Object o = ois.readObject();
//                readStatusList = (List<ReadStatus>) ois.readObject();
                // 5. readObject가 List<ReadStatus>라면, 데이터를 읽어 리스트에 저장
                if (o instanceof List<?>) {
                    List<?> tempList = (List<?>) o;
                    // 리스트가 ReadStatus 객체들로 구성되어 있으면
                    if (!tempList.isEmpty() && tempList.get(0) instanceof ReadStatus) {
                        readStatusList = (List<ReadStatus>) tempList;
                    }
                }

            } catch (IOException | ClassNotFoundException e) {
                // 예외 발생 시 예외 메시지 출력 (optional: 로깅 추가)
                e.printStackTrace();
                // 실패 시 빈 리스트 반환
                return readStatusList;
            }
        }

        // 6. load한 ReadStatus 리스트 반환
        return readStatusList;
    }

    @Override
    public void delete(UUID id) {
        Path path = resolvePath(id);
        if (Files.exists(path)) {
            try {
                Files.delete(path); // 해당 파일 삭제
            } catch (IOException e) {
                throw new RuntimeException("Error deleting file", e);
            }
        } else {
            throw new NoSuchElementException("ReadStatus with id " + id + " not found");
        }

    }

    @Override
    public boolean existByChannelIdAndUserId(UUID userId, UUID channelId) {
        // 해당 채널에 대한 파일을 찾ㄱ ㅗ존재 여부 확인
        return findByUserIdAndChannelId(userId, channelId) != null;
    }

    @Override
    public ReadStatus update(ReadStatus readStatus) {
        Path path = resolvePath(readStatus.getId());

        List<ReadStatus> readStatusList = new ArrayList<>();

        // 기존 파일 읽기
        if (Files.exists(path)) {
            try (
                    ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path.toFile()))) {
                        readStatusList = (List<ReadStatus>) ois.readObject();
                    } catch (ClassNotFoundException | IOException e) {
                throw new RuntimeException("Error reading from file", e);
            }
        }

        // 업덷이트할 ReadStatus가 리스트에 있는지 확인
        Optional<ReadStatus> existing = readStatusList.stream()
                .filter(readStatus1 -> readStatus1.getId().equals(readStatus.getId()))
                .findFirst();

        if (existing.isPresent()) {
            // 기존 값 수정
            readStatusList.remove(existing.get());
            readStatusList.add(readStatus);
            // 파일로 다시 저장
            this.save(readStatus);
            return readStatus;
        } else {
            throw new NoSuchElementException("ReadStatus with id " + readStatus.getId() + " not found");
        }
    }
}
