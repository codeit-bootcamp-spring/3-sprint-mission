package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class FileMessageService implements MessageService {

    private final UserService userService;
    private final ChannelService channelService;

    public FileMessageService(UserService userService, ChannelService channelService) {
        this.userService = userService;
        this.channelService = channelService;
    }

    @Override
    public List<Message> getChannelMessages(Channel channel) {
        return channelService.getChannel(channel.getId()).getMessages();
//        if (channelService instanceof FileChannelService) {
//            Map<UUID, Channel> channels = ((FileChannelService) channelService).loadFromFile();
//            return channels.get(channel.getId()).getMessages();
//        } else {
//            return List.of();
//        }
    }

    @Override
    public boolean deleteMessage(Channel channel, Message message, User user) {
        boolean result = false;

        if (channelService instanceof FileChannelService) {
            Map<UUID, Channel> channels = ((FileChannelService) channelService).loadFromFile();

            if (channels.containsKey(channel.getId())) {
                Channel ch = channels.get(channel.getId());
                for (Message m : ch.getMessages()) {
                    if (m.getId().equals(message.getId())) {
                        ch.getMessages().remove(m);
                        ((FileChannelService) channelService).saveToFile(channels);
                        result = true;
                        System.out.println("(" + m.getContent() + ") 메세지가 삭제되었습니다.");
                        break;
                    } else {
                        System.out.println("본인이 작성한 메세지만 삭제할 수 있습니다.");
                    }
                }
            }
        }

        return result;
    }

}
