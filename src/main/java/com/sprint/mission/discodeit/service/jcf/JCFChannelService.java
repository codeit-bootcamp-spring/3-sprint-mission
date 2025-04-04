package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;

import java.util.*;

public class JCFChannelService implements ChannelService {

    private final Map<UUID, Channel> channels;

    public JCFChannelService() {
        channels = new HashMap<>();
    }

    @Override
    public Channel createChannel(String name, String description, User user) {
        Channel channel = new Channel(user, name, description);
        channels.put(channel.getId(), channel);
        channel.getMembers().add(user);
        return channel;
    }

    @Override
    public Channel getChannel(UUID id) {
        if (channels.containsKey(id)) {
            return channels.get(id);
        }else {
            System.out.println("존재하지 않는 채널입니다.");
            return null;
        }
    }

    @Override
    public List<Channel> getAllChannels() {
        List<Channel> channelList = new ArrayList<>(channels.values());

        for (Channel channel : channelList) {
            System.out.println(channel);
        }
        return channelList;
    }

    @Override
    public boolean deleteChannel(UUID id, User user) {
        if (channels.containsKey(id)) {
            if (channels.get(id).getChannelAdmin().equals(user)) {
                channels.remove(id);
                System.out.println("삭제되었습니다.");
                return true;
            }else {
                System.out.println("권한이 없습니다.");
                return false;
            }
        }else {
            System.out.println("존재하지 않는 채널입니다.");
            return false;
        }
    }

    @Override
    public boolean modifyChannelName(UUID id, User user, String name) {
        if (channels.containsKey(id)) {
            if (channels.get(id).getChannelAdmin().equals(user)) {
                channels.get(id).setName(name);
                channels.get(id).setUpdatedAt(System.currentTimeMillis());
                System.out.println("변경되었습니다.");
                return true;
            }else {
                System.out.println("권한이 없습니다.");
                return false;
            }
        }else {
            System.out.println("존재하지 않는 채널입니다.");
            return false;
        }
    }

    @Override
    public boolean modifyChannelDescription(UUID id, User user, String description) {
        if (channels.containsKey(id)) {
            if (channels.get(id).getChannelAdmin().equals(user)) {
                channels.get(id).setDescription(description);
                channels.get(id).setUpdatedAt(System.currentTimeMillis());
                System.out.println("변경되었습니다.");
                return true;
            }else {
                System.out.println("권한이 없습니다.");
                return false;
            }
        }else {
            System.out.println("존재하지 않는 채널입니다.");
            return false;
        }
    }

    @Override
    public boolean kickOutChannel(UUID channelId, User kickUser, User admin) {
        if (channels.containsKey(channelId)) {
            Channel channel = channels.get(channelId);
            if (channel.getChannelAdmin().getId().equals(admin.getId())) {
                channel.getMembers().remove(kickUser);
                System.out.println(kickUser.getUserName() + "회원이 강퇴 되었습니다.");
                return true;
            }else {
                System.out.println("권한이 없습니다.");
                return false;
            }
        }else {
            System.out.println("존재하지 않는 채널입니다.");
            return false;
        }
    }
}
