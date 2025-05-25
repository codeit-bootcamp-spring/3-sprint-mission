package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ReadStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class BasicReadStatusService implements ReadStatusService {
    private final ReadStatusRepository readStatusRepository;
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;

    @Override
    public ReadStatus create(ReadStatusCreateRequest request) {
        UUID userId = request.userId();
        UUID channelId = request.channelId();

        if (!userRepository.existsById(userId)) {
            throw new NoSuchElementException("해당 사용자가 존재하지 않습니다.");
        }
        if (!channelRepository.existsById(channelId)) {
            throw new NoSuchElementException("해당 채널이 존재하지 않습니다.");
        }
        if (readStatusRepository.findAllByChannelId(channelId).stream()
                .anyMatch(readStatus -> readStatus.getUserId().equals(userId))) {
            throw new IllegalArgumentException("이미 존재하는 데이터입니다.");
        }

        Instant lastReadAt = request.lastReadAt();
        ReadStatus readStatus =
                ReadStatus.builder()
                        .userId(userId)
                        .channelId(channelId)
                        .lastReadAt(lastReadAt)
                        .build();

        return readStatusRepository.save(readStatus);
    }

    @Override
    public ReadStatus find(UUID readStatusId) {
        return readStatusRepository.findById(readStatusId)
                .orElseThrow(() -> new NoSuchElementException("데이터가 존재하지 않습니다."));
    }

    public List<ReadStatus> findAll() {
        return readStatusRepository.findAll();

    }

    @Override
    public List<ReadStatus> findAllByUserId(UUID userId) {
        System.out.println("Service의 findAllByUserId 메서드 호출됨!!");
        System.out.println("userId = " + userId);

        findAll().forEach(System.out::println);

        return readStatusRepository.findAllByUserId(userId).stream()
                .toList();
    }

    @Override
    public ReadStatus update(UUID readStatusId, ReadStatusUpdateRequest request) {
        Instant newLastReadAt = request.lastReadAt();
        ReadStatus readStatus = readStatusRepository.findById(readStatusId)
                .orElseThrow(() -> new NoSuchElementException("데이터가 존재하지 않습니다."));
        readStatus.update(newLastReadAt);
        return readStatusRepository.save(readStatus);
    }

    @Override
    public void delete(UUID readStatusId) {
        if (!readStatusRepository.existsById(readStatusId)) {
            throw new NoSuchElementException("ReadStatus with id " + readStatusId + " not found");
        }
        readStatusRepository.deleteById(readStatusId);
    }
}
