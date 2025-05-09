package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.exception.ReadStatusAlreadyExistsException;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ReadStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicReadStatusService implements ReadStatusService {
    private final ReadStatusRepository readStatusRepository;
    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;


    @Override
    public ReadStatus create(ReadStatusCreateRequest createRequest) {
        // 1. `Channel`이나`User`가 존재하지 않으면 예외 발생
        if (this.userRepository.existsById(createRequest.userId())) {
            throw new NoSuchElementException("User with id " + createRequest.userId() + " not found");
        }

        if (this.channelRepository.existsById(createRequest.channelId())) {
            throw new NoSuchElementException("Channel with id " + createRequest.channelId() + " not found");
        }

        //2. 같은`Channel`과`User`와 관련된 객체가 이미 존재하면 예외를 발생
        boolean isAlreadyExist = this.readStatusRepository.findAllByChannelId(createRequest.channelId()).stream().anyMatch((status) -> status.getUserId().equals(createRequest.userId()));
        if (isAlreadyExist) {
            throw new ReadStatusAlreadyExistsException();
        }

        // 3. ReadStatus 생성
        ReadStatus readStatus = new ReadStatus(createRequest.userId(), createRequest.channelId());
        //4. DB저장

        return this.readStatusRepository.save(readStatus);
    }

    @Override
    public ReadStatus find(UUID readStatusId) {
        return this.readStatusRepository
                .findById(readStatusId)
                .orElseThrow(() -> new NoSuchElementException("readStatus with id " + readStatusId + " not found"));
    }

    @Override
    public List<ReadStatus> findAllByUserId(UUID userId) {
        return this.readStatusRepository.findAllByUserId(userId)
                .stream().toList();
    }

    @Override
    public ReadStatus update(UUID readStatusId, ReadStatusUpdateRequest updateRequest) {
        ReadStatus readStatus = this.readStatusRepository
                .findById(readStatusId)
                .orElseThrow(() -> new NoSuchElementException("readStatus with id " + readStatusId + " not found"));

        readStatus.update(updateRequest.lastReadAt());

        /* 업데이트 후 다시 DB 저장 */
        this.readStatusRepository.save(readStatus);

        return this.readStatusRepository
                .findById(readStatusId)
                .orElseThrow(() -> new NoSuchElementException("readStatus with id " + readStatusId + " not found"));
    }

    @Override
    public void delete(UUID readStatusId) {
        if (!readStatusRepository.existsById(readStatusId)) {
            throw new NoSuchElementException("ReadStatus with id " + readStatusId + " not found");
        }
        
        this.readStatusRepository.deleteById(readStatusId);
    }
}
