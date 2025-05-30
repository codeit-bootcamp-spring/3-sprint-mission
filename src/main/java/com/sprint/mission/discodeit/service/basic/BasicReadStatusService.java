package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.Dto.channel.ChannelCreateResponse;
import com.sprint.mission.discodeit.Dto.readStatus.*;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.jpa.JpaChannelRepository;
import com.sprint.mission.discodeit.repository.jpa.JpaReadStatusRepository;
import com.sprint.mission.discodeit.repository.jpa.JpaUserRepository;
import com.sprint.mission.discodeit.service.ReadStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * packageName    : com.sprint.mission.discodeit.service.basic
 * fileName       : BasicReadStatusService
 * author         : doungukkim
 * date           : 2025. 4. 28.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2025. 4. 28.        doungukkim       최초 생성
 */
@Service("basicReadStatusService")
@RequiredArgsConstructor
@Transactional
public class BasicReadStatusService implements ReadStatusService {

    private final JpaReadStatusRepository readStatusRepository;
    private final JpaUserRepository userRepository;
    private final JpaChannelRepository channelRepository;


    @Override
    public List<FindReadStatusesResponse> findAllByUserId(UUID userId) {
        List<ReadStatus> readStatusList = Optional.ofNullable(readStatusRepository.findAllByUserId(userId))
                .orElseThrow(() -> new IllegalStateException("userId로 찾을 수 없음: BasicReadStatusService.findAllByUserId"));


        List<FindReadStatusesResponse> responses = new ArrayList<>();
        for (ReadStatus readStatus : readStatusList) {
            responses.add(new FindReadStatusesResponse(
                    readStatus.getId(),
                    readStatus.getCreatedAt(),
                    readStatus.getUpdatedAt(),
                    readStatus.getUser().getId(),
                    readStatus.getChannel().getId(),
                    readStatus.getLastReadAt()
            ));
        }
        return responses;
    }

    @Override
    public ReadStatusCreateResponse create(ReadStatusCreateRequest request) {
        UUID userId = request.userId();
        UUID channelId = request.channelId();
//        // user 검증
//        if (userRepository.findUserById(userId) == null) {
//            throw new NoSuchElementException("user with id " + userId + " not found");
//        }
//        // channel 검증
//        if (channelRepository.findChannelById(channelId) == null) {
//            throw new NoSuchElementException("channel with id " + channelId + " not found");
//        }
        User user = userRepository.findById(userId).orElseThrow(() -> new NoSuchElementException("user with id " + userId + " not found"));
        Channel channel = channelRepository.findById(channelId).orElseThrow(() -> new NoSuchElementException("channel with id " + channelId + " not found"));

        // ReadStatus 중복 방지
        if (readStatusRepository.existsByUserAndChannel(user, channel)) {
            throw new IllegalArgumentException("readStatus with userId " + request.userId() + " and channelId " + request.channelId() + " already exists");
        }

//        List<ReadStatus> readStatusesByChannelId = readStatusRepository.findReadStatusesByChannelId(request.channelId());
//        for (ReadStatus readStatus : readStatusesByChannelId) {
//            if (readStatus.getUserId().equals(request.userId())) {
//                throw new IllegalArgumentException("readStatus with userId " + request.userId() + " and channelId " + request.channelId() + " already exists");
//            }
//        }

//        ReadStatus readStatus = readStatusRepository.createByUserId(request.userId(), request.channelId(), request.lastReadAt());
        ReadStatus readStatus = ReadStatus.builder()
                .user(user)
                .channel(channel)
                .lastReadAt(request.lastReadAt())
                .build();
        readStatusRepository.save(readStatus);

        ReadStatusCreateResponse readStatusCreateResponse = new ReadStatusCreateResponse(
                readStatus.getId(),
                readStatus.getCreatedAt(),
                readStatus.getUpdatedAt(),
                readStatus.getUser().getId(),
                readStatus.getChannel().getId(),
                readStatus.getLastReadAt()
        );
        return readStatusCreateResponse;
    }


    @Override
    public UpdateReadStatusResponse update(UUID readStatusId, ReadStatusUpdateRequest request) {
//        readStatusRepository.updateUpdatedTime(readStatusId, request.newLastReadAt());

        ReadStatus readStatus = readStatusRepository.findById(readStatusId).orElseThrow(() -> new NoSuchElementException("readStatus with id " + readStatusId + " not found"));
        readStatus.setLastReadAt(request.newLastReadAt());
//        if (readStatus == null) {
//            throw new NoSuchElementException("readStatus with id " + readStatusId + " not found");
//        }
//
        UpdateReadStatusResponse response = new UpdateReadStatusResponse(
                readStatus.getId(),
                readStatus.getCreatedAt(),
                readStatus.getUpdatedAt(),
                readStatus.getUser().getId(),
                readStatus.getChannel().getId(),
                readStatus.getLastReadAt()
        );
//
        return response;
//        return null;
    }

    // --------------------------------------------------------------------------------------------------------------

    @Override
    public ReadStatus findById(UUID readStatusId) {
//        return Optional.ofNullable(readStatusRepository.findById(readStatusId)).orElseThrow(() -> new IllegalStateException("no read status to find"));
        return null;
    }

    @Override
    public void delete(UUID readStatusId) {
//        Objects.requireNonNull(readStatusId, "no readStatusId to delete");
//        readStatusRepository.deleteReadStatusById(readStatusId);
    }
}
