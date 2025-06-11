package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.common.ErrorMessages;
import com.sprint.mission.discodeit.dto.data.ReadStatusDto;
import com.sprint.mission.discodeit.dto.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.mapper.ReadStatusMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ReadStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class BasicReadStatusService implements ReadStatusService {

    public final UserRepository userRepository;
    public final ChannelRepository channelRepository;
    public final ReadStatusRepository readStatusRepository;
    public final ReadStatusMapper readStatusMapper;

    @Override
    @Transactional
    public ReadStatusDto create(ReadStatusCreateRequest request) {
        UUID userId = request.userId();
        UUID channelId = request.channelId();

        User user = userRepository.findById(userId)
            .orElseThrow(() -> new NoSuchElementException(
                ErrorMessages.format("user", ErrorMessages.ERROR_NOT_FOUND)));
        Channel channel = channelRepository.findById(channelId)
            .orElseThrow(() -> new NoSuchElementException(
                ErrorMessages.format("channel", ErrorMessages.ERROR_NOT_FOUND)));

//    boolean exists = readStatusRepository.findAllByUserId(userId).stream()
//        .anyMatch(readStatus -> readStatus.getChannel().getId().equals(channelId));

        //쿼리 메서드로 존재 여부 체크 ( 더 굿?)
        if (readStatusRepository.findByUserIdAndChannelId(userId, channelId).isPresent()) {
            throw new IllegalArgumentException(
                ErrorMessages.format("ReadStatus with user and channel", ErrorMessages.ERROR_EXISTS));
        }

        Instant lastReadAt = request.lastReadAt();
        ReadStatus readStatus = new ReadStatus(user, channel, lastReadAt);
        ReadStatus savedReadStatus = readStatusRepository.save(readStatus);

        return readStatusMapper.toDto(savedReadStatus);
    }

    @Transactional(readOnly = true)
    @Override
    public ReadStatusDto find(UUID id) {
        ReadStatus readStatus = readStatusRepository.findById(id)
            .orElseThrow(() -> new NoSuchElementException(
                ErrorMessages.format("ReadStatus", ErrorMessages.ERROR_NOT_FOUND)));

        return readStatusMapper.toDto(readStatus);
    }

    @Transactional(readOnly = true)
    @Override
    public List<ReadStatusDto> findAllByUserId(UUID userId) {

        return readStatusRepository.findAllByUserId(userId)
            .stream()
            .map(readStatusMapper::toDto)
            .toList();
    }

    @Override
    @Transactional
    public ReadStatusDto update(UUID readStatusId, ReadStatusUpdateRequest request) {
        ReadStatus readStatus = readStatusRepository.findById(readStatusId)
            .orElseThrow(() -> new NoSuchElementException(
                ErrorMessages.format("ReadStatus", ErrorMessages.ERROR_NOT_FOUND)));

        readStatus.update(request.newLastReadAt());

        return readStatusMapper.toDto(readStatus);
    }

    @Transactional
    @Override
    public void delete(UUID id) {
        if (!readStatusRepository.existsById(id)) {
            throw new NoSuchElementException(
                ErrorMessages.format("ReadStatus", ErrorMessages.ERROR_NOT_FOUND));
        }

        readStatusRepository.deleteById(id);
    }
}
