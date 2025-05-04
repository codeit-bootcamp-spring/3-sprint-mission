package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.entity.dto.CreateUserStatusRequest;
import com.sprint.mission.discodeit.entity.dto.UpdateUserStatusRequest;
import com.sprint.mission.discodeit.entity.exception.DuplicateUserStatusException;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

public class BasicUserStatusService {

    UserRepository userRepository;
    UserStatusRepository userStatusRepository;

    UserStatus create(CreateUserStatusRequest request) {

        // 1. User 존재 확인
        if (!userRepository.existsById(request.user().getId())) {
            throw new NoSuchElementException("User not found");
        }

        // 2. User의 UserStatus 존재 확인
        if (!userStatusRepository.existsByUserId(request.user().getId())) {
            throw new DuplicateUserStatusException("UserStatus with userId already exists");
        }

        // 3. UserStatus 생성 및 저장
        UserStatus userStatus = new UserStatus(request.user());

        return userStatusRepository.save(userStatus);
    }

    public Optional<UserStatus> find(UUID id) {
        if (!userRepository.existsById(id)) {
            throw new NoSuchElementException("User not found");
        }

        if (!userStatusRepository.existsByUserId(id)) {
            throw new DuplicateUserStatusException("UserStatus with userId not found");
        }

        return userStatusRepository.find(id);
    }

    List<UserStatus> findAll() {

        return userStatusRepository.findAll();
    }

    Optional<UserStatus> update(UpdateUserStatusRequest request) {

        if (!userRepository.existsById(request.user().getId())) {
            throw new NoSuchElementException("User not found");
        }

        if (!userStatusRepository.existsByUserId(request.user().getId())) {
            throw new DuplicateUserStatusException("UserStatus with userId not found");
        }

        // 1. 수정할 UserStatus 찾기
        Optional<UserStatus> userStatus = userStatusRepository.find(request.userStatus().getId());

        // 2. 수정
        userStatus.get().update();

        // 3. 저장
        userStatusRepository.save(userStatus.get());

        return userStatus;
    }

    Optional<UserStatus> updateByUserId(UpdateUserStatusRequest request) {

        if (!userRepository.existsById(request.user().getId())) {
            throw new NoSuchElementException("User not found");
        }

        if (!userStatusRepository.existsByUserId(request.user().getId())) {
            throw new DuplicateUserStatusException("UserStatus with userId not found");
        }

        // 1. 수정할 UserStatus 찾기
        Optional<UserStatus> optionalUserStatus = userStatusRepository.find(request.user().getId());

        // 2. 수정
        optionalUserStatus.get().update();

        // 3. 저장
        userStatusRepository.save(optionalUserStatus.get());

        return optionalUserStatus;
    }

    void delete(UUID id) {

        if (!userRepository.existsById(id)) {
            throw new NoSuchElementException("User not found");
        }

        if (!userStatusRepository.existsByUserId(id)) {
            throw new DuplicateUserStatusException("UserStatus with userId not found");
        }

        userStatusRepository.delete(id);
    }
}