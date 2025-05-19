package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Repository
@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "file")
public class FileUserRepository implements UserRepository {

    private final Path DIRECTORY;
    private final String EXTENSION = ".ser";

    /**
     * 파일 저장 디렉토리를 설정하고, 해당 디렉토리가 없는 경우 생성
     *
     * @param directory 루트 디렉토리
     * @throws RuntimeException 디렉토리 생성 중 예외가 발생한 경우
     */
    public FileUserRepository(@Value("${discodeit.repository.file-directory}") String directory) {
        this.DIRECTORY = Paths.get(System.getProperty("user.dir"), directory,
                User.class.getSimpleName());
        if (Files.notExists(DIRECTORY)) {
            try {
                Files.createDirectories(DIRECTORY);
            } catch (IOException e) {
                throw new RuntimeException("유저 저장 디렉토리를 생성하는 중 오류가 발생했습니다.");
            }
        }
    }

    /**
     * 주어진 ID에 해당하는 파일 경로 생성
     *
     * @param id 유저 ID
     * @return 해당 유저의 저장 경로
     */
    private Path resolvePath(UUID id) {
        return DIRECTORY.resolve(id + EXTENSION);
    }

    /**
     * 주어진 유저 데이터를 직렬화하여 파일에 저장
     *
     * @param user 저장할 유저
     * @return 저장된 유저
     * @throws RuntimeException 파일 직렬화 중 예외가 발생한 경우
     */
    @Override
    public User save(User user) {
        Path path = resolvePath(user.getId());
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(path.toFile()));) {
            oos.writeObject(user);
        } catch (IOException e) {
            throw new RuntimeException("User 데이터 파일을 저장하는 중 오류가 발생하였습니다.");
        }
        return user;
    }

    /**
     * 저장된 모든 유저 데이터를 역직렬화하여 로드
     *
     * @return 읽어온 유저 데이터
     * @throws RuntimeException 파일 역직렬화 중 예외가 발생한 경우
     */
    @Override
    public List<User> findAll() {
        try {
            return Files.list(DIRECTORY)
                    .filter(path -> path.toString().endsWith(EXTENSION))
                    .map(path -> {
                        try (ObjectInputStream ois = new ObjectInputStream(
                                new FileInputStream(path.toFile()));) {
                            return (User) ois.readObject();
                        } catch (IOException | ClassNotFoundException e) {
                            throw new RuntimeException("유저 데이터 파일을 읽는 중 오류가 발생하였습니다.");
                        }
                    })
                    .toList();
        } catch (IOException e) {
            throw new RuntimeException("유저 데이터 파일을 읽는 중 오류가 발생하였습니다.");
        }
    }

    /**
     * 주어진 ID에 해당하는 유저 조회
     *
     * @param id 조회할 유저 ID
     * @return 조회된 유저
     * @throws RuntimeException 파일 역직렬화 중 예외가 발생한 경우
     */
    @Override
    public Optional<User> findById(UUID id) {
        Path path = resolvePath(id);
        if (Files.exists(path)) {
            try (ObjectInputStream ois = new ObjectInputStream(
                    new FileInputStream(path.toFile()));) {
                return Optional.of((User) ois.readObject());
            } catch (IOException | ClassNotFoundException e) {
                throw new RuntimeException("User 데이터 파일을 읽는 중 오류가 발생하였습니다.");
            }
        }
        return Optional.empty();
    }

    /**
     * 주어진 유저명에 해당하는 유저 조회
     *
     * @param userName 조회할 유저의 유저명
     * @return 조회된 유저
     * @throws RuntimeException 파일 역직렬화 중 예외가 발생한 경우
     */
    @Override
    public Optional<User> findByUserName(String userName) {
        try {
            return Files.list(DIRECTORY)
                    .filter(path -> path.toString().endsWith(EXTENSION))
                    .map(path -> {
                        try (ObjectInputStream ois = new ObjectInputStream(
                                new FileInputStream(path.toFile()))) {
                            return (User) ois.readObject();
                        } catch (IOException | ClassNotFoundException e) {
                            throw new RuntimeException("유저 데이터 파일을 읽는 중 오류가 발생하였습니다.");
                        }
                    })
                    .filter(u -> u.getUsername().equals(userName))
                    .findFirst();
        } catch (IOException e) {
            throw new RuntimeException("유저 데이터 파일을 읽는 중 오류가 발생하였습니다.");
        }
    }

    /**
     * 주어진 ID에 해당하는 유저 존재여부 판단
     *
     * @param id 확인할 유저 ID
     * @return 해당 유저 존재여부
     */
    @Override
    public boolean existsById(UUID id) {
        return Files.exists(resolvePath(id));
    }

    /**
     * 주어진 유저명에 해당하는 유저 존재여부 판단
     *
     * @param userName 유저명
     * @return 해당 유저 존재여부
     * @throws RuntimeException 파일 역직렬화 중 예외가 발생한 경우
     */
    @Override
    public boolean existsByUserName(String userName) {
        try {
            return Files.list(DIRECTORY)
                    .filter(path -> path.toString().endsWith(EXTENSION))
                    .anyMatch(path -> {
                        try (ObjectInputStream ois = new ObjectInputStream(
                                new FileInputStream(path.toFile()))) {
                            User user = (User) ois.readObject();
                            return user.getUsername().equals(userName);
                        } catch (IOException | ClassNotFoundException e) {
                            throw new RuntimeException("유저 데이터 파일을 읽는 중 오류가 발생하였습니다.");
                        }
                    });
        } catch (IOException e) {
            throw new RuntimeException("유저 데이터 파일을 읽는 중 오류가 발생하였습니다.");
        }
    }

    /**
     * 주어진 email에 해당하는 유저 존재여부 판단
     *
     * @param email 이메일
     * @return 해당 유저 존재여부
     * @throws RuntimeException 파일 역직렬화 중 예외가 발생한 경우
     */
    @Override
    public boolean existsByEmail(String email) {
        try {
            return Files.list(DIRECTORY)
                    .filter(path -> path.toString().endsWith(EXTENSION))
                    .anyMatch(path -> {
                        try (ObjectInputStream ois = new ObjectInputStream(
                                new FileInputStream(path.toFile()))) {
                            User user = (User) ois.readObject();
                            return user.getEmail().equals(email);
                        } catch (IOException | ClassNotFoundException e) {
                            throw new RuntimeException("유저 데이터 파일을 읽는 중 오류가 발생하였습니다.");
                        }
                    });
        } catch (IOException e) {
            throw new RuntimeException("유저 데이터 파일을 읽는 중 오류가 발생하였습니다.");
        }
    }

    /**
     * 주어진 id에 해당하는 유저 삭제
     *
     * @param id 삭제 대상 유저 ID
     * @throws RuntimeException 데이터 삭제중 예외가 발생한 경우
     */
    @Override
    public void deleteById(UUID id) {
        Path path = resolvePath(id);
        try {
            Files.delete(path);
        } catch (IOException e) {
            throw new RuntimeException("User 데이터 삭제 중 오류가 발생하였습니다.");
        }
    }
}
