package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
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


        // Channel 찾기
        List<Message> messages = channelService.findChannelsById(channelId).get(0).getMessages();

        // List에 메세지 추가 추가
        messages.add(newMessage);
        // 저장
        channelService.findChannelsById(channelId).get(0).setMessages(messages);

        return newMessage.getId();
    }

    @Override
    public List<Message> findAllMessages() {
        return data.stream().collect(Collectors.toList());
    }

    @Override
    public List<Message> findMessageById(UUID messageId) {
        try {
            List<Message> targetMessage = data.stream()
                    .filter(message -> message.getId().equals(messageId))
                    .collect(Collectors.toList());
            return targetMessage;
        } catch (NullPointerException e) {
            return new ArrayList<>();
        }
    }

    @Override
    public void updateMessage(UUID messageId, String newMessage) {
        for (int i = 0; i < data.size(); i++) {
            if (data.get(i).getId().equals(messageId)) {
                data.get(i).setMessage(newMessage);
                data.get(i).setUpdatedAt(System.currentTimeMillis());
            }
        }
    }

    @Override
    public void deleteMessageById(UUID messageid) {
        List<Channel> channelsById;
        for (int i = 0; i < data.size(); i++) {
            if (data.get(i).getId().equals(messageid)) {
//                System.out.println("메세지의 id와 같은 메세지 발견 : "+data.get(i).getMessage());
                channelsById = channelService.findChannelsById(data.get(i).getChannelId()); // List<Channel>
                for (int j = 0; j < channelsById.size(); j++) {
                    if (channelsById.get(j).getMessages().contains(findMessageById(messageid).get(0))) {
//                        System.out.println("체널에서 메세지 삭제");
                        channelsById.get(j).getMessages().remove(findMessageById(messageid).get(0));
                        break;
                    }
                }
                data.remove(i);
                break;
            }
        }
    }


}
