package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.readstatus.ReadStatusRequestDto;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusResponseDto;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusUpdateDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.readstatus.ReadStatusAlreadyExistsException;
import com.sprint.mission.discodeit.exception.channel.NotFoundChannelException;
import com.sprint.mission.discodeit.exception.readstatus.NotFoundReadStatusException;
import com.sprint.mission.discodeit.exception.user.NotFoundUserException;
import com.sprint.mission.discodeit.mapper.struct.ReadStatusStructMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ReadStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service("basicReadStatusService")
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BasicReadStatusService implements ReadStatusService {

    private final ReadStatusRepository readStatusRepository;
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;
    private final ReadStatusStructMapper readStatusMapper;

    @Override
    @Transactional
    public ReadStatusResponseDto create(ReadStatusRequestDto readStatusRequestDTO) {
        UUID userId = readStatusRequestDTO.userId();
        UUID channelId = readStatusRequestDTO.channelId();

        if (!userRepository.existsById(userId)) {
            throw new NotFoundUserException(userId);
        }

        if (!channelRepository.existsById(channelId)) {
            throw new NotFoundChannelException(channelId);
        }

        if (readStatusRepository.existsByUserIdAndChannelId(userId, channelId)) {
            throw new ReadStatusAlreadyExistsException(userId, channelId);
        }

        User user = findUser(userId);
        Channel channel = findChannel(channelId);
        Instant lastReadAt = readStatusRequestDTO.lastReadAt();

        ReadStatus readStatus = ReadStatus.builder()
                .user(user)
                .channel(channel)
                .lastReadAt(lastReadAt)
                .build();

        readStatusRepository.save(readStatus);

        return readStatusMapper.toDto(readStatus);
    }

    @Override
    public ReadStatusResponseDto findById(UUID id) {
        ReadStatus readStatus = findReadStatus(id);

        return readStatusMapper.toDto(readStatus);
    }

    @Override
    public List<ReadStatusResponseDto> findAllByUserId(UUID userId) {
        return readStatusRepository.findAllByUserId(userId).stream()
                .map(readStatusMapper::toDto)
                .toList();
    }

    @Override
    @Transactional
    public ReadStatusResponseDto update(UUID id, ReadStatusUpdateDto readStatusUpdateDTO) {
        ReadStatus readStatus = findReadStatus(id);

        Instant updateTime = readStatusUpdateDTO.newLastReadAt() != null
                ? readStatusUpdateDTO.newLastReadAt()
                : Instant.now();

        readStatus.updateLastReadAt(updateTime);
        readStatusRepository.save(readStatus);

        return readStatusMapper.toDto(readStatus);
    }

    @Override
    @Transactional
    public void deleteById(UUID id) {
        readStatusRepository.deleteById(id);
    }

    private ReadStatus findReadStatus(UUID id) {
        return readStatusRepository.findById(id)
                .orElseThrow(() -> new NotFoundReadStatusException(id));
    }

    private User findUser(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundUserException(id));
    }

    private Channel findChannel(UUID id) {
        return channelRepository.findById(id)
                .orElseThrow(() -> new NotFoundChannelException(id));
    }
}
