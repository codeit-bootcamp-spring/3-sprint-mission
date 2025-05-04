package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.dto.CreateReadStatusRequest;
import com.sprint.mission.discodeit.entity.dto.UpdateReadStatusRequest;
import com.sprint.mission.discodeit.entity.exception.DuplicateReadStatusException;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

public class BasicReadStatusService {

    ChannelRepository channelRepository;
    UserRepository userRepository;
    ReadStatusRepository readStatusRepository;

    public ReadStatus create(CreateReadStatusRequest request) {

        // 1. User 존재 여부 확인
        User user = userRepository.findById(request.user().getId())
                .orElseThrow(() -> new NoSuchElementException("User not found"));

        // 2. Channel 존재 여부 확인
        Channel channel = channelRepository.findById(request.channel().getId())
                .orElseThrow(() -> new NoSuchElementException("Channel not found"));

        // 3. 이미 같은 Channel과 User에 대한 ReadStatus 존재 여부 확인
        boolean exists = readStatusRepository.existByChannelIdAndUserId(request.user().getId(), request.channel().getId());
        if (exists) {
            throw new DuplicateReadStatusException("ReadStatus already exists for this channel and user ");
        }

        // 4. ReadStatus 객체 생성
        ReadStatus readStatus = new ReadStatus(user, channel);

        // 5. 저장
        return readStatusRepository.save(readStatus);
    }

    public Optional<ReadStatus> find(UUID id) {

        return Optional.ofNullable(readStatusRepository.findById(id));
    }

    public List<ReadStatus> findAllByUserId(UUID userId) {

        return readStatusRepository.findByUserId(userId);
    }

    public ReadStatus update(UpdateReadStatusRequest request) {
        if (!readStatusRepository.existByChannelIdAndUserId(request.channel().getId(), request.user().getId())) {
            throw new NoSuchElementException("ReadStatus not found for this channel and user");
        }
        ReadStatus readStatus = readStatusRepository.findByUserIdAndChannelId(request.user().getId(), request.channel().getId());

        return readStatusRepository.update(readStatus);
    }

    public void delete(UUID id) {
        readStatusRepository.delete(id);
    }
}
