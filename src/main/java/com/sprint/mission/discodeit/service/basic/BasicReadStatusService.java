package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.ReadStatus.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.ReadStatus.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ReadStatusService;
import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BasicReadStatusService implements ReadStatusService {
    private final ReadStatusRepository readStatusRepository;
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;

    public ReadStatus create(ReadStatusCreateRequest request) {
        UUID userId = request.userId();
        UUID channelId = request.channelId();

        if (!userRepository.existsById(userId)) {
            throw new NoSuchElementException("해당하는 유저 아이디 없습니다.");
        }

        if (!channelRepository.existsById(channelId)) {
            throw new NoSuchElementException("해당하는 채널 아이디 없습니다.");
        }
        if (readStatusRepository.findAllByUserId(request.userId()).stream()
                .anyMatch(readStatus -> readStatus.getChannelId().equals(channelId))) {
            throw new RuntimeException("이미 ReadStatus가 존재합니다.");
        }

        Instant recentReadAt = request.recentReadAt();
        ReadStatus readStatus = new ReadStatus(userId, channelId, recentReadAt);

        return readStatusRepository.save(readStatus);
    }

    public ReadStatus find(UUID readStatusId) {
        return readStatusRepository.findById(readStatusId)
                .orElseThrow(() -> new NoSuchElementException("해당 id를 가진 ReadStatus는 없습니다."));
    }

    public List<ReadStatus> findAllByUserId(UUID userId) {
        return readStatusRepository.findAllByUserId(userId).stream().toList();
    }


    public ReadStatus update(UUID readStatusId, ReadStatusUpdateRequest request) {
        Instant newReadAt = request.newReadAt();

        ReadStatus readStatus = readStatusRepository.findById(readStatusId)
                .orElseThrow(() -> new NoSuchElementException("해당 id를 가진 ReadStatus는 없습니다."));

        readStatus.update(newReadAt);
        return readStatusRepository.save(readStatus);
    }


    public void delete(UUID readStatusId) {
        if (!readStatusRepository.existsById(readStatusId)) {
            throw new NoSuchElementException("해당 id를 가진 ReadStatus는 없습니다.");
        }
        readStatusRepository.deleteById(readStatusId);
    }
}
