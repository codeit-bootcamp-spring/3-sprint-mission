package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.ReadStatusResponse;
import com.sprint.mission.discodeit.dto.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
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
    //TODO : 레포 생성하고 autowired 해줘야함
//private final ReadStatusRepository readStatusRepository;
    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;


    @Override
    public ReadStatusResponse create(ReadStatusCreateRequest createRequest) {

        // 1. `Channel`이나`User`가 존재하지 않으면 예외 발생
        User user = this.userRepository
                .findById(createRequest.userId())
                .orElseThrow(() -> new NoSuchElementException("User with id " + createRequest.userId() + " not found"));

        Channel channel = this.channelRepository
                .findById(createRequest.channelId())
                .orElseThrow(() -> new NoSuchElementException("Channel with id " + createRequest.channelId() + " not found"));


        //2. 같은`Channel`과`User`와 관련된 객체가 이미 존재하면 예외를 발생
        // TODO : 레포 생성하고 실제 값 넣어주기
//        Optional<ReadStatus> readStatusNullable = this.readStatusRepository.findById(createRequest.userId(), createRequest.channelId());
//
//        readStatusNullable.ifPresent((readStatus) -> {
//            throw new ReadStatusAlreadyExistsException(readStatus);
//        });

        // 3. ReadStatus 생성
        ReadStatus readStatus = new ReadStatus(createRequest.userId(), createRequest.channelId());
//        //4. DB저장
//        this.readStatusRepository.save(readStatus);

        return new ReadStatusResponse(readStatus);
    }

    @Override
    public ReadStatusResponse find(UUID readStatusId) {
//        // TODO : 레포 생성하고 실제 값 넣어주기
//        ReadStatus readStatus = this.readStatusRepository
//                .findById(readStatusId)
//                .orElseThrow(() -> new NoSuchElementException("readStatus with id " + readStatusId + " not found"));
//
//        return new ReadStatusResponse(readStatus);

        return null;
    }

    @Override
    public List<ReadStatusResponse> findAllByUserId(UUID userId) {
        //        // TODO : 레포 생성하고 살리기
//        List<ReadStatusResponse> readStatuses = this.readStatusRepository.findAll()
//                .stream().filter(readStatus -> readStatus.getUserId().equal(userId))
//                .map(readStatus -> {
//                    return new ReadStatusResponse(readStatus);
//                })
//                .toList();
        return null;
    }

    @Override
    public ReadStatusResponse update(ReadStatusUpdateRequest updateRequest) {
        // FIXME : 나중에 userStatusRepository 구현체 만들고 넣어줘야함.

//        ReadStatus readStatus = this.readStatusRepository
//                .findById(updateRequest.readStatusId())
//                .orElseThrow(() -> new NoSuchElementException("readStatus with id " + updateRequest.readStatusId() + " not found"));

//        readStatus.update(updateRequest.isRead());

        //QUESTION: 이렇게 하면 파일레포일때 파일 업데이트가되나??? -> 안됨. 수정해야함
//        return new ReadStatusResponse(readStatus);
        return null;
    }

    @Override
    public void delete(UUID readStatusId) {
        // FIXME : 나중에 readStatusRepository 구현체 만들고 넣어줘야함.
//        ReadStatus readStatus = this.readStatusRepository
//                .findById(readStatusId)
//                .orElseThrow(() -> new NoSuchElementException("readStatus with id " + readStatusId + " not found"));
//
//
//        this.readStatusRepository.deleteById(readStatusId);

    }
}
