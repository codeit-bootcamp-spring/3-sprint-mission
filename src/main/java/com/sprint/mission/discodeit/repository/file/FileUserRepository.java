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
import java.time.Instant;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 이메일 필드 생긴 테스트용 유저 레포지토리 구현체
 */

@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "file")
@Repository
public class FileUserRepository implements UserRepository {

//    private static final String FILE_PATH = "data/user/userRepositoryAdv.ser";
//
//    private static final Logger logger = Logger.getLogger(FileUserRepository.class.getName());
//
//    private final Map<UUID, User> users = loadUsersFromFile();
//
//    // 유저 정보를 파일로 저장
//    private boolean saveUsersToFile(Map<UUID, User> users) {
//
//        try (FileOutputStream fileOut = new FileOutputStream(FILE_PATH);
//             ObjectOutputStream oos = new ObjectOutputStream(fileOut)) {
//
//            oos.writeObject(users);
//            return true;
//
//        } catch (FileNotFoundException e) {
//            // 파일 생성 실패 시 메시지
//            logger.log(Level.SEVERE, FILE_PATH + "경로에 파일을 생성할 수 없습니다: ", e);
//            return false;
//        } catch (IOException e) {
//            // 그 외 IO 예외
//            logger.log(Level.SEVERE, "유저 파일 저장 중 오류 발생", e);
//            return false;
//        }
//
//    }
//
//    // 유저 파일 읽어오기
//    private Map<UUID, User> loadUsersFromFile() {
//        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE_PATH))) {
//            return (Map<UUID, User>) ois.readObject();
//
//        } catch (FileNotFoundException e) {
//            return new LinkedHashMap<>();
//
//        } catch (IOException | ClassNotFoundException e) {
//            System.err.println("유저 파일 로드 중 오류 발생: " + e.getMessage());
//            e.printStackTrace();
//
//            return new LinkedHashMap<>();
//        }
//    }
//
//    // 유저 저장
//    @Override
//    public boolean saveUser(User user) {
//        users.put(user.getId(), user);
//
//        // 유저 저장 상태 확인
//        boolean success = saveUsersToFile(users);
//        if (!success) {
//            logger.warning("유저 저장에 실패했습니다: " + user.getId());
//        }
//        return success;
//    }
//
//    //유저 read
//    @Override
//    public List<User> findUserAll() {
//        return new ArrayList<>(users.values());
//    }
//
//    // 유저 read (id)
//    @Override
//    public User findUserById(UUID userId) {
//        return users.get(userId);
//    }
//
//    // 유저 read (name)
//    @Override
//    public Optional<User> findUserByName(String userName) {
//        return users.values().stream()
//                .filter(user -> user.getName().equalsIgnoreCase(userName))
//                .findFirst();
//    }
//
//    // 유저 조회 (이메일)
//    @Override
//    public Optional<User> findUserByEmail(String userEmail) {
//        return users.values().stream()
//                .filter(user -> user.getUserEmail().equalsIgnoreCase(userEmail))
//                .findFirst();
//    }
//
//    // 유저 이름 수정
//    @Override
//    public boolean updateUserName(User user, String newName){
//        user.updateName(newName);
//        user.updateUpdatedAt(Instant.now());
//        saveUser(user);
//
//        return true;
//    }
//
//    //유저 삭제
//    @Override
//    public void deleteUser(UUID userId) {
//        users.remove(userId);
//    }


    private static final Logger logger = Logger.getLogger(FileUserRepository.class.getName());

//    private final Map<UUID, User> users = loadUsersFromFile();

    private final Path DIRECTORY;
    private final String EXTENSION = ".ser";

    public FileUserRepository(
            @Value("${discodeit.repository.file-directory:data}") String fileDirectory
    ) {
        this.DIRECTORY = Paths.get(System.getProperty("user.dir"), fileDirectory, User.class.getSimpleName());
        if (Files.notExists(DIRECTORY)) {
            try {
                Files.createDirectories(DIRECTORY);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private Path resolvePath(UUID id) {
        return DIRECTORY.resolve(id + EXTENSION);
    }

    // 유저 저장
    @Override
    public User saveUser(User user) {
        Path path = resolvePath(user.getId());
        try (
                FileOutputStream fos = new FileOutputStream(path.toFile());
                ObjectOutputStream oos = new ObjectOutputStream(fos)
        ) {
            oos.writeObject(user);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return user;
    }

    //유저 read
    @Override
    public List<User> findUserAll() {
        try (Stream<Path> paths = Files.list(DIRECTORY)) {
            return paths
                    .filter(path -> path.toString().endsWith(EXTENSION))
                    .map(path -> {
                        try (
                                FileInputStream fis = new FileInputStream(path.toFile());
                                ObjectInputStream ois = new ObjectInputStream(fis)
                        ) {
                            return (User) ois.readObject();
                        } catch (IOException | ClassNotFoundException e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .toList();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // 유저 read (id)
    @Override
    public Optional<User> findUserById(UUID userId) {
        Path path = resolvePath(userId);
        if (Files.exists(path)) {
            try (
                    FileInputStream fis = new FileInputStream(path.toFile());
                    ObjectInputStream ois = new ObjectInputStream(fis)
            ) {
                User user = (User) ois.readObject();
                return Optional.of(user);
            } catch (IOException | ClassNotFoundException e) {
                throw new RuntimeException("FileUserRepository: 유저 조회에 실패했습니다", e);
            }
        }
        return Optional.empty();
    }

    // 유저 read (name)
    @Override
    public Optional<User> findUserByName(String userName) {
        return this.findUserAll().stream()
                .filter(user -> user.getName().equals(userName))
                .findFirst();
    }

    // 유저 조회 (이메일)
    @Override
    public Optional<User> findUserByEmail(String userEmail) {
        return this.findUserAll().stream()
                .filter(user -> user.getUserEmail().equals(userEmail))
                .findFirst();
    }

    // 유저 이름 수정
    @Override
    public boolean updateUserName(User user, String newName){
        user.updateName(newName);
        user.updateUpdatedAt(Instant.now());
        saveUser(user);

        return true;
    }

    //유저 삭제
    @Override
    public void deleteUser(UUID userId) {
        Path path = resolvePath(userId);
        try {
            Files.delete(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }    }


}
