package com.sprint.mission.discodeit.service.basic;


import com.sprint.mission.discodeit.dto.readstatus.CreateReadStatusRequest;
import com.sprint.mission.discodeit.dto.readstatus.UpdateReadStatusRequest;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ReaduStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;


@RequiredArgsConstructor
@Service
public class BasicReadStatusService implements ReaduStatusService {

    private final UserRepository userRepository;
    private final ReadStatusRepository readStatusRepository;
    private final ChannelRepository channelRepository;


    @Override
    public ReadStatus create(CreateReadStatusRequest createReadStatusRequest) {
        UUID userId = createReadStatusRequest.userId();
        UUID channelId = createReadStatusRequest.channelId();

        if(!userRepository.existsById(userId)){
            throw new NoSuchElementException("해당 유저 id는 존재하지 않습니다");
        }
        if(!channelRepository.existsById(channelId)){
            throw new NoSuchElementException("해당 채널 id는 존재하지 않습니다.");
        }
        if(readStatusRepository.findAllByUserId(userId).stream()
                .anyMatch(readStatus -> readStatus.getChannelId().equals(channelId))){
            throw new IllegalArgumentException("해당 user와 channel에는 이미 ReadStatus가 존재합니다.");
        }


        ReadStatus readStatus = new ReadStatus(userId,channelId,createReadStatusRequest.lastReadAt());
        return readStatusRepository.save(readStatus);

    }


    @Override
    public ReadStatus find(UUID readStatusId) {
        return readStatusRepository.findById(readStatusId)
                .orElseThrow(() -> new NoSuchElementException("ReadStatus with id " + readStatusId + " not found"));
    }

    @Override
    public List<ReadStatus> findAll() {
        return readStatusRepository.findAll().stream().toList();
    }

    @Override
    public List<ReadStatus> findAllByUserId(UUID userId) {
        return readStatusRepository.findAllByUserId(userId)
                .stream()
                .toList();
    }

    @Override
    public ReadStatus update(UUID readStatusId, UpdateReadStatusRequest updateReadStatusRequest) {
        Instant newLastReadAt = updateReadStatusRequest.newLastReadAt();
        ReadStatus readStatus = readStatusRepository.findById(readStatusId)
                .orElseThrow(() -> new NoSuchElementException("ReadStatus with id " + readStatusId + " not found"));
        readStatus.update(newLastReadAt);
        return readStatusRepository.save(readStatus);
    }

    @Override
    public void delete(UUID readStatusId) {
        if(!readStatusRepository.existsById(readStatusId)){
            throw new NoSuchElementException("ReadStatus with id " + readStatusId + " not found");
        }
        readStatusRepository.deleteById(readStatusId);
        System.out.println("delete readStatus : " + readStatusId + " success.");
    }
}
