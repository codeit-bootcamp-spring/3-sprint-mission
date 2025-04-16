package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.service.ChannelService;

import java.util.*;

public class BasicChannelService implements ChannelService {

    private final ChannelRepository channelRepository;

    public BasicChannelService(ChannelRepository channelRepository) {
        this.channelRepository = channelRepository;
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
        Map<UUID, Channel> channels = channelRepository.loadFromFile();
        Channel channel = new Channel(user, channelName, description);

        channel.addMember(user);

        channels.put(channel.getId(), channel);
        channelRepository.saveToFile(channels);
        return channel;
    }

    @Override
    public Optional<Channel> getChannel(UUID id) {
        return Optional.ofNullable(channelRepository.loadFromFile().get(id));
    }

    @Override
    public List<Channel> getAllChannels() {
        return new ArrayList<>(channelRepository.loadFromFile().values());
    }

    @Override
    public boolean deleteChannel(UUID id, User user) {
        Map<UUID, Channel> channels = channelRepository.loadFromFile();

        if (channels.containsKey(id)) {
            Channel channel = channels.get(id);
            if (isAdmin(channel, user)) {
                Set<User> memberCopy = new HashSet<>(channel.getMembers());
                memberCopy.forEach(member -> {
                    member.getChannels().remove(channel);
                });

                channels.remove(id);
                channelRepository.saveToFile(channels);
                System.out.println("삭제 되었습니다.");
                return true;
            }else {
                return false;
            }
        }
        return false;
    }

    @Override
    public boolean modifyChannelName(UUID id, User user, String name) {
        Map<UUID, Channel> channels = channelRepository.loadFromFile();

        if (channels.containsKey(id)) {
            System.out.println(user.getId());
            if (isAdmin(channels.get(id), user)) {
                channels.get(id).setName(name);
                channelRepository.saveToFile(channels);
                System.out.println("변경되었습니다.");
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
    public boolean modifyChannelDescription(UUID id, User user, String description) {
        Map<UUID, Channel> channels = channelRepository.loadFromFile();

        if (channels.containsKey(id)) {
            if (isAdmin(channels.get(id), user)) {
                channels.get(id).setDescription(description);
                channelRepository.saveToFile(channels);
                System.out.println("변경되었습니다.");
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
    public boolean kickOutChannel(UUID channelId, User kickUser, User admin) {
        Map<UUID, Channel> channels = channelRepository.loadFromFile();

        if (channels.containsKey(channelId)) {
            Channel channel = channels.get(channelId);

            if (isAdmin(channel, admin)) {
                boolean removed = channel.getMembers().remove(kickUser);
                kickUser.getChannels().remove(channel); // 양방향 제거

                if (removed) {
                    channelRepository.saveToFile(channels);
                    System.out.println(kickUser.getUserName() + " 회원이 강퇴되었습니다.");
                    return true;
                } else {
                    System.out.println("강퇴 실패: 멤버 목록에 없습니다.");
                }
            }
        } else {
            System.out.println("존재하지 않는 채널입니다.");
        }
        return false;
    }

    @Override
    public boolean joinChannel(Channel channel, User user) {
        Map<UUID, Channel> channels = channelRepository.loadFromFile();
        Channel ch = channels.get(channel.getId());
        if (ch != null) {
            ch.addMember(user);
            channelRepository.saveToFile(channels);
            return true;
        }
        return false;
    }

    @Override
    public boolean addMessageToChannel(UUID channelId, Message message) {
        Map<UUID, Channel> channels = channelRepository.loadFromFile();

        if (channels.containsKey(channelId)) {
            Channel ch = channels.get(channelId);
            ch.getMessages().add(message);
            channelRepository.saveToFile(channels);
            return true;
        }
        return false;
    }
}
