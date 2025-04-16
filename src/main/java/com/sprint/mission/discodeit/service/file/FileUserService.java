package com.sprint.mission.discodeit.service.file;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;

/**
 * 파일 기반 사용자 서비스 구현 사용자 정보를 파일에 저장하고 로드하는 기능을 제공. 사용자 생성, 조회, 업데이트, 삭제 기능을 제공.
 * 사용자 정보는 UUID.ser 형식으로 파일에 저장 사용자 정보는 객체 직렬화와 역직렬화를 사용해 저장.
 */
public class FileUserService implements UserService {

    private final Path dataDirectory;

    public FileUserService() {
        // 사용자 데이터 디렉토리 경로 설정. 프로젝트 루트 디렉토리에 data/users 폴더 생성
        this.dataDirectory = Paths.get(System.getProperty("user.dir"), "data", "users");
        init();
    }

    private void init() { // 사용자 데이터 디렉토리 초기화
        if (!Files.exists(dataDirectory)) {
            try {
                Files.createDirectories(dataDirectory);
            } catch (IOException e) {
                throw new RuntimeException("사용자 데이터 디렉토리 생성 실패", e);
            }
        }
    }

    private Path getUserPath(UUID userId) {
        // 사용자 정보 파일 경로 반환. data/users 폴더에 userId.ser 파일 생성
        return dataDirectory.resolve(userId.toString() + ".ser");
    }

    private synchronized void saveUser(User user) {
        // 사용자 정보 파일 경로 반환. data/users 폴더에 userId.ser 파일 생성   
        Path userPath = getUserPath(user.getUserId());
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(userPath.toFile()))) {
            oos.writeObject(user);
        } catch (IOException e) {
            throw new RuntimeException("사용자 저장 실패: " + user.getUserId(), e);
        }
    }

    private synchronized User loadUser(Path path) {
        // 파일에서 사용자 정보 로드. ObjectInputStream 사용해 객체 역직렬화
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path.toFile()))) {
            return (User) ois.readObject();
        } catch (IOException e) {
            throw new RuntimeException("파일 읽기 오류: " + path + ", 원인: " + e.getMessage(), e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("유효하지 않은 사용자 데이터 형식: " + path, e);
        } catch (ClassCastException e) {
            throw new RuntimeException("잘못된 사용자 객체 형식: " + path, e);
        }
    }

    @Override
    public User createUser(String userName, String email, String password) {
        // 입력값 검증
        if (userName == null || userName.isEmpty()) {
            throw new IllegalArgumentException("사용자 이름은 비어있을 수 없습니다.");
        }
        if (email == null || email.isEmpty()) {
            throw new IllegalArgumentException("이메일은 비어있을 수 없습니다.");
        }
        if (password == null || password.isEmpty()) {
            throw new IllegalArgumentException("비밀번호는 비어있을 수 없습니다.");
        }
        // 새로운 사용자 생성 및 저장
        User user = new User(userName, email, password);
        saveUser(user);
        return user;
    }

    @Override
    public synchronized User getUserById(UUID userId) {
        // 사용자 ID로 사용자 정보 조회
        Path userPath = getUserPath(userId);
        if (Files.exists(userPath)) {
            return loadUser(userPath);
        }
        return null;
    }

    @Override
    public List<User> getAllUsers() {
        // 모든 사용자 목록 조회
        try (Stream<Path> pathStream = Files.list(dataDirectory)) {
            return pathStream
                    .filter(path -> path.toString().endsWith(".ser"))
                    .map(this::loadUser)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException("사용자 목록 로드 실패", e);
        }
    }

    @Override
    public synchronized void updateUserName(UUID id, String newUsername) {
        // 사용자 이름 업데이트
        User user = getUserById(id);
        if (user == null) {
            throw new IllegalArgumentException("존재하지 않는 사용자 ID입니다.");
        }
        user.updateUserName(newUsername);
        saveUser(user);
    }

    @Override
    public void updateUserEmail(UUID id, String newEmail) {
        // 사용자 이메일 업데이트
        User user = getUserById(id);
        if (user == null) {
            throw new IllegalArgumentException("존재하지 않는 사용자 ID입니다.");
        }
        user.updateEmail(newEmail);
        saveUser(user);
    }

    @Override
    public void updateUserPassword(UUID id, String newPassword) {
        // 비밀번호 유효성 검사 추가
        if (newPassword == null) {
            throw new IllegalArgumentException("비밀번호는 null일 수 없습니다.");
        }
        if (newPassword.isEmpty()) {
            throw new IllegalArgumentException("비밀번호는 빈 값일 수 없습니다.");
        }
        // 사용자 비밀번호 업데이트
        User user = getUserById(id);
        if (user == null) {
            throw new IllegalArgumentException("존재하지 않는 사용자 ID입니다.");
        }
        user.updatePassword(newPassword);
        saveUser(user);
    }

    @Override
    public void deleteUser(UUID userId) {
        // 사용자 삭제
        try {
            Files.deleteIfExists(getUserPath(userId));
        } catch (IOException e) {
            throw new RuntimeException("사용자 삭제 실패: " + userId, e);
        }
    }
}
