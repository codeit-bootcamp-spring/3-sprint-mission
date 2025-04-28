package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.dto.channel.*;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.util.*;

@Repository
@Primary
public class FileChannelRepository implements ChannelRepository {
    private final String FILE_PATH = "channels.ser";

    public Map<UUID, Channel> loadFromFile() {
        File file = new File(FILE_PATH);
        if (!file.exists()) return new HashMap<>();

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (Map<UUID, Channel>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return new HashMap<>();
        }
    }

    public void saveToFile(Map<UUID, Channel> channels) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_PATH))) {
            oos.writeObject(channels);
        } catch (IOException e) {
            e.printStackTrace();
        }
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
        Map<UUID, Channel> channels = loadFromFile();
        Channel channel = new Channel(
                channelCreateDto.getAdmin(),
                channelCreateDto.getName(),
                channelCreateDto.getDescription()
        );
        channel.addMember(channelCreateDto.getAdmin());
        channels.put(channel.getId(), channel);
        saveToFile(channels);
        return channel;
    }

    @Override
    public Channel createChannel(ChannelCreatePrivateDto channelCreatePrivateDto) {
        Map<UUID, Channel> channels = loadFromFile();
        Channel channel = new Channel(channelCreatePrivateDto);
        channel.addMember(channelCreatePrivateDto.getAdmin());
        channels.put(channel.getId(), channel);
        saveToFile(channels);
        return channel;
    }

    @Override
    public Optional<Channel> getChannel(GetPublicChannelRequestDto getPublicChannelRequestDto) {
        Map<UUID, Channel> channels = loadFromFile();
        return channels.get(getPublicChannelRequestDto.getChannelId()) == null ? Optional.empty() : Optional.of(channels.get(getPublicChannelRequestDto.getChannelId()));
    }

    @Override
    public Optional<Channel> getChannel(GetPrivateChannelRequestDto getPrivateChannelRequestDto) {
        Map<UUID, Channel> channels = loadFromFile();
        return channels.get(getPrivateChannelRequestDto.getChannelId()) == null ? Optional.empty() : Optional.of(channels.get(getPrivateChannelRequestDto.getChannelId()));
    }

    @Override
    public List<Channel> findAllByUserId(UUID userId) {
        Map<UUID, Channel> channels = loadFromFile();
        List<Channel> result = new ArrayList<>();

        for (Channel channel : channels.values()) {
            if (channel.getType() == ChannelType.PUBLIC ||
                    (channel.getType() == ChannelType.PRIVATE &&
                            channel.getMembers().stream().anyMatch(member -> member.getId().equals(userId)))) {
                result.add(channel);
            }
        }

        return result;

    }

    @Override
    public boolean deleteChannel(UUID id, User user) {
        Map<UUID, Channel> channels = loadFromFile();
        if (!channels.containsKey(id)) {
            return false;
        }
        Channel channel = channels.get(id);
        if (channel.getChannelAdmin().getId().equals(user.getId())) {
            channel.getMembers().forEach(member -> {
                member.getChannels().remove(channel);
            });
            channel.getMembers().clear(); // 채널의 멤버 삭제
            channel.getMessages().clear(); // 채널의 메세지 삭제
            channels.remove(id);
            saveToFile(channels);
            return true;
        }
        return false;
    }

    @Override
    public boolean modifyChannel(ChannelUpdateRequestDto channelUpdateRequestDto) {
        Map<UUID, Channel> channels = loadFromFile();
        if (!channels.containsKey(channelUpdateRequestDto.getChannelId())) {
            return false;
        }

        Channel channel = channels.get(channelUpdateRequestDto.getChannelId());
        if (channel.getType().equals(ChannelType.PRIVATE)) {
            System.out.println("private 채널은 수정할 수 없습니다.");
            return false;
        }

        if (isAdmin(channel, channelUpdateRequestDto.getAdminId())) {
            if (channelUpdateRequestDto.getChannelDescription() != null && channelUpdateRequestDto.getChannelName() != null) {
                channel.setDescription(channelUpdateRequestDto.getChannelDescription());
                channel.setName(channelUpdateRequestDto.getChannelName());
                System.out.println("변경되었습니다.");
                saveToFile(channels);
                return true;
            } else if (channelUpdateRequestDto.getChannelDescription() == null && channelUpdateRequestDto.getChannelName() != null) {
                channel.setName(channelUpdateRequestDto.getChannelName());
                System.out.println("변경되었습니다.");
                saveToFile(channels);
                return true;
            } else {
                channel.setDescription(channelUpdateRequestDto.getChannelDescription());
                System.out.println("변경되었습니다.");
                saveToFile(channels);
                return true;
            }
        }

        System.out.println("권한이 없습니다.");
        return false;
    }

    @Override
    public boolean kickOutChannel(UUID channelId, User kickUser, User admin) {
        Map<UUID, Channel> channels = loadFromFile();
        if (channels.containsKey(channelId)) {
            Channel channel = channels.get(channelId);
            if (isAdmin(channel, admin.getId())) {
                channel.getMembers().remove(kickUser);
                System.out.println(kickUser.getUserName() + "회원이 강퇴 되었습니다.");
                saveToFile(channels);
                return true;
            } else {
                return false;
            }
        }else {
            System.out.println("존재하지 않는 채널입니다.");
            return false;
        }
    }

    @Override
    public boolean joinChannel(Channel channel, User user) {
        Map<UUID, Channel> channels = loadFromFile();
        System.out.println(channels.get(channel.getId()));
        Channel existingChannel = channels.get(channel.getId());

        if (existingChannel == null) {
            System.out.println("존재하지 않는 채널입니다.");
            return false;
        }

        if (existingChannel.getMembers().contains(user)) {
            System.out.println("이미 참여한 채널입니다.");
            return false;
        } else {
            existingChannel.addMember(user);
            saveToFile(channels);
            return true;
        }

    }
}
