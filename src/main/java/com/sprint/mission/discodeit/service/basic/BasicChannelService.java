package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.request.channel.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.channel.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.channel.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.channel.PrivateChannelUpdateException;
import com.sprint.mission.discodeit.mapper.ChannelMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

/**
 * 채널 관련 비즈니스 로직을 처리하는 서비스 클래스입니다.
 *
 * <p>공개/비공개 채널 생성, 수정, 삭제, 조회 등의 기능을 제공합니다.</p>
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class BasicChannelService implements ChannelService {

    private final ChannelRepository channelRepository;
    private final ReadStatusRepository readStatusRepository;
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final ChannelMapper channelMapper;

    private static final String SERVICE_NAME = "[ChannelService] ";

    /**
     * 공개 채널을 생성합니다.
     *
     * @param request 공개 채널 생성 요청 정보
     * @return 생성된 채널
     */
    @Override
    @Transactional
    public ChannelDto create(PublicChannelCreateRequest request) {
        log.info(SERVICE_NAME + "공개 채널 생성 시도: {}", request);

        String name = request.name();
        String description = request.description();
        Channel channel = new Channel(ChannelType.PUBLIC, name, description);
        Channel savedChannel = channelRepository.save(channel);

        log.info(SERVICE_NAME + "공개 채널 생성 완료: ID = {}", savedChannel.getId());
        log.info(SERVICE_NAME + "공개 채널 이름: {}", name);
        return channelMapper.toDto(savedChannel);
    }

    /**
     * 비공개 채널을 생성하고 참가자 정보를 등록합니다.
     *
     * @param request 비공개 채널 생성 요청 정보
     * @return 생성된 채널 DTO
     */
    @Override
    @Transactional
    public ChannelDto create(PrivateChannelCreateRequest request) {
        log.info(SERVICE_NAME + "비공개 채널 생성 요청: {}", request);

        Channel channel = new Channel(ChannelType.PRIVATE, null, null);
        Channel createdChannel = channelRepository.save(channel);

        request.participantIds().forEach(participantId -> {
            userRepository.findById(participantId).ifPresentOrElse(
                    user -> {
                        readStatusRepository.save(new ReadStatus(user, createdChannel, Instant.MIN));
                        log.debug(SERVICE_NAME + "비공개 채널 참가자 추가: Channel ID = {}, User ID = {}",
                                createdChannel.getId(), user.getId());
                    },
                    () -> log.error(SERVICE_NAME + "참가자 ID를 찾을 수 없음: {}", participantId)
            );
        });

        log.info(SERVICE_NAME + "비공개 채널 생성 완료: ID = {}", createdChannel.getId());
        return channelMapper.toDto(createdChannel);

//        request.participantIds().stream()
//            .map(userRepository::findById)
//            .filter(Optional::isPresent)
//            .map(Optional::get)
//            .map(user -> new ReadStatus(user, createdChannel, Instant.MIN))
//            .forEach(readStatusRepository::save);
//
//        return channelMapper.toDto(createdChannel);
    }

    /**
     * 채널을 ID로 조회합니다.
     *
     * @param channelId 조회할 채널 ID
     * @return 조회된 채널 DTO
     * @throws NoSuchElementException 채널이 존재하지 않을 경우
     */
    @Override
    @Transactional(readOnly = true)
    public ChannelDto find(UUID channelId) {
        log.debug(SERVICE_NAME + "채널 조회 시도: ID = {}", channelId);

        ChannelDto channelDto = channelRepository.findById(channelId)
            .map(channelMapper::toDto)
            .orElseThrow(() -> {
                log.error(SERVICE_NAME + "채널 조회 실패: 존재하지 않는 ID = {}", channelId);
                return new ChannelNotFoundException("채널을 찾을 수 없습니다.");
            });

        log.debug(SERVICE_NAME + "채널 조회 성공: ID = {}", channelId);
        return channelDto;
    }

    /**
     * 특정 사용자가 접근 가능한 채널 목록을 조회합니다.
     *
     * @param userId 사용자 ID
     * @return 접근 가능한 채널 DTO 목록
     */
    @Override
    @Transactional(readOnly = true)
    public List<ChannelDto> findAllByUserId(UUID userId) {
        log.debug(SERVICE_NAME + "사용자 채널 목록 조회: ID = {}", userId);

        List<UUID> mySubscribedChannelIds = readStatusRepository.findAllByUserId(userId).stream()
            .map(ReadStatus::getChannel)
            .map(Channel::getId)
            .toList();

        List<ChannelDto> result = channelRepository.findAll().stream()
            .filter(channel ->
                channel.getType().equals(ChannelType.PUBLIC)
                    || mySubscribedChannelIds.contains(channel.getId())
            )
            .map(channelMapper::toDto)
            .toList();

        log.info(SERVICE_NAME + "사용자 채널 목록 조회 완료: user ID = {}, 조회된 채널 수 = {}", userId, result.size());
        return result;
    }

    /**
     * 공개 채널을 수정합니다.
     *
     * @param channelId 수정할 채널 ID
     * @param request   수정 요청 정보
     * @return 수정된 채널 DTO
     * @throws NoSuchElementException 채널이 존재하지 않을 경우
     * @throws IllegalArgumentException 비공개 채널을 수정하려 할 경우
     */
    @Override
    @Transactional
    public ChannelDto update(UUID channelId, PublicChannelUpdateRequest request) {
        log.info(SERVICE_NAME + "채널 수정 요청: ID = {}", channelId);

        Channel channel = channelRepository.findById(channelId)
            .orElseThrow(() -> {
                log.error(SERVICE_NAME + "채널 수정 실패: 존재하지 않는 ID = {}", channelId);
                return new ChannelNotFoundException("채널을 찾을 수 없습니다.");
            });

        if (channel.getType().equals(ChannelType.PRIVATE)) {
            log.error(SERVICE_NAME + "비공개 채널은 수정 불가: ID = {}", channelId);
            throw new PrivateChannelUpdateException("비공개 채널은 수정할 수 없습니다.");
        }

        String newName = request.newName();
        String newDescription = request.newDescription();
        channel.update(newName, newDescription);
        channelRepository.save(channel);

        log.info(SERVICE_NAME + "채널 수정 완료: ID = {}", channelId);
        return channelMapper.toDto(channel);
    }

    /**
     * 채널을 삭제합니다.
     *
     * @param channelId 삭제할 채널 ID
     * @throws NoSuchElementException 채널이 존재하지 않을 경우
     */
    @Override
    @Transactional
    public void delete(UUID channelId) {
        log.info(SERVICE_NAME + "채널 삭제 시도: ID = {}", channelId);
        Channel channel = channelRepository.findById(channelId)
            .orElseThrow(() -> {
                log.error(SERVICE_NAME + "채널 삭제 실패: 존재하지 않는 ID = {}", channelId);
                return new ChannelNotFoundException("채널을 찾을 수 없습니다.");
            });

        messageRepository.deleteAllByChannelId(channel.getId());
        readStatusRepository.deleteAllByChannelId(channel.getId());
        channelRepository.deleteById(channelId);

        log.info(SERVICE_NAME + "채널 삭제 완료: ID = {}", channelId);
    }
}
