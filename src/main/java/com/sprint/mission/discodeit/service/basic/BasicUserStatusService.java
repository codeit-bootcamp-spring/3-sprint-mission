package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.UserStatusCreateRequest;
import com.sprint.mission.discodeit.dto.UserStatusResponse;
import com.sprint.mission.discodeit.dto.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.UserStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicUserStatusService implements UserStatusService {
    //TODO : 레포 생성하고 autowired 해줘야함
//    private final UserStatusRepository userStatusRepository;
    private final UserRepository userRepository;

    @Override
    public UserStatusResponse create(UserStatusCreateRequest createRequest) {
        // TODO : 레포 생성하고 실제 값 넣어주기
        //1.  `User`가 존재하지 않으면 예외 발생
        User user = this.userRepository
                .findById(createRequest.userId())
                .orElseThrow(() -> new NoSuchElementException("User with id " + createRequest.userId() + " not found"));

        //2.  같은 `User`와 관련된 객체가 이미 존재하면 예외를 발생
        // TODO : 레포 생성하고 실제 값 넣어주기
//        Optional<UserStatus> userStatusNullable = this.userStatusRepository.findById(createRequest.userId());
//        userStatusNullable.ifPresent((userStatus) -> {
//            throw new UserStatusAlreadyExistsException(userStatus);
//        });

        // 3. ReadStatus 생성
        UserStatus userStatus = new UserStatus(createRequest.userId());
//        //4. DB저장
//        this.userStatusRepository.save(userStatus);

        return new UserStatusResponse(userStatus);
    }

    @Override
    public UserStatusResponse find(UUID userStatusId) {
        // TODO : 레포 생성하고 실제 값 넣어주기
//        UserStatus userStatus = this.userStatusRepository
//                .findById(userStatusId)
//                .orElseThrow(() -> new NoSuchElementException("userStatus with id " + userStatusId + " not found"));
//
//        return new UserStatusResponse(userStatus);

        return null;
    }

    @Override
    public List<UserStatusResponse> findAll() {
        // TODO : 레포 생성하고 실제 값 넣어주기
//        List<UserStatusResponse> userStatuses = this.userStatusRepository.findAll().toList();

        return List.of();
    }

    @Override
    public UserStatusResponse update(UserStatusUpdateRequest updateRequest) {
        // FIXME : 나중에 userStatusRepository 구현체 만들고 넣어줘야함.
//        UserStatus userStatus = this.userStatusRepository
//                .findById(updateRequest.userStatusId())
//                .orElseThrow(() -> new NoSuchElementException("userStatus with id " + updateRequest.userStatusId() + " not found"));

//        userStatus.update(updateRequest.status());

        //QUESTION: 이렇게 하면 파일레포일때 파일 업데이트가되나??? -> 안됨. 수정해야함
//        return new UserStatusResponse(userStatus);
        return null;
    }

    @Override
    public UserStatusResponse updateByUserId(UUID userId) {
        // FIXME : 나중에 userStatusRepository 구현체 만들고 넣어줘야함.
//        UserStatus userStatus = this.userStatusRepository
//                .findByUserId(userId)
//                .orElseThrow(() -> new NoSuchElementException("userStatus with userId " + userId + " not found"));

//        userStatus.update(updateRequest.status());

        //QUESTION: 이렇게 하면 파일레포일때 파일 업데이트가되나??? -> 안됨. 수정해야함
//        return new UserStatusResponse(userStatus);
        return null;
    }

    @Override
    public void delete(UUID userStatusId) {
        // FIXME : 나중에 userStatusRepository 구현체 만들고 넣어줘야함.

//        UserStatus userStatus = this.userStatusRepository
//                .findById(updateRequest.userStatusId())
//                .orElseThrow(() -> new NoSuchElementException("userStatus with id " + updateRequest.userStatusId() + " not found"));

//
//        this.userStatusRepository.deleteById(userStatusId);

    }
}
