package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.Dto.userStatus.UserStatusCreateRequest;
import com.sprint.mission.discodeit.Dto.userStatus.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/**
 * packageName    : com.sprint.mission.discodeit.service.basic
 * fileName       : BasicUserStatusService
 * author         : doungukkim
 * date           : 2025. 4. 28.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2025. 4. 28.        doungukkim       최초 생성
 */
@Service("basicUserStatusService")
@RequiredArgsConstructor
public class BasicUserStatusService implements UserStatusService {
    private final UserStatusRepository userStatusRepository;
    private final UserRepository userRepository;
    //    create
//      [ ] DTO를 활용해 파라미터를 그룹화합니다.
//      [ ] 관련된 User가 존재하지 않으면 예외를 발생시킵니다.
//      [ ] 같은 User와 관련된 객체가 이미 존재하면 예외를 발생시킵니다.
    @Override
    public UserStatus create(UserStatusCreateRequest request) {
        UUID userId = Optional.ofNullable(request.getUserId()).orElseThrow(() -> new IllegalArgumentException("no request exist"));

        userRepository.findUserById(userId); // 없으면 throw error 날림

        if (userStatusRepository.findUserStatusByUserId(userId) != null) { // userStatus 이미 있으면 예외 발생
            throw new RuntimeException("user status already exist");
        }

        return userStatusRepository.createUserStatus(request.getUserId());
        // response(UserStatus, isOnline) <= response 추가시 리팩토링
    }

    //    find
//      [ ] id로 조회합니다.
    @Override
    public UserStatus find(UUID userStatusId) {
        Objects.requireNonNull(userStatusId, "no userStatusId param");
        return userStatusRepository.findById(userStatusId);
    }

    //    findAll
//      [ ] 모든 객체를 조회합니다.
    @Override
    public List<UserStatus> findAll() {
        return userStatusRepository.findAllUserStatus();
    }

    //    update
//      [ ] DTO를 활용해 파라미터를 그룹화합니다.
//          수정 대상 객체의 id 파라미터, 수정할 값(시간) 파라미터
    @Override
    public void update(UserStatusUpdateRequest request) {
        UUID userStatusId = Optional.ofNullable(request.getUserStatusId()).orElseThrow(() -> new IllegalArgumentException("no param"));
        Instant newTime = Optional.ofNullable(request.getNewTime()).orElseThrow(() -> new IllegalArgumentException("no param"));

        userStatusRepository.update(userStatusId, newTime);
    }


    //    updateByUserId
//      [ ] userId 로 특정 User의 객체를 업데이트합니다.
    @Override
    public void updateByUserId(UUID userId, Instant newTime) {
        Objects.requireNonNull(userId, "no userId in param");
        Objects.requireNonNull(newTime, "no userId in param");

        userStatusRepository.updateByUserId(userId, newTime);
    }

    //    delete
//      [ ] id로 삭제합니다.
    @Override
    public void delete(UUID userStatusId) {
        Objects.requireNonNull(userStatusId, "no userStatusId");
        userStatusRepository.deleteById(userStatusId);

    }
}
