package com.sprint.mission.discodeit.service.readStatus;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.dto.readStatus.ReadStatusCreateRequestDto;
import com.sprint.mission.discodeit.entity.dto.readStatus.ReadStatusUpdateRequestDto;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReadStatusServiceImpl implements ReadStatusService {

    private final ReadStatusRepository readStatusRepository;
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;

    @Override
    public ReadStatus createReadStatus(ReadStatusCreateRequestDto dto) {
        validateCreateReadStatus(dto);

        ReadStatus readStatus = new ReadStatus(dto.getUserId(), dto.getChannelId());
        return readStatusRepository.createReadStatus(readStatus);
    }

    @Override
    public ReadStatus findReadStatusById(UUID statusId) {
        return readStatusRepository.findReadStatusById(statusId)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 읽기 상태입니다."));
    }

    @Override
    public List<ReadStatus> findAllByUserId(UUID userId) {
        validateUser(userId);
        return readStatusRepository.findAllByUserId(userId);
    }

    @Override
    public boolean updateReadStatus(ReadStatusUpdateRequestDto dto) {
        ReadStatus readStatus = findReadStatusById(dto.getStatusId());
        readStatus.setJoinedAt(dto.getNowTime());

        return readStatusRepository.updateReadStatus(readStatus);
    }

    @Override
    public boolean deleteReadStatus(UUID statusId) {
        if (!readStatusRepository.findReadStatusById(statusId).isPresent()) {
            throw new NoSuchElementException("존재하지 않는 읽기 상태입니다.");
        }
        return readStatusRepository.deleteReadStatus(statusId);
    }

    private void validateCreateReadStatus(ReadStatusCreateRequestDto dto) {
        if (dto == null) {
            throw new IllegalArgumentException("요청 데이터가 없습니다.");
        }

        validateUser(dto.getUserId());
        validateChannel(dto.getUserId(), dto.getChannelId());
        validateDuplicateStatus(dto.getUserId(), dto.getChannelId());
    }

    private void validateUser(UUID userId) {
        if (!userRepository.getUser(userId).isPresent()) {
            throw new NoSuchElementException("존재하지 않는 사용자입니다.");
        }
    }

    private void validateChannel(UUID userId, UUID channelId) {
        List<Channel> userChannels = channelRepository.findAllByUserId(userId);
        if (userChannels.stream().noneMatch(channel -> channel.getId().equals(channelId))) {
            throw new NoSuchElementException("존재하지 않는 채널입니다.");
        }
    }

    private void validateDuplicateStatus(UUID userId, UUID channelId) {
        boolean isDuplicate = readStatusRepository.findAllByUserId(userId).stream()
                .anyMatch(status -> status.getChannelId().equals(channelId));

        if (isDuplicate) {
            throw new IllegalArgumentException("이미 존재하는 읽기 상태입니다.");
        }
    }

}
