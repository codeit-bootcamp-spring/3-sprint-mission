package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.UserStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class BasicUserStatusService implements UserStatusService {
    private final UserRepository userRepository;
    private final UserStatusRepository userStatusRepository;


    @Override
    public UserStatus create(UserStatusCreateRequest request) {
        UUID userId = request.userId();
        if(userRepository.findById(userId) == null){
            throw new NoSuchElementException("생성 실패 : 존재하지 않는 유저입니다.");
        }
        if(userStatusRepository.existsById(userId)){
            throw new IllegalArgumentException("생성 실패 : 이미 생성된 UserStatus입니다.");
        }
        Instant lastActiveat = request.lastActiveAt();
        UserStatus userStatus = new UserStatus(userId,lastActiveat);
        return userStatusRepository.save(userStatus);
    }

    @Override
    public UserStatus find(UUID userStatusId) {
        return userStatusRepository.findById(userStatusId)
                .orElseThrow(() -> new NoSuchElementException("조회 실패 : 존재하지 않는 userStatusId입니다."));
    }

    @Override
    public List<UserStatus> findAll() {
        return userStatusRepository.findAll().stream().toList();
    }

    @Override
    public UserStatus update(UUID userStatusId, UserStatusUpdateRequest request) {
        Instant newLastActiveAt = request.newlastActiveAt();

        UserStatus userStatus = userStatusRepository.findById(userStatusId).orElseThrow(()->new NoSuchElementException("존재하지 않는 userStatusId입니다."));
        userStatus.update(newLastActiveAt);

        return userStatusRepository.save(userStatus);
    }

    @Override
    public UserStatus updateByUserId(UUID userId, UserStatusUpdateRequest request) {
        Instant newLastActiveAt = request.newlastActiveAt();

        UserStatus userStatus = userStatusRepository.findByUserId(userId).orElseThrow(()->new NoSuchElementException("존재하지 않는 userStatusId입니다."));
        userStatus.update(newLastActiveAt);

        return userStatusRepository.save(userStatus);
    }

    @Override
    public void delete(UUID userStatusId) {
        if (!userStatusRepository.existsById(userStatusId)) {
            throw new NoSuchElementException("UserStatus with id " + userStatusId + " not found");
        }
        userStatusRepository.deleteById(userStatusId);

    }
}
