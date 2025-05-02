package com.sprint.mission.discodeit.service.readStatus;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.dto.readStatus.ReadStatusCreateRequestDto;
import com.sprint.mission.discodeit.entity.dto.readStatus.ReadStatusUpdateRequestDto;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class ReadStatusServiceImpl implements ReadStatusService {

    private final ReadStatusRepository readStatusRepository;
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;

    @Override
    public ReadStatus createReadStatus(ReadStatusCreateRequestDto readStatusCreateRequestDto) {

        List<Channel> userChannels = channelRepository.findAllByUserId(readStatusCreateRequestDto.getUserId());

        boolean channelExists = userChannels.stream()
                .anyMatch(channel -> channel.getId().equals(readStatusCreateRequestDto.getChannelId()));

        if (!channelExists) {
            throw new NoSuchElementException("존재하지 않는 채널입니다.");
        }

        // 2. User 존재 여부 확인
        Optional<User> userOptional = userRepository.getUser(readStatusCreateRequestDto.getUserId());
        if (userOptional.isEmpty()) {
            throw new NoSuchElementException("존재하지 않는 유저입니다.");
        }

        List<ReadStatus> readStatuses = readStatusRepository.findAllByUserId(readStatusCreateRequestDto.getUserId());

        readStatuses.forEach(readStatus -> {
            if (readStatus.getChannelId().equals(readStatusCreateRequestDto.getChannelId())
                && readStatus.getUserId().equals(readStatusCreateRequestDto.getUserId())) {
                throw new IllegalArgumentException("이미 존재하는 데이터 입니다.");
            }
        });
        return readStatusRepository.createReadStatus(readStatusCreateRequestDto);
    }

    @Override
    public ReadStatus findReadStatusById(UUID readStatusId) {
        return readStatusRepository.findReadStatusById(readStatusId);
    }

    @Override
    public List<ReadStatus> findAllByUserId(UUID userId) {
        return readStatusRepository.findAllByUserId(userId);
    }

    @Override
    public boolean updateReadStatus(ReadStatusUpdateRequestDto readStatusUpdateRequestDto) {
        return readStatusRepository.updateReadStatus(readStatusUpdateRequestDto);
    }

    @Override
    public boolean deleteReadStatus(UUID readStatusId) {
        return readStatusRepository.deleteReadStatus(readStatusId);
    }
}
