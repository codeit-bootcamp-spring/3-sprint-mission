package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.channel.ChannelResponseDto;
import com.sprint.mission.discodeit.dto.channel.PrivateChannelDto;
import com.sprint.mission.discodeit.dto.channel.PublicChannelDto;
import com.sprint.mission.discodeit.dto.channel.PublicChannelUpdateDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.PrivateChannelModificationException;
import com.sprint.mission.discodeit.exception.notfound.NotFoundChannelException;
import com.sprint.mission.discodeit.exception.notfound.NotFoundUserException;
import com.sprint.mission.discodeit.mapper.ChannelMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service("basicChannelService")
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BasicChannelService implements ChannelService {

    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;
    private final ReadStatusRepository readStatusRepository;
    private final ChannelMapper channelMapper;

    @Override
    @Transactional
    public ChannelResponseDto createPublicChannel(PublicChannelDto publicChannelDto) {

        String name = publicChannelDto.name();
        String description = publicChannelDto.description();

        log.info("[BasicChannelService] 공개 채널 생성 요청 - name: {}, description: {}",
                name, description);

        Channel channel = Channel.builder()
                .name(name)
                .description(description)
                .type(ChannelType.PUBLIC)
                .build();

        Channel savedChannel = channelRepository.save(channel);

        log.info("[BasicChannelService] 공개 채널 생성 성공 - id: {}, name: {}, description: {}",
                savedChannel.getId(), savedChannel.getName(), savedChannel.getDescription());

        return channelMapper.toDto(savedChannel);
    }

    @Override
    @Transactional
    public ChannelResponseDto createPrivateChannel(PrivateChannelDto privateChannelDto) {
        log.info("[BasicChannelService] 개인 채널 생성 요청 participants: {}", privateChannelDto.participantIds().size());

        Channel channel = new Channel();

        Channel createdChannel = channelRepository.save(channel);

        // 읽음 상태 추가
        List<ReadStatus> readStatuses = privateChannelDto.participantIds().stream()
                .map(userId -> {
                    User user = findUser(userId);
                    ReadStatus readStatus = ReadStatus.builder()
                            .user(user)
                            .channel(channel)
                            .lastReadAt(createdChannel.getCreatedAt())
                            .build();

                    return readStatus;
                })
                .toList();

        readStatusRepository.saveAll(readStatuses);

        log.info("[BasicChannelService] 비공개 채널 생성 성공 id: {}", createdChannel.getId());

        return channelMapper.toDto(createdChannel);
    }

    @Override
    public ChannelResponseDto findById(UUID channelId) {
        Channel channel = findChannel(channelId);

        return channelMapper.toDto(channel);
    }

    @Override
    public List<ChannelResponseDto> findAllByUserId(UUID userId) {
        List<UUID> participatedChannelIds = readStatusRepository.findAllByUserId(userId).stream()
                .map(readStatus -> readStatus.getChannel().getId())
                .toList();

        return channelRepository.findAll().stream()
                .filter(channel ->
                        channel.getType().equals(ChannelType.PUBLIC)
                                || participatedChannelIds.contains(channel.getId()))
                .map(channelMapper::toDto)
                .toList();
    }

    @Override
    @Transactional
    public ChannelResponseDto update(UUID channelId, PublicChannelUpdateDto publicChannelUpdateDto) {
        Channel channel = findChannel(channelId);

        String newName = publicChannelUpdateDto.newName();
        String newDescription = publicChannelUpdateDto.newDescription();

        log.info("[BasicChannelService] 공개 채널 수정 요청: id: {}, newName: {}, newDescription: {}",
                channelId, newName, newDescription);

        // PRIVATE 채널은 수정 불가
        if (channel.getType().equals(ChannelType.PRIVATE)) {
            throw new PrivateChannelModificationException();
        }

        // 변경 사항 적용
        channel.updateName(newName);
        channel.updateDescription(newDescription);

        Channel updatedChannel = channelRepository.save(channel);

        log.info("[BasicChannelService] 공개 채널 수정 성공: id: {}, newName: {}, newDescription: {}",
                updatedChannel.getId(), updatedChannel.getName(), updatedChannel.getDescription());

        return channelMapper.toDto(updatedChannel);
    }

    @Override
    @Transactional
    public void deleteById(UUID channelId) {
        log.info("[BasicChannelService] 채널 삭제 요청: id: {}", channelId);

        Channel channel = findChannel(channelId);

        log.info("[BasicChannelService] 삭제할 채널 조회 완료: id: {}", channel.getId());

        channelRepository.deleteById(channelId);

        log.info("[BasicUserService] 채널 삭제 완료 - channelId: {}", channelId);
    }


    private Channel findChannel(UUID id) {
        return channelRepository.findById(id)
                .orElseThrow(NotFoundChannelException::new);
    }

    private User findUser(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(NotFoundUserException::new);
    }
}
