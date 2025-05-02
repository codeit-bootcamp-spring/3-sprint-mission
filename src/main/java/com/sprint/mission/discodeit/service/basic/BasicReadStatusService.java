package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
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
        User user = this.userRepository
                .findById(createRequest.userId())
                .orElseThrow(() -> new NoSuchElementException("User with id " + createRequest.userId() + " not found"));

        Channel channel = this.channelRepository
                .findById(createRequest.channelId())
                .orElseThrow(() -> new NoSuchElementException("Channel with id " + createRequest.channelId() + " not found"));


        //2. 같은`Channel`과`User`와 관련된 객체가 이미 존재하면 예외를 발생
        boolean isAlreadyExist = this.readStatusRepository.findAllByChannelId(createRequest.channelId()).stream().anyMatch((status) -> status.getUserId().equals(createRequest.userId()));
        if (isAlreadyExist) {
            throw new ReadStatusAlreadyExistsException();
        }

        // 3. ReadStatus 생성
        ReadStatus readStatus = new ReadStatus(createRequest.userId(), createRequest.channelId());
        //4. DB저장
        this.readStatusRepository.save(readStatus);

        return readStatus;
    }

    @Override
    public ReadStatus find(UUID readStatusId) {
        ReadStatus readStatus = this.readStatusRepository
                .findById(readStatusId)
                .orElseThrow(() -> new NoSuchElementException("readStatus with id " + readStatusId + " not found"));

        return readStatus;
    }

    @Override
    public List<ReadStatus> findAllByUserId(UUID userId) {
        List<ReadStatus> readStatuses = this.readStatusRepository.findAll()
                .stream().filter(readStatus -> readStatus.getUserId() == userId)
                .toList();
        return readStatuses;
    }

    @Override
    public ReadStatus update(ReadStatusUpdateRequest updateRequest) {
        ReadStatus readStatus = this.readStatusRepository
                .findById(updateRequest.readStatusId())
                .orElseThrow(() -> new NoSuchElementException("readStatus with id " + updateRequest.readStatusId() + " not found"));

        readStatus.update(updateRequest.isRead());

        /* 업데이트 후 다시 DB 저장 */
        this.readStatusRepository.save(readStatus);

        ReadStatus updateReadStatus = this.readStatusRepository
                .findById(updateRequest.readStatusId())
                .orElseThrow(() -> new NoSuchElementException("readStatus with id " + updateRequest.readStatusId() + " not found"));

        return updateReadStatus;
    }

    @Override
    public void delete(UUID readStatusId) {
        ReadStatus readStatus = this.readStatusRepository
                .findById(readStatusId)
                .orElseThrow(() -> new NoSuchElementException("readStatus with id " + readStatusId + " not found"));

        this.readStatusRepository.deleteById(readStatusId);
    }
}
