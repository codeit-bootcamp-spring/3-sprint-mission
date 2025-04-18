package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class FileMessageRepository implements MessageRepository {
    private static final Path FILE_PATH = Paths.get("data/message.ser");
    private final Map<UUID, Message> data;

    public FileMessageRepository() {
        this.data = readAll();
    }

    public FileMessageRepository(Map<UUID, Message> data) {
        this.data = data;
    }

    /**
     * 메시지 데이터를 직렬화하여 파일에 저장하는 메서드
     *
     * @throws RuntimeException 파일 생성/직렬화 중 예외가 발생한 경우
     */
    @Override
    public void saveAll() {
        try{
            Files.createDirectories(FILE_PATH.getParent());
            try(ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_PATH.toFile()))){
                oos.writeObject(data);
            }
        } catch (IOException e) {
            throw new RuntimeException("메시지 데이터 파일을 저장하는 중 오류가 발생하였습니다.");
        }
    }

    /**
     * 주어진 메시지를 파일에 저장하는 메서드
     *
     * @param message 저장할 메시지
     */
    @Override
    public void save(Message message) {
        data.put(message.getId(), message);
        saveAll();
    }

    /**
     * 파일에서 읽어온 메시지 데이터를 역직렬화하여 로드하는 메서드
     *
     * @return 역직렬화된 메시지 데이터
     * @throws RuntimeException 파일 역직렬화 중 예외가 발생한 경우
     */
    @SuppressWarnings("unchecked")
    public Map<UUID, Message> readAll() {
        if (!Files.exists(FILE_PATH)) {
            return new HashMap<>();
        }
        try(ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE_PATH.toFile()))
        ) {
            return (Map<UUID, Message>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("메시지 데이터 파일을 읽는 중 오류가 발생하였습니다.");
        }

    }

    /**
     * 주어진 id에 해당하는 메시지를 조회하는 메서드
     *
     * @param id 조회할 메시지의 ID
     * @return 조회된 메시지
     */
    @Override
    public Optional<Message> readById(UUID id) {
        return Optional.ofNullable(data.get(id));
    }

    /**
     * 주어진 메시지내용에 해당하는 메시지를 조회하는 메서드
     *
     * @param msgContent 조회할 메시지내용
     * @return 조회된 메시지
     */
    @Override
    public Optional<Message> readByMessageContent(String msgContent) {
        return data.values().stream()
                .filter(m->m.getMsgContent().equals(msgContent))
                .findFirst();
    }
}
