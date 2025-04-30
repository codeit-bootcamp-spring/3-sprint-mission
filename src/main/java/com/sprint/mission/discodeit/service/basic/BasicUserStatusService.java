package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.UserStatusCreateRequest;
import com.sprint.mission.discodeit.dto.UserStatusResponse;
import com.sprint.mission.discodeit.dto.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.entity.UserStatusType;
import com.sprint.mission.discodeit.exception.UserStatusAlreadyExistsException;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicUserStatusService implements UserStatusService {
    private final UserStatusRepository userStatusRepository;
    private final UserRepository userRepository;

    @Override
    public UserStatusResponse create(UserStatusCreateRequest createRequest) {
        //1.  `User`가 존재하지 않으면 예외 발생
        User user = this.userRepository
                .findById(createRequest.userId())
                .orElseThrow(() -> new NoSuchElementException("User with id " + createRequest.userId() + " not found"));

        //2.  같은 `User`와 관련된 객체가 이미 존재하면 예외를 발생
        this.userStatusRepository.findById(createRequest.userId()).ifPresent((userStatus) -> {
            throw new UserStatusAlreadyExistsException(userStatus);
        });

        // 3. ReadStatus 생성
        UserStatus userStatus = new UserStatus(createRequest.userId());

        //4. DB저장
        this.userStatusRepository.save(userStatus);

        return new UserStatusResponse(userStatus);
    }

    @Override
    public UserStatusResponse find(UUID userStatusId) {
        UserStatus userStatus = this.userStatusRepository
                .findById(userStatusId)
                .orElseThrow(() -> new NoSuchElementException("userStatus with id " + userStatusId + " not found"));

        return new UserStatusResponse(userStatus);
    }

    @Override
    public List<UserStatusResponse> findAll() {
        List<UserStatusResponse> userStatuses = this.userStatusRepository.findAll().stream().map(UserStatusResponse::new).toList();

        return userStatuses;
    }

    @Override
    public UserStatusResponse update(UserStatusUpdateRequest updateRequest) {
        UserStatus userStatus = this.userStatusRepository
                .findById(updateRequest.userStatusId())
                .orElseThrow(() -> new NoSuchElementException("userStatus with id " + updateRequest.userStatusId() + " not found"));

        userStatus.update(updateRequest.status());

        /* 업데이트 후 다시 DB 저장 */
        this.userStatusRepository.save(userStatus);

        UserStatus updatedUserStatus = this.userStatusRepository
                .findById(updateRequest.userStatusId())
                .orElseThrow(() -> new NoSuchElementException("userStatus with id " + updateRequest.userStatusId() + " not found"));

        return new UserStatusResponse(updatedUserStatus);
    }

    @Override
    public UserStatusResponse updateByUserId(UUID userId, UserStatusType status) {
        UserStatus userStatus = this.userStatusRepository
                .findByUserId(userId)
                .orElseThrow(() -> new NoSuchElementException("userStatus with userId " + userId + " not found"));

        userStatus.update(status);

        /* 업데이트 후 다시 DB 저장 */
        this.userStatusRepository.save(userStatus);

        UserStatus updatedUserStatus = this.userStatusRepository
                .findByUserId(userId)
                .orElseThrow(() -> new NoSuchElementException("userStatus with userId " + userId + " not found"));

        return new UserStatusResponse(updatedUserStatus);
    }

    @Override
    public void delete(UUID userStatusId) {
        UserStatus userStatus = this.userStatusRepository
                .findById(userStatusId)
                .orElseThrow(() -> new NoSuchElementException("userStatus with id " + userStatusId + " not found"));

        this.userStatusRepository.deleteById(userStatusId);
    }
}
