package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.dto.response.ReadStatusDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.readstatus.ReadStatusAlreadyExistException;
import com.sprint.mission.discodeit.exception.readstatus.ReadStatusNotFoundException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.ReadStatusMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ReadStatusService;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Logger;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class BasicReadStatusService implements ReadStatusService {

    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;
    private final ReadStatusRepository readStatusRepository;
    private final ReadStatusMapper readStatusMapper;

    private static final Logger logger = Logger.getLogger(BasicReadStatusService.class.getName());

    // 생성
    @Override
    @Transactional
    public ReadStatusDto create(ReadStatusCreateRequest request) {

        User user = userRepository.findById(request.userId())
                .orElseThrow(
                        () -> new UserNotFoundException(request.userId()));

        Channel channel = channelRepository.findById(request.channelId())
                .orElseThrow(() -> new ChannelNotFoundException(request.channelId()));

        Optional<ReadStatus> existing = readStatusRepository.findByUserIdAndChannelId(request.userId(), request.channelId());
        if (existing.isPresent()) {
            throw new ReadStatusAlreadyExistException(request.channelId(), request.userId());
        }

        Instant lastReadAt = request.lastReadAt();
        // 객체 생성
        ReadStatus readStatus = new ReadStatus(user, channel, lastReadAt);
        // 저장
        readStatusRepository.save(readStatus);

        return readStatusMapper.toDto(readStatus);
    }

    // id로 readStatus 조회
    @Override
    @Transactional(readOnly = true)
    public Optional<ReadStatus> find(UUID id) {
        Optional<ReadStatus> result = readStatusRepository.findById(id);

        if (result.isEmpty()) {
            throw new ReadStatusNotFoundException(id);
        }
        return result;
    }

    // user id로 ReadStatus 조회
    @Override
    @Transactional(readOnly = true)
    public List<ReadStatusDto> findAllByUserId(UUID userId) {
        List<ReadStatus> result = readStatusRepository.findAllByUserId(userId);

        return result.stream()
                .map(readStatusMapper::toDto)
                .toList();
    }

    // ReadStatus 업데이트
    @Override
    @Transactional
    public ReadStatusDto update(UUID readStatusId, ReadStatusUpdateRequest request) {
        // id로 ReadStatus 조회
        ReadStatus readStatus = readStatusRepository.findById(readStatusId)
                .orElseThrow(
                        () -> new ReadStatusNotFoundException(readStatusId));

        readStatus.updateLastReadAt(request.newLastReadAt());

        return readStatusMapper.toDto(readStatus);
    }

    // 삭제
    @Override
    @Transactional
    public void delete(UUID id) {
        if (find(id).isEmpty()) {
            throw new ReadStatusNotFoundException(id);
        }
        readStatusRepository.deleteById(id);
    }
}
