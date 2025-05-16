package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.*;

import java.util.logging.Logger;
import java.util.stream.Stream;


@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "file")
@Repository
public class FileMessageRepository implements MessageRepository {

    private static final Logger logger = Logger.getLogger(FileMessageRepository.class.getName());

    private final Path DIRECTORY;
    private final String EXTENSION = ".ser";


    public FileMessageRepository(
            @Value("${discodeit.repository.file-directory:data}") String fileDirectory
    ) {
        this.DIRECTORY = Paths.get(System.getProperty("user.dir"), fileDirectory, Message.class.getSimpleName());
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

    // 메시지 생성
    @Override
    public Message saveMessage(Message message) {
        Path path = resolvePath(message.getMessageId());
        try (
                FileOutputStream fos = new FileOutputStream(path.toFile());
                ObjectOutputStream oos = new ObjectOutputStream(fos)
        ) {
            oos.writeObject(message);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return message;
    }

//    // 메시지에 첨부파일 id 연결
//    public boolean addAttachedFileId(UUID messageId, UUID attachedFileId) {
//        Optional<Message> message = findMessageById(messageId);
//
//        message.getAttachedFileIds().add(attachedFileId);
//
//        boolean success = saveMessageToFile(messages);
//
//        if (!success) {
//            logger.warning("메시지 정보 저장에 실패했습니다: " + message.getMessageId());
//        }
//        return true;
//    }

//    // 메시지 내용 수정
//    @Override
//    public boolean updateMessage(UUID messageId, String newMessageContent) {
//        Message message = findMessageById(messageId);
//        message.updateMessageContent(newMessageContent);
//        message.updateUpdatedAt(Instant.now());
//
//        boolean success = saveMessageToFile(messages);
//        if (!success) {
//            logger.warning("메시지 저장에 실패했습니다: " + message.getMessageId());
//        }
//        return success;
//    }

    // 전체 메시지 조회
    @Override
    public List<Message> findAllMessage() {
        try (Stream<Path> paths = Files.list(DIRECTORY)) {
            return paths
                    .filter(path -> path.toString().endsWith(EXTENSION))
                    .map(path -> {
                        try (
                                FileInputStream fis = new FileInputStream(path.toFile());
                                ObjectInputStream ois = new ObjectInputStream(fis)
                        ) {
                            return (Message) ois.readObject();
                        } catch (IOException | ClassNotFoundException e) {
                            throw new RuntimeException("FileMessageRepository: 메시지 파일 읽기 중 오류 발생 ", e);
                        }
                    })
                    .toList();
        } catch (IOException e) {
            throw new RuntimeException("FileMessageRepository: 메시지 폴더 읽기 중 오류 발생 ", e);
        }
    }

    // 아이디로 메시지 조회
    @Override
    public Optional<Message> findMessageById(UUID messageId) {
        Path path = resolvePath(messageId);
        if (Files.exists(path)) {
            try (
                    FileInputStream fis = new FileInputStream(path.toFile());
                    ObjectInputStream ois = new ObjectInputStream(fis)
            ) {
                Message message = (Message) ois.readObject();
                return Optional.of(message);
            } catch (IOException | ClassNotFoundException e) {
                throw new RuntimeException("FileMessageRepository: 메시지 파일 읽기 중 오류 발생 ", e);
            }
        }
        return Optional.empty();
    }

    // 특정 채널의 메시지 조회
    @Override
    public List<Message> findMessageByChannel(UUID channelId) {
        try (Stream<Path> paths = Files.list(DIRECTORY)) {
            return paths
                    .filter(path -> path.toString().endsWith(EXTENSION))
                    .map(path -> {
                        try (
                                FileInputStream fis = new FileInputStream(path.toFile());
                                ObjectInputStream ois = new ObjectInputStream(fis)
                        ) {
                            return (Message) ois.readObject();
                        } catch (IOException | ClassNotFoundException e) {
                            throw new RuntimeException("FileMessageRepository: 메시지 파일 읽기 중 오류 발생 ", e);
                        }
                    })
                    .filter(message -> message.getChannelId().equals(channelId))
                    .toList();
        } catch (IOException e) {
            throw new RuntimeException("FileMessageRepository: 메시지 폴더 읽기 중 오류 발생 ", e);
        }
    }

    // 특정 유저의 메시지 조회
    @Override
    public List<Message> userMessage(UUID senderId) {
        try (Stream<Path> paths = Files.list(DIRECTORY)) {
            return paths
                    .filter(path -> path.toString().endsWith(EXTENSION))
                    .map(path -> {
                        try (
                                FileInputStream fis = new FileInputStream(path.toFile());
                                ObjectInputStream ois = new ObjectInputStream(fis)
                        ) {
                            return (Message) ois.readObject();
                        } catch (IOException | ClassNotFoundException e) {
                            throw new RuntimeException("FileMessageRepository: 메시지 파일 읽기 중 오류 발생 ", e);
                        }
                    })
                    .filter(message -> message.getSenderId().equals(senderId))
                    .toList();
        } catch (IOException e) {
            throw new RuntimeException("FileMessageRepository: 메시지 폴더 읽기 중 오류 발생 ", e);
        }
    }

    // 메시지 삭제
    @Override
    public void deletedMessage(UUID messageId) {
        Path path = resolvePath(messageId);
        try {
            Files.delete(path);
        } catch (IOException e) {
            throw new RuntimeException("FileMessageRepository: 메시지 삭제 중 오류 발생 ", e);
        }
    }
}
