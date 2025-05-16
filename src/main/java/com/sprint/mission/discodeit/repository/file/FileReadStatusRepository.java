package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "file")
@Repository
public class FileReadStatusRepository implements ReadStatusRepository {

    private static final Logger logger = Logger.getLogger(FileReadStatusRepository.class.getName());

    private final Path DIRECTORY;
    private final String EXTENSION = ".ser";

    public FileReadStatusRepository(
            @Value(".data") String fileDirectory
    ) {
        this.DIRECTORY = Paths.get(System.getProperty("user.dir"), fileDirectory, ReadStatus.class.getSimpleName());
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

    // 저장
    @Override
    public ReadStatus saveReadStatus(ReadStatus readStatus) {
        Path path = resolvePath(readStatus.getId());

        try (
                FileOutputStream fos = new FileOutputStream(path.toFile());
                ObjectOutputStream oos = new ObjectOutputStream(fos)
        ){
            oos.writeObject(readStatus);
        }
        catch (FileNotFoundException e) {
            // 파일 생성 실패 시 메시지
            throw new RuntimeException(path + " 경로에 파일을 생성할 수 없습니다 ", e);
        }
        catch (IOException e) {
            // 그 외 IO 예외
            throw new RuntimeException(" readStatus 파일 저장 중 오류 발생 ", e);
        }
        return readStatus;
    }

    // readStatus 아이디로 조회
    @Override
    public Optional<ReadStatus> findReadStatusById(UUID id) {
        Path path = resolvePath(id);

        if (!Files.exists(path)){
            return Optional.empty();
        }
        try(
                FileInputStream fis = new FileInputStream(path.toFile());
                ObjectInputStream ois = new ObjectInputStream(fis)
        ){
            return Optional.of((ReadStatus) ois.readObject());
        }
        catch (IOException | ClassNotFoundException e){
            throw new RuntimeException("id로 readStatus 조회 중 오류 ", e);
        }
    }

    // 유저 아이디로 조회
    @Override
    public List<ReadStatus> findUserReadStatus(UUID userId){
        // "user-data-improve/read-status" 경로 아래의 모든 파일 읽어들이기
        try (Stream<Path> paths = Files.list(DIRECTORY)) {
            return paths
                    // .ser로 끝나는 파일들 읽기
                    .filter(path ->  path.toString().endsWith(EXTENSION))
                    // 파일마다 읽어서 id가 일치하는지 확인함.
                    .map(path -> {
                        try (
                                FileInputStream fis = new FileInputStream(path.toFile());
                                ObjectInputStream ois = new ObjectInputStream(fis)
                        ) {
                            return (ReadStatus) ois.readObject();
                        } catch (IOException | ClassNotFoundException e) {
                            throw new RuntimeException(path.getFileName() + " 파일 읽기 실패: ", e);
                        }
                    })
                    .filter(readStatus -> readStatus.getUserId().equals(userId))
                    .toList();
        } catch (IOException e) {
            throw new RuntimeException(DIRECTORY + " 디렉토리 읽기 실패: ", e);
        }
    }

    // 채널 아이디로 조회
    @Override
    public List<ReadStatus> findReadStatusByChannelId(UUID channelId) {
        try (Stream<Path> paths = Files.list(DIRECTORY)) {
            return paths
                    .filter(path ->  path.toString().endsWith(EXTENSION))
                    .map(path -> {
                        try (
                                FileInputStream fis = new FileInputStream(path.toFile());
                                ObjectInputStream ois = new ObjectInputStream(fis)
                        ) {
                            return (ReadStatus) ois.readObject();
                        } catch (IOException | ClassNotFoundException e) {
                            throw new RuntimeException(path.getFileName() + " 파일 읽기 실패: ", e);
                        }
                    })
                    .filter(readStatus -> readStatus.getChannelId().equals(channelId))
                    .toList();
        } catch (IOException e) {
            throw new RuntimeException(DIRECTORY + " 디렉토리 읽기 실패: ", e);
        }
    }

    // 업데이트 (저장과 동일함)
    @Override
    public void updateReadStatus(ReadStatus readStatus){
        saveReadStatus(readStatus);
    }

    // readStatus 삭제
    @Override
    public void deleteReadStatusById(UUID id) {
        Path path = resolvePath(id);
        try{
            Files.delete(path);
        }
        catch (IOException e){
            throw new RuntimeException("파일 삭제 중 오류 ", e);
        }
    }

    // 채널 아이디로 삭제
    @Override
    public void deleteByChannelId(UUID channelId){
        this.findReadStatusByChannelId(channelId)
                .forEach(readStatus -> deleteReadStatusById(readStatus.getId()));
    }

}
