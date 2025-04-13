package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;

import java.util.*;
import java.util.stream.Collectors;

public class JCFChannelService implements ChannelService {

    private final Map<UUID, Channel> channelList;

    public JCFChannelService() {
        channelList = new LinkedHashMap<>();
    }

    @Override
    public Channel create(String channelName, String channelDescription,
                          boolean isLock, User creator, String password) {
        Channel channel = new Channel(channelName, channelDescription, isLock, creator, password);
        this.channelList.put(channel.getId(), channel);
        return channel;
    }

    @Override
    public Channel readById(UUID channelId) {
        return channelList.get(channelId);
    }

    @Override
    public List<Channel> readAll() {
        return channelList.values().stream().collect(Collectors.toList());
    }

    @Override
    public void update(Channel channel, String ModifiedChannelName, String channelDescription,
                       boolean isLock, String password) {
        Channel c = channelList.get(channel.getId());
        c.update(ModifiedChannelName, channelDescription, isLock, password);
    }

    @Override
    public void deleteById(User user, Channel channel) {
        if (channel.getCreator().equals(user)) {
            channelList.remove(channel.getId());
            System.out.println("Channel has been deleted");
        } else {
            System.out.println("You are not allowed to delete this channel");
        }
    }

    @Override
    public void joinChannel(User user, Channel channel, String password) {
        Channel c = channelList.get(channel.getId());
        if (c == null) {
            System.out.println("Channel not found");
            return;
        }
        if (c.getMemberList().contains(user)) {
            System.out.println("You are already member of this channel");
            return;
        }
        if (!c.getPassword().equals(password) && c.isLock()) {
            System.out.println("Password is incorrect!!!");
            return;
        }
        channel.getMemberList().add(user);
        System.out.println(user.getUserName() + " Joined channel !!! [" + channel.getChannelName() + "]");
        System.out.println();
    }

    @Override
    public void leave(User user, Channel channel) {
        if (channel.getCreator().getId().equals(user.getId())) {
            System.out.println("You are creator of this channel.\n Are you sure you want to leave? \n Y/N");
            Scanner scanner = new Scanner(System.in);
            String answer = scanner.next();
            if ("y".equalsIgnoreCase(answer)) {
                System.out.println(user.getUserName() + "님이 " + channel.getChannelName() + "채널을 떠났습니다");
                channel.getMemberList().remove(user);
            } else {
                System.out.println(user.getUserName() + "님은 아직 " + channel.getChannelName() + "채널에 머물러있습니다");
            }
        } else {
            System.out.println(user.getUserName() + "님이 " + channel.getChannelName() + "채널을 떠났습니다");
            channel.getMemberList().remove(user);
        }
    }

    @Override
    public void addMessage(Channel channel, Message message) {
        channel.getMessageList().add(message);
    }

    @Override
    public void deleteMessage(Channel channel, Message message) {
        channel.getMessageList().remove(message);
    }

}




