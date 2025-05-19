package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.Channel.ChannelDto;
import com.sprint.mission.discodeit.dto.Channel.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.Channel.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.Channel.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BasicChannelService implements ChannelService {

    private final ChannelRepository channelRepository;
    private final ReadStatusRepository readStatusRepository;
    private final MessageRepository messageRepository;

    /**
     * 주어진 요청 DTO를 기반으로 Public 채널 생성 및 저장
     *
     * @param createRequestDTO 생성 요청 DTO
     * @return 생성된 채널
     */
    @Override
    public Channel create(PublicChannelCreateRequest createRequestDTO) {
        String name = createRequestDTO.name();
        String description = createRequestDTO.description();

        Channel ch = Channel.builder()
                .type(ChannelType.PUBLIC)
                .name(name)
                .description(description)
                .build();

        channelRepository.save(ch);
        return ch;
    }

    /**
     * 주어진 요청 DTO를 기반으로 Private 채널 생성
     *
     * @param createRequestDTO 생성 요청 DTO
     * @return 생성된 채널
     */
    @Override
    public Channel create(PrivateChannelCreateRequest createRequestDTO) {
        Channel ch = Channel.builder()
                .type(ChannelType.PRIVATE)
                .build();

        createRequestDTO.participantIds().stream()
                .map(userId -> ReadStatus.builder()
                        .userId(userId)
                        .channelId(ch.getId())
                        .lastReadAt(Instant.now())
                        .build())
                .forEach(readStatusRepository::save);

        channelRepository.save(ch);
        return ch;
    }

    /**
     * 주어진 채널을 기반으로 ChannelResponseDTO 생성
     *
     * @param ch 채널엔티티
     * @return ChannelResponseDTO
     */
    private ChannelDto buildChannelDTO(Channel ch) {
        // 가장 최근 메시지
        Message msg = messageRepository.findByChannelId(ch.getId()).stream()
                .reduce((a, b) -> b)
                .orElse(null);

        // PRIVATE 채널인 경우 참여한 User의 id 정보 포함
        if (ch.getType().equals(ChannelType.PRIVATE)) {
            List<UUID> userIdList = readStatusRepository.findUserIdByChannelId(ch.getId());
            return ChannelDto.from(ch,
                    msg != null ? msg.getCreatedAt() : null,
                    userIdList);
        } else {
            return ChannelDto.from(ch,
                    msg != null ? msg.getCreatedAt() : null);
        }
    }

    /**
     * 주어진 유저ID가 조회 가능한 채널 전체 조회
     *
     * @param userId 조회할 채널의 유저ID
     * @return 조회된 채널DTO 리스트
     */
    @Override
    public List<ChannelDto> findAllByUserId(UUID userId) {
        List<Channel> channelList = channelRepository.findAll();

        //유저가 속한 채널 ID
        Set<UUID> userChannelIds = readStatusRepository.findByUserId(userId).stream()
                .map(ReadStatus::getChannelId)
                .collect(Collectors.toSet());

        return channelList.stream()
                .filter(ch ->
                        // PUBLIC 채널 + PRIVATE 채널인 경우 유저가 포함된 채널만 반환
                        ch.getType().equals(ChannelType.PUBLIC)
                                || userChannelIds.contains(ch.getId())
                )
                .map(this::buildChannelDTO)
                .collect(Collectors.toList());

    }

    /**
     * 주어진 ID에 해당하는 채널 조회
     *
     * @param channelId 조회할 채널의 ID
     * @return 조회된 채널
     * @throws NoSuchElementException 해당 ID의 채널이 존재하지 않는 경우
     */
    @Override
    public ChannelDto find(UUID channelId) {
        Channel ch = channelRepository.findById(channelId)
                .orElseThrow(() -> new NoSuchElementException("해당 ID의 채널이 존재하지 않습니다."));

        return buildChannelDTO(ch);
    }

    /**
     * 주어진 채널ID에 해당하는 공개 채널을 수정 요청DTO의 값으로 수정
     *
     * @param channelId        수정 대상 채널ID
     * @param updateRequestDTO 수정 요청 DTO
     * @return 수정된 채널
     * @throws NoSuchElementException   해당 ID의 채널이 존재하지 않는 경우
     * @throws IllegalArgumentException 해당 채널이 PRIVATE 채널인 경우
     */
    @Override
    public Channel update(UUID channelId, PublicChannelUpdateRequest updateRequestDTO) {
        // 채널 유효성 검사
        Channel ch = channelRepository.findById(channelId)
                .orElseThrow(() -> new NoSuchElementException("해당 채널이 존재하지 않습니다."));

        if (ch.getType().equals(ChannelType.PRIVATE)) {
            throw new IllegalArgumentException("PRIVATE 채널은 수정할 수 없습니다.");
        }

        String newName = updateRequestDTO.newName();
        String newDescription = updateRequestDTO.newDescription();

        ch.update(newName, newDescription);

        channelRepository.save(ch);
        return ch;
    }

    /**
     * 주어진 ID에 해당하는 채널 삭제
     *
     * @param channelId 삭제할 대상 채널 ID
     */
    @Override
    public void delete(UUID channelId) {
        // 채널 유효성 검사
        channelRepository.findById(channelId)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 채널입니다."));

        messageRepository.findByChannelId(channelId).stream()
                .map(Message::getId)
                .forEach(messageRepository::deleteById);

        readStatusRepository.findByChannelId(channelId).stream()
                .map(ReadStatus::getId)
                .forEach(readStatusRepository::deleteById);

        channelRepository.deleteById(channelId);
    }
}
