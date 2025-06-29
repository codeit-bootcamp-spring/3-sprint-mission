package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.channel.InvalidChannelParticipantException;
import com.sprint.mission.discodeit.exception.channel.PrivateChannelUpdateException;
import com.sprint.mission.discodeit.mapper.ChannelMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class BasicChannelService implements ChannelService {

    private final ChannelRepository channelRepository;
    private final ReadStatusRepository readStatusRepository;
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final ChannelMapper channelMapper;

    //private 채널생성
    @Transactional
    @Override
    public ChannelDto create(PrivateChannelCreateRequest request) {
        List<UUID> requestParticipantIds = new ArrayList<>(request.participantIds());
        log.info("[channel] 생성 요청: 참여자 수={}", requestParticipantIds.size());

        Channel privateChannel = channelRepository.save(new Channel(ChannelType.PRIVATE));
        log.info("[channel] 생성 완료: id={}, type={}", privateChannel.getId(),
            privateChannel.getType());

        List<User> users = userRepository.findAllById(requestParticipantIds);

        if (users.size() != requestParticipantIds.size()) {
            List<UUID> found = users.stream().map(User::getId).toList();
            requestParticipantIds.removeAll(found);

            log.warn("[channel] 참여자 추출 오류 - 유효하지 않은 Id 포함: {}", requestParticipantIds);
            throw new InvalidChannelParticipantException(privateChannel.getId(),
                requestParticipantIds);
        }

        List<ReadStatus> readStatuses = users.stream()
            .map(user -> new ReadStatus(user, privateChannel,
                privateChannel.getCreatedAt()))
            .toList();

        readStatusRepository.saveAll(readStatuses);
        log.info("[channel] ReadStatus 저장 완료: count={}", readStatuses.size());

        return channelMapper.toDto(privateChannel);
    }

    //public 채널생성
    @Transactional
    @Override
    public ChannelDto create(PublicChannelCreateRequest request) {
        String name = request.name();
        String description = request.description();
        log.info("[channel] 생성 요청: name={}, description={}", name, description);

        Channel channel = channelRepository.save(
            new Channel(ChannelType.PUBLIC, name, description));
        log.info("[channel] 저장 완료: id={}", channel.getId());

        return channelMapper.toDto(channel);
    }

    @Transactional(readOnly = true)
    @Override
    public ChannelDto find(UUID channelId) {
        log.info("[channel] 조회 요청: id={}", channelId);

        return channelRepository.findById(channelId)
            .map(channelMapper::toDto)
            .orElseThrow(() -> {
                log.warn("[channel] 조회 실패 - 존재하지 않는 id: id={}", channelId);
                return new ChannelNotFoundException(channelId);
            });
    }

    @Transactional(readOnly = true)
    @Override
    public List<ChannelDto> findAllByUserId(UUID userId) {
        log.info("[channel] 전체 조회 요청: userId={}", userId);
        List<UUID> mySubscribedChannelIds = readStatusRepository.findAllByUserId(userId).stream()
            .map(ReadStatus::getChannel)
            .map(Channel::getId)
            .toList();

        List<ChannelDto> channels = channelRepository.findAllByTypeOrIdIn(ChannelType.PUBLIC,
                mySubscribedChannelIds)
            .stream()
            .map(channelMapper::toDto)
            .toList();

        log.info("[channel] 전체 조회 응답: userId={}, 결과 개수={}", userId, channels.size());
        return channels;
    }

    @Transactional
    @Override
    public ChannelDto update(UUID channelId, PublicChannelUpdateRequest request) {
        String newName = request.newName();
        String newDescription = request.newDescription();
        log.info("[channel] 수정 요청: id={}, newName={}, newDescription={}", channelId, newName,
            newDescription);

        Channel channel = channelRepository.findById(channelId)
            .orElseThrow(() -> {
                log.warn("[channel] 수정 실패 - 존재하지 않는 id: id={}", channelId);
                return new ChannelNotFoundException(channelId);
            });

        //채널 타입이 private이면 수정 불가
        if (channel.getType().equals(ChannelType.PRIVATE)) {
            log.warn("[channel] 수정 실패 - private 타입: id={}, type={}", channelId,
                ChannelType.PRIVATE);
            throw new PrivateChannelUpdateException(channelId);
        }
        channel.update(newName, newDescription);
        log.info("[channel] 수정 완료: id={}, newName={}, newDescription={}", channelId, newName,
            newDescription);

        return channelMapper.toDto(channel);
    }

    @Transactional
    @Override
    public void delete(UUID channelId) {
        if (!channelRepository.existsById(channelId)) {
            log.warn("[channel] 삭제 실패 - 존재하지 않는 id: id={}", channelId);
            throw new ChannelNotFoundException(channelId);
        }

        messageRepository.deleteAllByChannelId(channelId);
        readStatusRepository.deleteAllByChannelId(channelId);
        channelRepository.deleteById(channelId);

        log.info("[channel] 삭제 완료: id={}", channelId);
    }
}
