package com.sprint.mission.discodeit.service;


import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;

import java.util.*;

public interface MessageService{
    boolean uploadMsg(User user);
    void updateMsg(Message message);

//    Optional<User> findMsgByNum(UUID id);
//    Optional<User> findUserByUser(String name);
//    boolean deleteUserById(UUID id);
//    boolean deleteUserByName(String name);
//
    void printAllMessages();
    List<Message> getMessagesList(); // 사용자 전체 조회
}
