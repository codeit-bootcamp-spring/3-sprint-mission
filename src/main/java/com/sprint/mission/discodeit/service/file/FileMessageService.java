package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.file.FileMessageRepository;
import com.sprint.mission.discodeit.service.MessageService;

import java.util.List;
import java.util.UUID;

public class FileMessageService implements MessageService {
    MessageRepository messageRepository = new FileMessageRepository();

    @Override
    public void createMsg(UUID channelId, String uploaderName, String txtMsg){
        List<Message> messages = messageRepository.getMessages(channelId);
        long now = System.currentTimeMillis();
        int msgNumber = messages.size() + 1;
        Message newMsg = new Message(msgNumber, uploaderName, txtMsg, now, now);
        messages.add(newMsg);
        messageRepository.saveMessages(channelId, messages);
    }

    @Override
    public List<Message> getMessagesList(UUID channelId){
        return messageRepository.getMessages(channelId);
    }

    public void updateMsg(UUID channelId, String editerName, Message message,String newMsg) {
        String resetColor = "\u001B[0m";
        String setColorYellow = "\u001B[33m";
        message.setUpdatedAt(System.currentTimeMillis());
        message.setTextMsg(newMsg + setColorYellow + "      *** [" + editerName + "] 에 의해 수정됨" + resetColor);
        messageRepository.saveMessages(channelId, messageRepository.getMessages(channelId));
    }

    @Override
    public void deleteMessage(UUID channelId, String deleterName, Message message) {
        String resetColor = "\u001B[0m";
        String setColorRed = "\u001B[31m";
        message.setUpdatedAt(System.currentTimeMillis());
        message.setTextMsg(setColorRed + " - ////// " + deleterName + " 사용자에 의해 삭제됨 ////// - " + message.getUpdatedAt() + resetColor);
        messageRepository.saveMessages(channelId, messageRepository.getMessages(channelId));
    }

    @Override
    public Message findMessageByNum(UUID channelId, int msgNum){
        List<Message> messages = messageRepository.getMessages(channelId);
        for (Message msg : messages) {
            if (msg.getMsgNumber() == msgNum) return msg;
        }
        return null;
    }



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
    public void printOneMessage(UUID channelId,int msgNum){
        Message message = findMessageByNum(channelId,msgNum);
        System.out.printf("%4d | %-10s: %s \n       생성일 : %s    수정일 : %s    UUID : %s\n", message.getMsgNumber(),message.getAuthor(),message.getTextMsg(),message.getCreatedAt(),message.getUpdatedAt(),message.getId() );
    }
}
