package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.ReadStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class BasicReadStatusService implements ReadStatusService {

    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;
    private final ReadStatusRepository readStatusRepository;

    // 생성
    @Override
    public ReadStatus createReadStatus(ReadStatusCreateRequest request) {

        // 유저 존재하지 않으면 예외 발생
        Optional<User> findUser = userRepository.findUserById(request.userId());
        if (findUser.isEmpty()){
            throw new IllegalArgumentException("존재하지 않는 유저입니다.");
        }

        // 채널 존재하지 않으면 예외 발생
        Channel channel = channelRepository.findChannelUsingId(request.channelId())
                .orElseThrow(() -> new NoSuchElementException("MessageService: 채널이 존재하지 않습니다."));

        if (readStatusRepository.findUserReadStatus(request.userId()).stream()
                .anyMatch(readStatus -> readStatus.getChannelId().equals(request.channelId()))) {
            throw new IllegalArgumentException("이미 존재하는 ReadStatus 입니다. ");
        }

        Instant lastReadAt = request.lastReadAt();
        // 객체 생성
        ReadStatus readStatus = new ReadStatus(request.userId(), request.channelId(), request.lastReadAt());

        // 저장
        return readStatusRepository.saveReadStatus(readStatus);
    }

    // id로 readStatus 조회
    @Override
    public Optional<ReadStatus> findReadStatusById(UUID id) {
        Optional<ReadStatus> result = readStatusRepository.findReadStatusById(id);

        if(result.isEmpty()){
            throw new IllegalStateException("해당하는 ReadStatus가 없습니다.");
        }
        System.out.println("ReadStatus 조회 성공");
        return result;
    }

    // user id로 ReadStatus 조회
    @Override
    public List<ReadStatus> findReadStatusByUserId(UUID userId) {
        List<ReadStatus> result = readStatusRepository.findUserReadStatus(userId);

        if(result == null){
            throw new IllegalStateException("해당하는 유저의 ReadStatus가 없습니다.");
        }
        System.out.println("유저의 ReadStatus 조회 성공");
        return result;
    }

    // ReadStatus 업데이트
    @Override
    public ReadStatus updateReadStatus(ReadStatusUpdateRequest request) {
        // id로 ReadStatus 조회
        Optional<ReadStatus> result = readStatusRepository.findReadStatusById(request.id());

        if(result.isEmpty()){
            throw new IllegalArgumentException("해당하는 ReadStatus가 없습니다.");
        }

        ReadStatus readStatus = result.get();

        readStatus.updateLastReadAt(request.updatedAt());

        readStatusRepository.updateReadStatus(readStatus);

        return readStatus;
    }

    // 삭제
    @Override
    public void deleteReadStatus(UUID id) {
        if(findReadStatusById(id).isEmpty()){
            throw new NoSuchElementException("해당하는 ReadStatus가 없습니다. ");
        }
        readStatusRepository.deleteReadStatusById(id);
    }
}
