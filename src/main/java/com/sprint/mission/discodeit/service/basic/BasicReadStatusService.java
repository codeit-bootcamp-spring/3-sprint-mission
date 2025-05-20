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
    ReadStatusRepository readStatusRepository;
    UserRepository userRepository;
    ChannelRepository channelRepository;


    @Override
    public ReadStatus create(ReadStatusCreateRequest request) {
        UUID userId = request.userId();
        UUID channelId = request.channelId();
        Instant lastReadAt = request.lastReadAt();

        if(userRepository.findById(userId) == null){
            throw new NoSuchElementException("생성 실패 : 존재하지 않는 userId입니다. ");
        }
        if(channelRepository.findById(channelId) == null){
            throw new NoSuchElementException("생성 실패 : 존재하지 않는 channelId입니다. ");
        }
        ReadStatus readStatus = new ReadStatus(userId,channelId,lastReadAt);
        return readStatusRepository.save(readStatus);
    }

    @Override
    public ReadStatus find(UUID readStatusId) {
        return readStatusRepository.findById(readStatusId);
    }

    @Override
    public List<ReadStatus> findAllByUserId(UUID userId) {
        return readStatusRepository.findAllByUserId(userId).stream().toList();
    }

    @Override
    public ReadStatus update(UUID readStatusId, ReadStatusUpdateRequest request) {
        Instant newLastReadAt = request.newlastReadAt();
        ReadStatus readStatus = readStatusRepository.findById(readStatusId);
        if(readStatus == null){
            throw new NoSuchElementException("수정 실패 : 존재하지 않는 readStatusId입니다.");
        }
        readStatus.update(newLastReadAt);
        return readStatusRepository.save(readStatus);
    }

    @Override
    public void delete(UUID readStatusId) {
        if(!readStatusRepository.existsById(readStatusId)){
            throw new NoSuchElementException("삭제 실패 : 존재하지 않는 readStatusId입니다.");
        }
        readStatusRepository.deleteById(readStatusId);
    }
}
