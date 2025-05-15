package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.userstatus.CreateUserStatusRequest;
import com.sprint.mission.discodeit.dto.userstatus.UpdateUserStatusRequest;
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
    private final UserStatusRepository userStatusRepository;
    private final UserRepository userRepository;


    @Override
    public UserStatus create(CreateUserStatusRequest createUserStatusRequest) {
        if(!userRepository.existsById(createUserStatusRequest.userId())){
            throw new NoSuchElementException("존재하지 않는 유저 id입니다.");
        }
        if(userStatusRepository.findByUserId(createUserStatusRequest.userId()).isPresent()){
            throw new IllegalArgumentException("해당 id의 유저 상태가 이미 있습니다.");
        }

        UserStatus userStatus = new UserStatus(createUserStatusRequest.userId(),createUserStatusRequest.lastActiveAt());
        return userStatusRepository.save(userStatus);
    }

    @Override
    public UserStatus find(UUID userStatusId) {
        return userStatusRepository.findById(userStatusId)
                .orElseThrow(() -> new NoSuchElementException("UserStatus with id " + userStatusId + " not found"));
    }

    public UserStatus findByUserId(UUID userId) {
        return userStatusRepository.findByUserId(userId)
                .orElseThrow(() -> new NoSuchElementException("UserStatus with userId " + userId + " not found"));
    }

    @Override
    public List<UserStatus> findAll() {
        return userStatusRepository.findAll()
                .stream()
                .toList();
    }

    @Override
    public UserStatus update(UUID userStatusId, UpdateUserStatusRequest updateUserStatusRequest) {
        UserStatus userStatus = userStatusRepository.findByUserId(userStatusId)
                .orElseThrow(() -> new NoSuchElementException("UserStatus with userStatusId " + userStatusId + " not found"));
        userStatus.update(updateUserStatusRequest.newLastActiveAt());
        return userStatusRepository.save(userStatus);
    }

    @Override
    public UserStatus updateByUserId(UUID userId, UpdateUserStatusRequest updateUserStatusRequest) {
        UserStatus userStatus = userStatusRepository.findByUserId(userId)
                .orElseThrow(() -> new NoSuchElementException("UserStatus with userId " + userId + " not found"));
        userStatus.update(updateUserStatusRequest.newLastActiveAt());
        return userStatusRepository.save(userStatus);
    }

    @Override
    public void delete(UUID userStatusId) {
        if(!userStatusRepository.existsById(userStatusId)){
            throw new NoSuchElementException("UserStatus with id " + userStatusId + " not found");
        }
        userStatusRepository.deleteById(userStatusId);
        System.out.println("delete userStatus : " + userStatusId + " success.");
    }
}
