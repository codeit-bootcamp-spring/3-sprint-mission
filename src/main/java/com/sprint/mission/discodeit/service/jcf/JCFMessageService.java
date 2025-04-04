package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;

import java.util.*;
import java.util.stream.Collectors;

/**
 * packageName    : com.sprint.mission.discodeit.service.jcf
 * fileName       : JCFMessageService
 * author         : doungukkim
 * date           : 2025. 4. 3.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2025. 4. 3.        doungukkim       최초 생성
 */
public class JCFMessageService implements MessageService {
    private final ChannelService channelService;
    // (channelId, message)
    private final List<Message> data;

    public JCFMessageService(ChannelService channelService) {
        this.data = new ArrayList<>();
        this.channelService = channelService;
    }

    @Override
    public UUID createMessage(UUID senderId, UUID channelId, String message) {
        Message newMessage = new Message(senderId, channelId, message);
        data.add(newMessage);

        // Channel에 message 추가
        // Channel 찾기
        if (channelService.findChannelsById(channelId).isEmpty()) {

        }
        // List에 추가

        return newMessage.getId();
    }

    @Override
    public List<Message> findAllMessages() {
        return data.stream().collect(Collectors.toList());
    }

    @Override
    public List<Message> findMessageById(UUID id) {
        return data.stream()
                .filter(message -> message.getId().equals(id))
                .collect(Collectors.toList());
    }

    @Override
    public void updateMessage(UUID id, String newMessage) {
        for (int i = 0; i < data.size(); i++) {
            if (data.get(i).getId().equals(id)) {
                data.get(i).setMessage(newMessage);
                data.get(i).setUpdatedAt(System.currentTimeMillis());
            }
        }
    }

    @Override
    public void deleteMessageById(UUID id) {
        for (int i = 0; i < data.size(); i++) {
            if (data.get(i).getId().equals(id)) {
                data.remove(i);
            }
        }
    }
}
