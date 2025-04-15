package com.sprint.mission.discodeit.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.MessageService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class JCFMessageService implements MessageService {
    private static final Map<UUID,List<Message>> channelMsgBoard = new ConcurrentHashMap<>();


    public static void setDefaultBoard(Channel channel){
        channelMsgBoard.computeIfAbsent(channel.getId(), id -> {
            List<Message> list = new ArrayList<>();
            list.add(new Message(1, "defaultUser", "defaultMessage", 1744036548250L, 1744036548250L));
            return list;
        });
    };


    @Override
    public void uploadMsg(UUID channelId,String uploaderName,String txtMsg){
        channelMsgBoard.putIfAbsent(channelId,new ArrayList<>());
        int msgNumber = channelMsgBoard.get(channelId).size() +1;
        long now = System.currentTimeMillis();
        Message message = new Message(msgNumber,uploaderName,txtMsg,now,now);
        channelMsgBoard.get(channelId).add(message);
    }

    @Override
    public void updateMsg(String editerName, Message message,String newMsg) {
        String resetColor = "\u001B[0m";
        String setColorYellow = "\u001B[33m";
        message.setUpdatedAt(System.currentTimeMillis());
        message.setTextMsg(newMsg + setColorYellow + "      *** [" + editerName + "] 에 의해 수정됨" + resetColor);
    }

    public void deleteMessage(String deleterName, Message message){
        String resetColor = "\u001B[0m";
        String setColorRed = "\u001B[31m";
        message.setUpdatedAt(System.currentTimeMillis());
        message.setTextMsg(setColorRed + " - ////// " + deleterName + " 사용자에 의해 삭제됨 ////// -" +message.getUpdatedAt() + resetColor);
    }
    public Message findMessageByNum(UUID channelId,int num){
        for (Message message : channelMsgBoard.get(channelId)) {
            if (message.getMsgNumber() == num) {
                return message; // 일치하는 메세지 번호 발견시 message 리턴
            }
        }return null;
    }



    public void printAllMessages(UUID channelId) {
        if(channelMsgBoard.get(channelId)!=null) {
            System.out.println("<< 입력된 전체 메세지 >>");
            System.out.println("번호 | 사용자       : 메세지");
            channelMsgBoard.get(channelId).forEach(m -> System.out.printf("%4d | %-10s: %s \n       생성일 : %s    수정일 : %s    UUID : %s\n", m.getMsgNumber(), m.getAuthor(), m.getTextMsg(), m.getCreatedAt(), m.getUpdatedAt(), m.getId()));
            System.out.println();
        } else {
            System.out.println("<<선택된 채널에 입력된 메세지 없음>>");
        }

    }
    public void printOneMessage(UUID channelId,int msgNum){
        Message message = findMessageByNum(channelId,msgNum);
        System.out.printf("%4d | %-10s: %s \n       생성일 : %s    수정일 : %s    UUID : %s\n", message.getMsgNumber(),message.getAuthor(),message.getTextMsg(),message.getCreatedAt(),message.getUpdatedAt(),message.getId() );
    }
    public List<Message> getMessagesList(UUID channelId) {
        return channelMsgBoard.get(channelId);
    }
}
