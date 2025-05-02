package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.dto.channel.*;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "jcf")
public class JCFChannelRepository implements ChannelRepository {

    private final Map<UUID, Channel> channels;

    public JCFChannelRepository() {
        channels = new HashMap<UUID, Channel>();
    }

    private boolean isAdmin(Channel channel, UUID userId) {
        boolean isAdmin = channel.getChannelAdmin().getId().equals(userId);
        if (!isAdmin) {
            System.out.println("권한이 없습니다.");
        }
        return isAdmin;
    }

    @Override
    public Channel createChannel(ChannelCreateDto channelCreateDto) {
        Channel channel = new Channel(
                channelCreateDto.getAdmin(),
                channelCreateDto.getName(),
                channelCreateDto.getDescription()
        );

        channel.addMember(channelCreateDto.getAdmin());

        channels.put(channel.getId(), channel);
        return channel;
    }

    @Override
    public Channel createChannel(ChannelCreatePrivateDto channelCreatePrivateDto) {
        Channel channel = new Channel(channelCreatePrivateDto);
        channels.put(channel.getId(), channel);
        return channel;
    }

    @Override
    public Optional<Channel> getChannel(GetPublicChannelRequestDto getPublicChannelRequestDto) {
        return channels.get(getPublicChannelRequestDto.getChannelId()) == null ? Optional.empty() : Optional.of(channels.get(getPublicChannelRequestDto.getChannelId()));
    }

    @Override
    public Optional<Channel> getChannel(GetPrivateChannelRequestDto getPrivateChannelRequestDto) {
        return channels.get(getPrivateChannelRequestDto.getChannelId()) == null ? Optional.empty() : Optional.of(channels.get(getPrivateChannelRequestDto.getChannelId()));
    }

    @Override
    public List<Channel> findAllByUserId(UUID userId) {
        List<Channel> result = new ArrayList<>();
        for (Channel channel : channels.values()) {
            if (channel.getMembers().stream().anyMatch(member -> member.getId().equals(userId))) {
                result.add(channel);
            }
        }
        return result;
    }

    @Override
    public boolean deleteChannel(UUID id, User user) {
        Channel channel = channels.get(id);
        if (channel == null) {
            throw new NoSuchElementException("존재하지 않는 채널입니다.");
        }
        if (channel.getChannelAdmin().getId().equals(user.getId())) {
            channel.getMembers().forEach(member -> {
                member.getChannels().remove(channel);
            });
            channels.remove(id);
            return true;
        }
        return false;
    }

    @Override
    public boolean modifyChannel(ChannelUpdateRequestDto channelUpdateRequestDto) {
        if (!channels.containsKey(channelUpdateRequestDto.getChannelId())) {
            throw new NoSuchElementException("존재하지 않는 채널입니다.");
        }

        Channel channel = channels.get(channelUpdateRequestDto.getChannelId());

        if (channel.getType().equals(ChannelType.PRIVATE)) {
            System.out.println("private 채널은 수정할 수없습니다");
            return false;
        }

        if (isAdmin(channel, channelUpdateRequestDto.getAdminId())) {
            if (channelUpdateRequestDto.getChannelDescription() != null && channelUpdateRequestDto.getChannelName() != null) {
                channel.setDescription(channelUpdateRequestDto.getChannelDescription());
                channel.setName(channelUpdateRequestDto.getChannelName());
                System.out.println("변경되었습니다.");
                return true;
            } else if (channelUpdateRequestDto.getChannelDescription() == null && channelUpdateRequestDto.getChannelName() != null) {
                channel.setName(channelUpdateRequestDto.getChannelName());
                System.out.println("변경되었습니다.");
                return true;
            } else {
                channel.setDescription(channelUpdateRequestDto.getChannelDescription());
                System.out.println("변경되었습니다.");
                return true;
            }
        }

        System.out.println("권한이 없습니다.");
        return false;
    }

    @Override
    public boolean kickOutChannel(UUID channelId, User kickUser, User admin) {
        if (channels.containsKey(channelId)) {
            Channel channel = channels.get(channelId);
            if (isAdmin(channel, admin.getId())) {
                channel.getMembers().remove(kickUser);
                System.out.println(kickUser.getUserName() + "회원이 강퇴 되었습니다.");
                return true;
            } else {
                return false;
            }
        } else {
            System.out.println("존재하지 않는 채널입니다.");
            return false;
        }
    }

    @Override
    public boolean joinChannel(Channel channel, User user) {
        if (channels.get(channel.getId()).getMembers().contains(user)) {
            System.out.println("이미 참여한 채널입니다.");
            return false;
        }else {
            channel.addMember(user);
            return true;
        }
    }

}
