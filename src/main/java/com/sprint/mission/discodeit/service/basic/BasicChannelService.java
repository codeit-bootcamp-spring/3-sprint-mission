package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.response.ChannelDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.channel.PrivateChannelUpdateException;
import com.sprint.mission.discodeit.mapper.ChannelMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class BasicChannelService implements ChannelService {

    //레포지토리 의존성
    private final ChannelRepository channelRepository;
    private final ReadStatusRepository readStatusRepository;
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final ChannelMapper channelMapper;

    private static final Logger logger = Logger.getLogger(BasicChannelService.class.getName()); // 필드로 Logger 선언

    // 공개 채널 생성
    @Override
    @Transactional
    public ChannelDto createPublicChannel(PublicChannelCreateRequest request) {

        Channel channel = new Channel(
                request.name(),
                ChannelType.PUBLIC,
                request.description()
        );

        channelRepository.save(channel);
        return channelMapper.toDto(channel);
    }

    // 비공개 채널 생성
    @Override
    @Transactional
    public ChannelDto createPrivateChannel(PrivateChannelCreateRequest request) {

        Channel channel = new Channel(
                null,
                ChannelType.PRIVATE,
                null
        );
        channelRepository.save(channel);

        request.participantIds().stream()
                .map(userId -> new ReadStatus(userRepository.findById(userId).get(), channel, channel.getCreatedAt()))
                .forEach(readStatusRepository::save);

        return channelMapper.toDto(channel);
    }

    // 채널 공개 여부 별로 조건 달아준 전체 조회
    @Override
    @Transactional(readOnly = true)
    public List<ChannelDto> findAllByUserId(UUID userId) {

        List<UUID> participants = readStatusRepository.findAllByUserId(userId).stream()
                .map(readStatus -> readStatus.getChannel().getId())
                .toList();

        return channelRepository.findAll().stream()
                .filter(channel ->
                        channel.getType().equals(ChannelType.PUBLIC)
                                || participants.contains(channel.getId())
                )
                .map(channelMapper::toDto)
                .toList();
    }

    // id로 채널 조회
    @Override
    @Transactional(readOnly = true)
    public ChannelDto find(UUID channelId) {
        return channelRepository.findById(channelId)
                .map(channelMapper::toDto)
                .orElseThrow(
                        () -> new ChannelNotFoundException(channelId));
    }

    // 채널 이름 수정
    @Override
    @Transactional
    public ChannelDto update(UUID channelId, PublicChannelUpdateRequest request) {

        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new ChannelNotFoundException(channelId));

        if (channel.getType().equals(ChannelType.PRIVATE)) {
            throw new PrivateChannelUpdateException(channelId);
        }

        channel.updateChannelName(request.newName());
        channel.updateDescription(request.newDescription());
        logger.info("ChannelService: 채널 이름과 설명 수정이 완료되었습니다.");

        return channelMapper.toDto(channel);
    }

    // 채널 삭제
    @Override
    @Transactional
    public void delete(UUID channelId) {
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(
                        () -> new ChannelNotFoundException(channelId));

        List<Message> messages = messageRepository.findAllByChannelId(channelId);
        messageRepository.deleteAll(messages);

        List<ReadStatus> readStatuses = readStatusRepository.findAllByChannelId(channelId);
        readStatusRepository.deleteAll(readStatuses);

        channelRepository.deleteById(channelId);
        logger.info("ChannelService: 채널이 삭제되었습니다.");
    }
}
