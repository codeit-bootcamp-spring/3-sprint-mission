package com.sprint.mission.discodeit.service.jcf;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import com.sprint.mission.discodeit.dto.channel.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.channel.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.channel.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.dto.data.ChannelDto;

public class JCFChannelService implements ChannelService {

    private static volatile JCFChannelService instance;
    private final ChannelRepository channelRepository;
    private final UserService userService;

    private JCFChannelService(UserService userService, ChannelRepository channelRepository) {
        this.userService = userService;
        this.channelRepository = channelRepository;
    }

    public static JCFChannelService getInstance(UserService userService, ChannelRepository channelRepository) {
        JCFChannelService result = instance;
        if (result == null) {
            synchronized (JCFChannelService.class) {
                result = instance;
                if (result == null) {
                    result = new JCFChannelService(userService, channelRepository);
                    instance = result;
                }
            }
        }
        return result;
    }

    @Override
    public Channel createChannel(PublicChannelCreateRequest request) {
        if (userService.getUserById(request.ownerId()) == null) {
            throw new IllegalArgumentException("존재하지 않는 사용자 ID로는 채널을 생성할 수 없습니다.");
        }
        Channel channel = new Channel(ChannelType.PUBLIC, request.channelName(), null, request.ownerId());
        return channelRepository.save(channel);
    }

    @Override
    public Channel createChannel(PrivateChannelCreateRequest request) {
        // DTO로부터 채널 이름, 소유자 ID, 참가자 목록을 가져옴
        String channelName = request.channelName();
        UUID ownerId = request.ownerId();
        List<UUID> participantIds = request.participantIds();

        // 소유자 유효성 검사 (UserService가 주입되어 있음)
        if (userService.getUserById(ownerId) == null) { 
            throw new IllegalArgumentException("Invalid owner ID for private channel creation: " + ownerId);
        }

        // Channel 엔티티 생성 (비밀번호는 null로 설정)
        Channel channel = new Channel(ChannelType.PRIVATE, channelName, null, ownerId);
        // Channel 생성자에서 ownerId는 이미 participantIds에 추가됨
        // DTO로 받은 추가 참가자들을 채널에 추가
        if (participantIds != null) {
            for (UUID participantId : participantIds) {
                channel.addParticipant(participantId); // Channel 엔티티의 addParticipant 사용
            }
        }

        // JCF 기반 Repository에 저장
        return channelRepository.save(channel);
    }

    @Override
    public Channel getChannelById(UUID channelId) {
        Optional<Channel> channelOptional = channelRepository.findById(channelId);
        return channelOptional.orElseThrow(() -> new IllegalArgumentException("채널이 존재하지 않습니다."));
    }

    @Override
    public List<Channel> getAllChannels() {
        return channelRepository.findAll();
    }

    @Override
    public Channel updateChannel(PublicChannelUpdateRequest request) {
        Optional<Channel> channelOptional = channelRepository.findById(request.channelId());
        Channel channel = channelOptional.orElseThrow(() -> new IllegalArgumentException("채널이 존재하지 않습니다."));

        if (request.channelName() != null && !request.channelName().isEmpty()) {
            channel.updateChannelName(request.channelName());
        }
        if (request.password() != null && !request.password().isEmpty()) {
            channel.updatePassword(request.password());
        }

        return channelRepository.save(channel);
    }

    @Override
    public void deleteChannel(UUID channelId) {
        channelRepository.deleteById(channelId);
    }

    @Override
    public List<ChannelDto> getChannelsByUserId(UUID userId) {
        List<Channel> allChannels = channelRepository.findAll();
        List<Channel> userChannels = allChannels.stream()
                .filter(channel -> channel.getParticipantIds() != null && channel.getParticipantIds().contains(userId))
                .collect(Collectors.toList());

        return userChannels.stream()
                .map(channel -> new ChannelDto(
                        channel.getChannelId(),
                        channel.getChannelType(),
                        channel.getChannelName(),
                        channel.getPassword(), // Or apply logic for private channels
                        channel.getParticipantIds(),
                        channel.getLastMessageAt() // Assuming Channel entity has this
                ))
                .collect(Collectors.toList());
    }
}
