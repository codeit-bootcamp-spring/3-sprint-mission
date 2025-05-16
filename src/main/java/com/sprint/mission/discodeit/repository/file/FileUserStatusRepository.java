package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.io.*;
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
public class FileUserStatusRepository implements UserStatusRepository {

    private static final Logger logger = Logger.getLogger(FileUserStatusRepository.class.getName());

    private final Path DIRECTORY;
    private final String EXTENSION = ".ser";

    public FileUserStatusRepository(
            @Value("${discodeit.repository.file-directory:data}") String fileDirectory
    ) {
        this.DIRECTORY = Paths.get(System.getProperty("user.dir"), fileDirectory, UserStatus.class.getSimpleName());
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


    // 유저 상태 저장
    @Override
    public UserStatus saveUserStatus(UserStatus userStatus) {
        Path path = resolvePath(userStatus.getId());
        
        try (
                FileOutputStream fos = new FileOutputStream(path.toFile());
                ObjectOutputStream oos = new ObjectOutputStream(fos)
        ) {
            oos.writeObject(userStatus);
        }
        catch (FileNotFoundException e) {
            // 파일 생성 실패 시 메시지
            throw new RuntimeException(path + " 경로에 파일을 생성할 수 없습니다 ", e);
        }
        catch (IOException e) {
            // 그 외 IO 예외
            throw new RuntimeException(" 접속 상태 파일 저장 중 오류 발생 ", e);
        }
        return userStatus;
    }

    @Override
    public Optional<UserStatus> findById(UUID id){
        Path path = resolvePath(id);

        if (!Files.exists(path)){
            return Optional.empty();
        }

        try (
                FileInputStream fis = new FileInputStream(path.toFile());
                ObjectInputStream ois = new ObjectInputStream(fis)
        ) {
            return Optional.of((UserStatus) ois.readObject());

        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("id로 userStatus 조회 중 오류 ", e);
        }
    }

    // 특정 유저의 상태 조회
    @Override
    public Optional<UserStatus> findStatus(UUID userId) {

        Optional<UserStatus> result = findAllStatus().stream()
                .filter(userStatus -> userStatus.getUserId().equals(userId))
                .findFirst();

        if (result.isEmpty()) {
            System.out.println("해당 유저의 상태 정보가 존재하지 않습니다: " + userId);
        }

        return result;
    }

    // 등록된 전체 상태 조회
    @Override
    public List<UserStatus> findAllStatus() {

        try (Stream<Path> paths = Files.list(DIRECTORY)){
            return paths
                    .filter(path -> path.toString().endsWith(EXTENSION))
                    .map(path -> {
                        try(
                                FileInputStream fis = new FileInputStream(path.toFile());
                                ObjectInputStream ois = new ObjectInputStream(fis);
                        ){
                            return (UserStatus) ois.readObject();
                        }
                        catch (IOException | ClassNotFoundException e){
                            throw new RuntimeException("userStatus 전체 조회 실패 ", e);
                        }

                    })
                    .toList();
        }
        catch (IOException e){
            throw new RuntimeException(DIRECTORY + " UserStatus 폴더 읽기 실패: ", e);
        }
    }

    // 유저 상태 업데이트
    @Override
    public boolean updateUserStatus(UserStatus userStatus) {
        Path path = resolvePath(userStatus.getId());
        try (
                FileOutputStream fos = new FileOutputStream(path.toFile());
                ObjectOutputStream oos = new ObjectOutputStream(fos)
        ) {
            oos.writeObject(userStatus);
        }
        catch (FileNotFoundException e) {
            // 파일 생성 실패 시 메시지
            throw new RuntimeException(path + " 경로에 파일을 생성할 수 없습니다 ", e);
        }
        catch (IOException e) {
            // 그 외 IO 예외
            throw new RuntimeException("userStatus 업데이트 중 오류 발생 ", e);
        }
        return true;
    }

    // 아이디로 삭제
    @Override
    public void deleteById(UUID id) {
        Path path = resolvePath(id);
        try {
            Files.delete(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // 유저 아이디로 삭제
    @Override
    public void deleteUserStatus(UUID userId) {
        this.findStatus(userId)
                .ifPresent(userStatus -> this.deleteById(userStatus.getId()));
    }
}
