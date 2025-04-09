package com.sprint.mission.discodeit.service;


import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;

import java.util.*;

public interface MessageService{
    void uploadMsg(User user,String txtMsg);
    void updateMsg(Message message,String Msg);
    Message findMessageByNum(int num);

    void deleteMessage(Message message);
//
    void printAllMessages();
    List<Message> getMessagesList(); // 사용자 전체 조회
}
