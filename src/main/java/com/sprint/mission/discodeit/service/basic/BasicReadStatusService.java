package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.annotation.Logging;
import com.sprint.mission.discodeit.dto.ReadStatus.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.ReadStatus.ReadStatusDto;
import com.sprint.mission.discodeit.dto.ReadStatus.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.Channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.ReadStatus.ReadStatusAlreadyExistsException;
import com.sprint.mission.discodeit.exception.ReadStatus.ReadStatusNotFoundException;
import com.sprint.mission.discodeit.exception.User.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.ReadStatusMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ReadStatusService;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Logging
@RequiredArgsConstructor
public class BasicReadStatusService implements ReadStatusService {

    private final ReadStatusRepository readStatusRepository;
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;

    private final ReadStatusMapper readStatusMapper;

    /**
     * 주어진 생성 요청 DTO를 기반으로 ReadStatus 생성
     *
     * @param createRequest ReadStatus 생성 요청 DTO
     * @return 생성된 ReadStatus
     * @throws UserNotFoundException
     * @throws ChannelNotFoundException
     * @throws ReadStatusAlreadyExistsException 같은 Channel, User와 관련된 ReadStatus가 이미 존재하는 경우
     */
    @Override
    @Transactional
    public ReadStatusDto create(ReadStatusCreateRequest createRequest) {

        UUID channelId = createRequest.channelId();
        UUID userId = createRequest.userId();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> UserNotFoundException.byId(userId));

        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new ChannelNotFoundException(channelId));

        if (readStatusRepository.existsByUserIdAndChannelId(userId, channelId)) {
            throw ReadStatusAlreadyExistsException.byUserIdAndChannelId(userId, channelId);
        }

        ReadStatus readStatus = ReadStatus.builder()
                .user(user)
                .channel(channel)
                .lastReadAt(createRequest.lastReadAt())
                .build();

        readStatusRepository.save(readStatus);
        return readStatusMapper.toDto(readStatus);
    }

    /**
     * 주어진 유저ID로 유저별 ReadStatus 전체 조회
     *
     * @param userId 조회할 ReadStatus의 유저ID
     * @return 조회된 유저별 ReadStatus 리스트
     */
    @Override
    @Transactional(readOnly = true)
    public List<ReadStatusDto> findAllByUserId(UUID userId) {

        return readStatusRepository.findAllByUserId(userId).stream()
                .map(readStatusMapper::toDto)
                .toList();
    }

    /**
     * 주어진 ID에 해당하는 ReadStatus 조회
     *
     * @param id 조회할 ReadStatus ID
     * @return 조회된 ReadStatus
     * @throws ReadStatusNotFoundException ReadStatus가 존재하지 않는 경우
     */
    @Override
    @Transactional(readOnly = true)
    public ReadStatusDto find(UUID id) {

        ReadStatus readStatus = readStatusRepository.findById(id)
                .orElseThrow(() -> new ReadStatusNotFoundException(id));

        return readStatusMapper.toDto(readStatus);
    }

    /**
     * 주어진 ID에 해당하는 ReadStatus 수정
     *
     * @param id            수정할 ReadStatus ID
     * @param updateRequest 수정 요청 DTO
     * @return 수정된 ReadStatus
     * @throws ReadStatusNotFoundException ReadStatus가 존재하지 않는 경우
     */
    @Override
    @Transactional
    public ReadStatusDto update(UUID id, ReadStatusUpdateRequest updateRequest) {

        ReadStatus readStatus = readStatusRepository.findById(id)
                .orElseThrow(() -> new ReadStatusNotFoundException(id));

        readStatus.update(updateRequest.newLastReadAt());
        return readStatusMapper.toDto(readStatus);
    }

    /**
     * 주어진 ID에 해당하는 ReadStatus 삭제
     *
     * @param id 삭제할 ReadStatus ID
     * @throws ReadStatusNotFoundException ReadStatus가 존재하지 않는 경우
     */
    @Override
    @Transactional
    public void delete(UUID id) {

        if (!readStatusRepository.existsById(id)) {
            throw new ReadStatusNotFoundException(id);
        }

        readStatusRepository.deleteById(id);
    }
}
