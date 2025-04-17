package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.MessageService;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.UUID;

public class JCFMessageService implements MessageService {
    private final Map<UUID, Message> data = new HashMap<>();

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
        // 메시지 컬렉션에 추가
        data.put(msg.getId(), msg);
        return msg;
    }

    /**
     * 메모리에 저장되어있는 메시지 데이터를 리턴하는 메서드
     *
     * @return 메모리에 저장된 메시지데이터
     */
    @Override
    public Map<UUID, Message> getMessages() {
        return data;
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
        return targetMsg;
    }

    /**
     * 메시지 데이터를 저장하는 메서드
     * JCF*Service의 경우 메모리에 저장되어 있으므로 해당 메서드 구현하지 않음
     */
    @Override
    public void saveMessages() {
    }
}
