package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.dto.channel.ChannelType;
import com.sprint.mission.discodeit.entity.dto.channel.GetPrivateChannelRequestDto;
import com.sprint.mission.discodeit.entity.dto.channel.GetPublicChannelRequestDto;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "file")
public class FileChannelRepository extends AbstractFileRepository<UUID, Channel> implements ChannelRepository {

    public FileChannelRepository(@Value("${discodeit.repository.file-directory}") String filePath) {
        super(filePath, "/channel.ser");
    }

    @Override
    public Channel createChannel(Channel channel) {
        return save(channel.getId(), channel);
    }

    @Override
    public Optional<Channel> getChannel(GetPublicChannelRequestDto dto) {
        Map<UUID, Channel> channels = loadFromFile();
        return Optional.ofNullable(channels.get(dto.getChannelId()));
    }

    @Override
    public Optional<Channel> getChannel(GetPrivateChannelRequestDto dto) {
        Map<UUID, Channel> channels = loadFromFile();
        return Optional.ofNullable(channels.get(dto.getChannelId()));
    }

    @Override
    public List<Channel> findAllByUserId(UUID userId) {
        return loadFromFile().values().stream()
                .filter(channel -> isUserInChannel(channel, userId))
                .collect(Collectors.toList());
    }

    @Override
    public boolean modifyChannel(Channel channel) {
        Map<UUID, Channel> channels = loadFromFile();
        if (!channels.containsKey(channel.getId())) {
            return false;
        }
        channels.put(channel.getId(), channel);
        saveToFile(channels);
        return true;
    }

    @Override
    public boolean deleteChannel(UUID id, UUID userId) {
        Map<UUID, Channel> channels = loadFromFile();
        Channel channel = channels.get(id);

        if (channel != null && isAdmin(channel, userId)) {
            cleanupChannelData(channel);
            channels.remove(id);
            saveToFile(channels);
            return true;
        }
        return false;
    }

    @Override
    public boolean kickOutChannel(UUID channelId, User kickUser, User admin) {
        Map<UUID, Channel> channels = loadFromFile();
        Channel channel = channels.get(channelId);

        if (channel != null && isAdmin(channel, admin.getId())) {
            channel.removeMember(kickUser);
            saveToFile(channels);
            return true;
        }
        return false;
    }

    @Override
    public boolean joinChannel(UUID channelId, User user) {
        Map<UUID, Channel> channels = loadFromFile();
        Channel channel = channels.get(channelId);

        if (channel == null) {
            return false;
        }

        if (channel.getMembers().contains(user)) {
            return false;
        }

        channel.addMember(user);
        saveToFile(channels);
        return true;
    }

    // 내부 헬퍼 메서드
    private boolean isAdmin(Channel channel, UUID userId) {
        return channel.getChannelAdmin().getId().equals(userId);
    }

    private boolean isUserInChannel(Channel channel, UUID userId) {
        return channel.getType() == ChannelType.PUBLIC ||
                channel.getMembers().stream()
                        .anyMatch(member -> member.getId().equals(userId));
    }

    private void cleanupChannelData(Channel channel) {
        channel.getMembers().forEach(member -> member.getChannels().remove(channel));
        channel.getMembers().clear();
        channel.getMessages().clear();
    }

}
