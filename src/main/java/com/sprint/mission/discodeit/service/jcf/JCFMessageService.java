package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.List;
import java.util.NoSuchElementException;

public class JCFMessageService implements MessageService {

    private final UserService userService;
    private final ChannelService channelService;


    public JCFMessageService(UserService userService, ChannelService channelService) {
        this.userService = userService;
        this.channelService = channelService;
    }

    @Override
    public List<Message> getChannelMessages(Channel channel) {
        Channel ch = channelService.getChannel(channel.getId())
                .orElseThrow(() -> new NoSuchElementException("해당 채널을 찾을 수 없습니다: " + channel.getId()));
        return ch.getMessages();
    }

    @Override
    public boolean deleteMessage(Channel channel, Message message, User user) {
        Channel foundChannel = channelService.getChannel(channel.getId())
                .orElseThrow(() -> new NoSuchElementException("채널을 찾을 수 없습니다."));

        List<Message> messages = foundChannel.getMessages();

        User findUser = userService.getUser(user.getId())
                .orElseThrow(() -> new NoSuchElementException("유저를 찾을 수 없습니다."));

        boolean result = messages.removeIf(m ->
                m.getId().equals(message.getId()) && m.getSender().getId().equals(findUser.getId())
        );

        if (result) {
            System.out.println("(" + message.getContent() + ") 메세지가 삭제되었습니다.");
        } else {
            System.out.println("본인이 작성한 메세지만 삭제할 수 있습니다.");
        }

        return result;
    }

}
