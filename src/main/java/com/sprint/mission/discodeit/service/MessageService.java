package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Message;

import java.util.*;

public interface MessageService{
    void uploadMsg(UUID channelId, String uploaderName,String txtMsg);
    void updateMsg(String editerName, Message message, String Msg);
    Message findMessageByNum(UUID channelId,int num);

    void deleteMessage(String deleterName, Message message);
    void printAllMessages(UUID channelId);
    void printOneMessage(UUID channelId,int msgNum);
    List<Message> getMessagesList(UUID channelId); // 사용자 전체 조회
}
