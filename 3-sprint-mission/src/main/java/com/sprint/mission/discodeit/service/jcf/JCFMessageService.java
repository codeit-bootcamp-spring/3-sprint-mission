package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Message;

import java.util.List;

public class JCFMessageService {

    private final List<Message> messageList;

    public JCFMessageService(List<Message> messageList) {
        this.messageList = messageList;
    }

}
