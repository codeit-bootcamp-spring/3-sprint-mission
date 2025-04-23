package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFMessageRepository;
import com.sprint.mission.discodeit.service.MessageService;

import java.util.List;
import java.util.UUID;

public class JCFMessageService implements MessageService {

    static MessageRepository messageRepository = new JCFMessageRepository();

    @Override
    public void createMsg(UUID channelId, String uploaderName, String txtMsg){
        List<Message> messages = messageRepository.getMessages(channelId);
        int msgCount = messages.size() + 1;
        long now = System.currentTimeMillis();
        Message message = new Message(msgCount,uploaderName,txtMsg,now,now);
        messages.add(message);
        messageRepository.saveMessages(channelId, messages);
    }

    @Override
    public void updateMsg(UUID channelId, String editerName, Message message,String newMsg) {
        String resetColor = "\u001B[0m";
        String setColorYellow = "\u001B[33m";
        message.setUpdatedAt(System.currentTimeMillis());
        message.setTextMsg(newMsg + setColorYellow + "      *** [" + editerName + "] 에 의해 수정됨" + resetColor);
    }

    @Override
    public void deleteMessage(UUID channelId, String deleterName, Message message) {
        String resetColor = "\u001B[0m";
        String setColorRed = "\u001B[31m";
        message.setUpdatedAt(System.currentTimeMillis());
        message.setTextMsg(setColorRed + " - ////// " + deleterName + " 사용자에 의해 삭제됨 ////// -" +message.getUpdatedAt() + resetColor);
    }

    @Override
    public Message findMessageByNum(UUID channelId,int num){
        List<Message> messages = messageRepository.getMessages(channelId);
        for (Message message : messages) {
            if (message.getMsgNumber() == num) {
                return message; // 일치하는 메세지 번호 발견시 message 리턴
            }
        }return null;
    }

    @Override
    public void printAllMessages(UUID channelId) {
        List<Message> messages = messageRepository.getMessages(channelId);
        if(messages!=null) {
            System.out.println("<< 입력된 전체 메세지 >>");
            System.out.println("번호 | 사용자       : 메세지");
            messages.forEach(m -> System.out.printf("%4d | %-10s: %s \n       생성일 : %s    수정일 : %s    UUID : %s\n", m.getMsgNumber(), m.getAuthor(), m.getTextMsg(), m.getCreatedAt(), m.getUpdatedAt(), m.getId()));
            System.out.println();
        } else {
            System.out.println("<<선택된 채널에 입력된 메세지 없음>>");
        }

    }

    @Override
    public void printOneMessage(UUID channelId,int msgNum){
        Message message = findMessageByNum(channelId,msgNum);
        System.out.printf("%4d | %-10s: %s \n       생성일 : %s    수정일 : %s    UUID : %s\n", message.getMsgNumber(),message.getAuthor(),message.getTextMsg(),message.getCreatedAt(),message.getUpdatedAt(),message.getId() );
    }

    @Override
    public List<Message> getMessagesList(UUID channelId) {
        return messageRepository.getMessages(channelId);
    }
}
