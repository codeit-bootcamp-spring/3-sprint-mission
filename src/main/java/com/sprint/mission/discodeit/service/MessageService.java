package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Message;

import java.util.*;

public interface MessageService{
    void createMsg(UUID channelId, String uploaderName, String txtMsg);         // Create 새 메세지 생성

    Message findMessageByNum(UUID channelId,int num);                           // Read 채널 내 단일 메세지 조회
    List<Message> getMessagesList(UUID channelId);                              // Read 채널 내 전체 메세지 조회

    void updateMsg(UUID channelId, String editerName, Message message, String Msg);             // Update 메세지 수정

    void deleteMessage(UUID channelId, String deleterName, Message message);                    // Delete 단일 메세지 삭제

    void printAllMessages(UUID channelId);                                      // Read 채널 내 전체 메세지 포맷 출력
    void printOneMessage(UUID channelId,int msgNum);                            // Read 채널 내 단일 메세지 포맷 출력
}
