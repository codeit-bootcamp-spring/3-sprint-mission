package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.MessageService;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.UUID;


public class FileMessageService implements MessageService {
    private static final Path FILE_PATH = Paths.get("data/message.ser");
    private final Map<UUID, Message> data = getMessages();

    /**
     * 주어진 채널, 유저, 메시지내용으로 메시지를 생성하는 메서드
     *
     * @param sendChannel 메시지를 보낼 채널
     * @param sendUser 메시지를 보낸 유저
     * @param msgContent 생성할 메시지 내용
     * @return 생성된 메시지
     */
    @Override
    public Message createMessage(Channel sendChannel, User sendUser, String msgContent) {
        // 메시지 생성
        Message msg = new Message(sendChannel, sendUser, msgContent);
        data.put(msg.getId(), msg);
        // 파일 저장
        saveMessages();
        return msg;
    }

    /**
     * 파일에서 읽어온 메시지 데이터를 역직렬화하여 로드하는 메서드
     *
     * @return 역직렬화된 메시지 데이터
     * @throws RuntimeException 파일 역직렬화 중 예외가 발생한 경우
     */
    @Override
    public Map<UUID, Message> getMessages() {
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
     * @throws NoSuchElementException 해당 ID의 메시지가 존재하지 않는 경우
     */
    @Override
    public Message getMessageById(UUID id) {
        Message msg = data.get(id);
        if (msg == null) {
            throw new NoSuchElementException("해당 ID의 메시지가 존재하지 않습니다.");
        }
        return msg;
    }

    /**
     * 주어진 메시지내용에 해당하는 메시지를 조회하는 메서드
     *
     * @param msgContent 조회할 메시지내용
     * @return 조회된 메시지
     * @throws NoSuchElementException 해당 내용의 메시지가 존재하지 않는 경우
     */
    @Override
    public Message getMessageByContent(String msgContent) {
        // 검색 결과가 여러 개인 경우, 가장 먼저 등록된 메시지를 조회
        // data를 순회하며 메시지 내용으로 검색
        return data.values().stream()
                .filter(m->m.getMsgContent().equals(msgContent))
                .findFirst()
                .orElseThrow(()->new NoSuchElementException("해당 내용의 메시지를 찾을 수 없습니다."));
    }

    /**
     * 주어진 메시지를 새로운 메시지내용으로 수정하는 메서드
     *
     * @param msg 수정할 대상 메시지
     * @param msgContent 새로운 메시지내용
     * @return 수정된 메시지
     */
    @Override
    public Message updateMessage(Message msg, String msgContent) {
        Message targetMsg = getMessageById(msg.getId());
        // 메시지 내용 업데이트
        targetMsg.updateMsgContent(msgContent);
        saveMessages();
        return targetMsg;
    }

    /**
     * 주어진 id에 해당하는 메시지를 삭제하는 메서드
     *
     * @param id 삭제할 대상 메시지 id
     * @return 삭제된 메시지
     */
    @Override
    public Message deleteMessage(UUID id) {
        Message targetMsg = getMessageById(id);
        targetMsg.deleteMsgContent();
        saveMessages();
        return targetMsg;
    }

    /**
     * 메시지 데이터를 직렬화하여 파일에 저장하는 메서드
     *
     * @throws RuntimeException 파일 생성/직렬화 중 예외가 발생한 경우
     */
    public void saveMessages() {
        try {
            Files.createDirectories(FILE_PATH.getParent());
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_PATH.toFile()))) {
                oos.writeObject(data);
            }
        } catch (IOException e) {
            throw new RuntimeException("메시지 데이터 파일을 저장하는 중 오류가 발생하였습니다");
        }
    }
}
