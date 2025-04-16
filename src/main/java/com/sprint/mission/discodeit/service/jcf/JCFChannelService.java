package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;

import java.util.*;

public class JCFChannelService implements ChannelService {

    private final Map<UUID, Channel> channels;

    public JCFChannelService() {
        channels = new HashMap<>();
    }

    private Optional<Channel> findChannelById(UUID id) {
        Channel channel = channels.get(id);
        if (channel == null) {
            System.out.println("존재하지 않는 채널입니다.");
            return Optional.empty();
        }
        return Optional.of(channel);
    }

    private boolean isAdmin(Channel channel, User user) {
        boolean isAdmin = channel.getChannelAdmin().getId().equals(user.getId());
        if (!isAdmin) {
            System.out.println("권한이 없습니다.");
        }
        return isAdmin;
    }

    @Override
    public Channel createChannel(String channelName, String description, User user) {
        Channel channel = new Channel(user, channelName, description);
        channel.addMember(user);
        channels.put(channel.getId(), channel);
        return channel;
    }

    @Override
    public Optional<Channel> getChannel(UUID id) {
        return Optional.ofNullable(channels.get(id));
    }

    @Override
    public List<Channel> getAllChannels() {
        return new ArrayList<>(channels.values());
    }

    @Override
    public boolean deleteChannel(UUID id, User user) {
        Optional<Channel> optionalChannel = findChannelById(id);

        if (optionalChannel.isPresent()) {
            Channel channel = optionalChannel.get();
            if (isAdmin(channel, user)) {
                channels.get(id).getMembers().clear();
                channels.get(id).getMessages().clear();
                channel.getChannelAdmin().getChannels().forEach(ch -> {
                    if (ch.getId().equals(channel.getId())) {
                        user.getChannels().remove(ch);
                    }
                });
                channels.remove(id);
                System.out.println("삭제되었습니다.");
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
//        if (channels.containsKey(id)) {
//            if (channels.get(id).getChannelAdmin().equals(user)) {
//                channels.get(id).getMembers().clear();
//                channels.get(id).getMessages().clear();
//                channels.remove(id);
//                System.out.println("삭제되었습니다.");
//                return true;
//            }else {
//                System.out.println("권한이 없습니다.");
//                return false;
//            }
//        }else {
//            System.out.println("존재하지 않는 채널입니다.");
//            return false;
//        }
    }

    @Override
    public boolean modifyChannelName(UUID id, User user, String name) {
        if (channels.containsKey(id)) {
            if (isAdmin(channels.get(id), user)) {
                channels.get(id).setName(name);
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
            if (isAdmin(channels.get(id), user)) {
                channels.get(id).setDescription(description);
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
            if (isAdmin(channel, admin)) {
                channel.getMembers().remove(kickUser);
                System.out.println(kickUser.getUserName() + "회원이 강퇴 되었습니다.");
                return true;
            }else {
                return false;
            }
        }else {
            System.out.println("존재하지 않는 채널입니다.");
            return false;
        }
    }

    @Override
    public boolean joinChannel(Channel channel, User user) {
        if (channel.getMembers().contains(user)) {
            System.out.println("이미 참여한 채널입니다.");
            return false;
        }else {
            channel.addMember(user);
            return true;
        }
    }

    @Override
    public boolean addMessageToChannel(UUID channelId, Message message) {
        Optional<Channel> optionalChannel = getChannel(channelId);
        if (optionalChannel.isPresent()) {
            optionalChannel.get().getMessages().add(message);
            return true;
        } else {
            System.out.println("채널이 존재하지 않습니다.");
            return false;
        }
    }
}
