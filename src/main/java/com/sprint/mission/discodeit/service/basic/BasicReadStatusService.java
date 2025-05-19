package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.dto.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ReadStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BasicReadStatusService implements ReadStatusService {
    private final ReadStatusRepository readStatusRepository;
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;

    @Override
    public ReadStatus create(ReadStatusCreateRequest readStatusCreateRequest) {
        UUID userId = readStatusCreateRequest.getUserId();
        UUID channelId = readStatusCreateRequest.getChannelId();

        if (userRepository.loadById(userId) == null) {
            throw new IllegalArgumentException("[ReadStatus] 존재하지 않은 사용자입니다. (" + userId + ")");
        }

        if (channelRepository.loadById(channelId) == null) {
            throw new IllegalArgumentException("[ReadStatus] 존재하지 채널 사용자입니다. (" + channelId + ")");
        }

        if (readStatusRepository
                .loadAllByUserId(userId)
                .stream()
                .anyMatch(rs -> rs.getChannelId().equals(channelId))) {
            throw new IllegalArgumentException("[ReadStatus] 이미 관련된 객체가 존재합니다. (user=" + userId + ", channel=" + channelId + ")");
        }

        ReadStatus readStatus = ReadStatus.of(readStatusCreateRequest.getUserId(), readStatusCreateRequest.getChannelId());
        readStatusRepository.save(readStatus);

        return readStatus;
    }

    @Override
    public ReadStatus find(UUID id) {
        return readStatusRepository.loadById(id);
    }

    @Override
    public List<ReadStatus> findAllByUserId(UUID id) {
        return readStatusRepository.loadAllByUserId(id);
    }

    @Override
    public List<ReadStatus> findAllByChannelId(UUID channelId) {
        Channel channel  = channelRepository.loadById(channelId);
        if (channel == null) {
            throw new IllegalArgumentException("[ReadStatus] 존재하지 않는 채널입니다. (channelId: " + channelId + ")");
        }

        return channel.getMemberIds().stream()
                .flatMap(userId ->
                        readStatusRepository.loadAllByUserId(userId).stream()
                )
                .filter(readStatus -> readStatus.getChannelId().equals(channelId))
                .collect(Collectors.toList());
    }

    @Override
    public ReadStatus update(ReadStatusUpdateRequest readStatusUpdateRequest) {
        ReadStatus readStatus = readStatusRepository.loadAllByUserId(readStatusUpdateRequest.getUserId()).stream()
                .filter(r -> r.getChannelId().equals(readStatusUpdateRequest.getChannelId()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "[ReadStatus] 해당하는 상태가 없습니다. (user=" + readStatusUpdateRequest.getUserId() + ", channel=" + readStatusUpdateRequest.getChannelId() + ")"
                ));

        readStatus.update();
        readStatusRepository.save(readStatus);
        return readStatus;
    }

    @Override
    public void delete(UUID id) {
        readStatusRepository.deleteByUserId(id);
    }
}
