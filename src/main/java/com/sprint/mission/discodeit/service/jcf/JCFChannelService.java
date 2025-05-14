package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.dto.channel.*;
import com.sprint.mission.discodeit.entity.dto.message.MessageDeleteRequestDto;
import com.sprint.mission.discodeit.entity.dto.readStatus.ReadStatusCreateRequestDto;
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

    private Optional<Channel> findChannelById(UUID id) {
        Channel channel = channels.get(id);
        if (channel == null) {
            System.out.println("존재하지 않는 채널입니다.");
            return Optional.empty();
        }
        return Optional.of(channel);
    }



    @Override
    public Channel createChannel(ChannelCreateDto channelCreateDto) {
        return channelRepository.createChannel(channelCreateDto);
    }

    @Override
    public Channel createChannel(ChannelCreatePrivateDto channelCreatePrivateDto) {
        Map<UUID, Set<ReadStatus>> channelReadStatus = new HashMap<>();
        Set<ReadStatus> readStatusList = new HashSet<>();

        Channel channel = channelRepository.createChannel(channelCreatePrivateDto);

        channelCreatePrivateDto.getUsers().forEach(user -> {
            ReadStatusCreateRequestDto readStatusDto = new ReadStatusCreateRequestDto(
                    user.getId(),
                    channel.getId()
            );

            // ReadStatus 생성 및 저장
            readStatusRepository.createReadStatus(readStatusDto);
        });

        ReadStatusCreateRequestDto adminReadStatusDto = new ReadStatusCreateRequestDto(
                channelCreatePrivateDto.getAdmin().getId(),
                channel.getId()
        );
        readStatusRepository.createReadStatus(adminReadStatusDto);

        return channel;
    }

    @Override
    public Optional<ChannelResponseDto> getChannel(GetPublicChannelRequestDto getPublicChannelRequestDto) {
        return channelRepository.getChannel(getPublicChannelRequestDto)
                .map(this::createChannelResponseDto);
    }

    @Override
    public Optional<ChannelResponseDto> getChannel(GetPrivateChannelRequestDto getPrivateChannelRequestDto) {
        return channelRepository.getChannel(getPrivateChannelRequestDto)
                .map(channel -> {
                    ChannelResponseDto responseDto = createChannelResponseDto(channel);
                    responseDto.setMemberIds(channel.getMembers().stream()
                            .map(User::getId)
                            .collect(Collectors.toSet()));
                    return responseDto;
                });
    }


    @Override
    public List<ChannelResponseDto> findAllByUserId(UUID userId) {
        return channelRepository.findAllByUserId(userId).stream()
                .filter(channel -> {
                    boolean isPublic = channel.getType() == ChannelType.PUBLIC;
                    boolean isPrivateAndMember = !isPublic && channel.getMembers().stream()
                            .anyMatch(member -> member.getId().equals(userId));
                    return isPublic || isPrivateAndMember;
                })
                .map(channel -> {
                    ChannelResponseDto dto = createChannelResponseDto(channel);

                    if (channel.getType() != ChannelType.PUBLIC) {
                        dto.setMemberIds(channel.getMembers().stream()
                                .map(User::getId)
                                .collect(Collectors.toSet()));
                    }

                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public boolean deleteChannel(UUID id, User user) {
        try {
            Optional<Channel> channelOpt = channelRepository.getChannel(
                    new GetPublicChannelRequestDto(id));

            if (channelOpt.isPresent()) {
                Channel channel = channelOpt.get();

                channel.getMessages().forEach(message ->
                        messageRepository.deleteMessage(
                                new MessageDeleteRequestDto(
                                        message.getId(),
                                        channel.getId(),
                                        user.getId()
                                )
                        )
                );

                channel.getMembers().forEach(member -> {
                    ReadStatus readStatus = readStatusRepository.findAllByUserId(member.getId())
                            .stream()
                            .filter(rs -> rs.getChannelId().equals(id))
                            .findFirst()
                            .orElse(null);

                    if (readStatus != null) {
                        readStatusRepository.deleteReadStatus(readStatus.getId());
                    }
                });

                return channelRepository.deleteChannel(id, user);
            }
            return false;

        } catch (NoSuchElementException e) {
            System.out.println("존재하지 않는 채널입니다.");
            return false;
        }
    }

    @Override
    public boolean modifyChannel(ChannelUpdateRequestDto channelUpdateRequestDto) {
        return channelRepository.modifyChannel(channelUpdateRequestDto);
    }

    @Override
    public boolean kickOutChannel(UUID channelId, User kickUser, User admin) {
        return channelRepository.kickOutChannel(channelId, kickUser, admin);
    }

    @Override
    public boolean joinChannel(Channel channel, User user) {
        return channelRepository.joinChannel(channel, user);
    }

    private ChannelResponseDto createChannelResponseDto(Channel channel) {
        ChannelResponseDto dto = new ChannelResponseDto();
        dto.setChannelId(channel.getId());
        dto.setName(channel.getName());
        dto.setDescription(channel.getDescription());
        dto.setAdmin(channel.getChannelAdmin());
        dto.setType(channel.getType());

        // 최근 메시지 시간 설정
        if (!channel.getMessages().isEmpty()) {
            Message lastMessage = channel.getMessages().get(channel.getMessages().size() - 1);
            dto.setLastMessageAt(lastMessage.getCreatedAt());
        }

        return dto;
    }

}
