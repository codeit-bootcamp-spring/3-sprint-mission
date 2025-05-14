package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.dto.channel.*;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;

import java.util.*;
import java.util.stream.Collectors;

public class JCFChannelService implements ChannelService {

    private final UserRepository userRepository;
    private final ReadStatusRepository readStatusRepository;
    private final ChannelRepository channelRepository;
    private final MessageRepository messageRepository;
    private final Map<UUID, Channel> channels;

    public JCFChannelService(ChannelRepository channelRepository, UserRepository userRepository, ReadStatusRepository readStatusRepository, MessageRepository messageRepository) {
        this.channelRepository = channelRepository;
        this.userRepository = userRepository;
        this.readStatusRepository = readStatusRepository;
        this.messageRepository = messageRepository;
        this.channels = new HashMap<>();
    }

    @Override
    public ChannelResponseDto createChannel(ChannelCreateDto dto) {
        User admin = getUserOrThrow(dto.getAdminId());
        Channel channel = new Channel(admin, dto.getName(), dto.getDescription());
        return createChannelResponseDto(channelRepository.createChannel(channel));
    }

    @Override
    public ChannelResponseDto createChannel(ChannelCreatePrivateDto dto) {
        User admin = getUserOrThrow(dto.getAdminId());
        Channel channel = new Channel(dto, admin);
        Channel savedChannel = channelRepository.createChannel(channel);
        createReadStatuses(savedChannel, dto.getUsers(), admin);
        return createChannelResponseDto(savedChannel);
    }

    @Override
    public Optional<ChannelResponseDto> getChannel(GetPublicChannelRequestDto dto) {
        return channelRepository.getChannel(dto)
                .map(this::createChannelResponseDto);
    }

    @Override
    public Optional<ChannelResponseDto> getChannel(GetPrivateChannelRequestDto dto) {
        return channelRepository.getChannel(dto)
                .map(channel -> createPrivateChannelResponseDto(channel));
    }

    @Override
    public List<ChannelResponseDto> findAllByUserId(UUID userId) {
        return channelRepository.findAllByUserId(userId).stream()
                .map(channel -> channel.getType() == ChannelType.PUBLIC ?
                        createChannelResponseDto(channel) :
                        createPrivateChannelResponseDto(channel))
                .collect(Collectors.toList());
    }

    @Override
    public boolean deleteChannel(UUID channelId, UUID userId) {
        Optional<Channel> channelOpt = channelRepository.getChannel(new GetPublicChannelRequestDto(channelId));
        if (channelOpt.isEmpty() || !isAdmin(channelOpt.get(), userId)) {
            return false;
        }

        Channel channel = channelOpt.get();
        deleteChannelResources(channel);
        return channelRepository.deleteChannel(channelId, userId);
    }

    @Override
    public boolean modifyChannel(ChannelUpdateRequestDto dto) {
        return channelRepository.getChannel(new GetPublicChannelRequestDto(dto.getChannelId()))
                .filter(channel -> isAdmin(channel, dto.getAdminId()))
                .map(channel -> {
                    updateChannelFields(channel, dto);
                    return channelRepository.modifyChannel(channel);
                })
                .orElse(false);
    }

    @Override
    public boolean kickOutChannel(UUID channelId, User kickUser, User admin) {
        return channelRepository.kickOutChannel(channelId, kickUser, admin);
    }

    @Override
    public boolean joinChannel(UUID channelId, UUID userId) {
        User user = getUserOrThrow(userId);
        return channelRepository.joinChannel(channelId, user);
    }

    // 내부 헬퍼 메서드들
    private User getUserOrThrow(UUID userId) {
        return userRepository.getUser(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));
    }

    private void createReadStatuses(Channel channel, Set<UUID> memberIds, User admin) {
        if (memberIds != null) {
            memberIds.forEach(userId -> createReadStatus(userId, channel.getId()));
        }
        createReadStatus(admin.getId(), channel.getId());
    }

    private void createReadStatus(UUID userId, UUID channelId) {
        ReadStatus readStatus = new ReadStatus(userId, channelId);
        readStatusRepository.createReadStatus(readStatus);
    }

    private boolean isAdmin(Channel channel, UUID userId) {
        return channel.getChannelAdmin().getId().equals(userId);
    }

    private void updateChannelFields(Channel channel, ChannelUpdateRequestDto dto) {
        Optional.ofNullable(dto.getChannelName())
                .ifPresent(channel::setName);
        Optional.ofNullable(dto.getChannelDescription())
                .ifPresent(channel::setDescription);
    }

    private void deleteChannelResources(Channel channel) {
        deleteChannelMessages(channel);
        deleteChannelReadStatuses(channel);
    }

    private void deleteChannelMessages(Channel channel) {
        channel.getMessages().forEach(message ->
                messageRepository.deleteMessage(message, channel.getId())
        );
    }

    private void deleteChannelReadStatuses(Channel channel) {
        channel.getMembers().stream()
                .map(User::getId)
                .forEach(memberId ->
                        readStatusRepository.findAllByUserId(memberId).stream()
                                .filter(rs -> rs.getChannelId().equals(channel.getId()))
                                .forEach(rs -> readStatusRepository.deleteReadStatus(rs.getId()))
                );
    }

    private ChannelResponseDto createChannelResponseDto(Channel channel) {
        ChannelResponseDto dto = new ChannelResponseDto();
        setBasicChannelInfo(dto, channel);
        setLastMessageTime(dto, channel);
        return dto;
    }

    private ChannelResponseDto createPrivateChannelResponseDto(Channel channel) {
        ChannelResponseDto dto = createChannelResponseDto(channel);
        dto.setMemberIds(channel.getMembers().stream()
                .map(User::getId)
                .collect(Collectors.toSet()));
        return dto;
    }

    private void setBasicChannelInfo(ChannelResponseDto dto, Channel channel) {
        dto.setChannelId(channel.getId());
        dto.setName(channel.getName());
        dto.setDescription(channel.getDescription());
        dto.setType(channel.getType());
    }

    private void setLastMessageTime(ChannelResponseDto dto, Channel channel) {
        List<Message> messages = messageRepository.getChannelMessages(channel.getId());
        if (!messages.isEmpty()) {
            dto.setLastMessageAt(messages.get(messages.size() - 1).getCreatedAt());
        }
    }

}
