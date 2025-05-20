package com.sprint.mission.discodeit.service.userStatus;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.entity.dto.userStatus.UserStatusCreateRequestDto;
import com.sprint.mission.discodeit.entity.dto.userStatus.UserStatusUpdateRequestDto;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserStatusServiceImpl implements UserStatusService {

    private final UserRepository userRepository;
    private final UserStatusRepository userStatusRepository;


    @Override
    public UserStatus createUserStatus(UserStatusCreateRequestDto dto) {
        validateCreateRequest(dto);

        UserStatus userStatus = new UserStatus(dto.getUserId(), dto.getNowTime());
        return userStatusRepository.createUserStatus(userStatus);
    }

    @Override
    public UserStatus findStatusById(UUID statusId) {
        return userStatusRepository.findStatusById(statusId)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 상태입니다."));
    }

    @Override
    public List<UserStatus> findAllStatus() {
        return userStatusRepository.findAllStatus();
    }

    @Override
    public boolean updateUserStatus(UserStatusUpdateRequestDto dto) {
        Optional<UserStatus> userStatusOptional = userStatusRepository.findStatusById(dto.getUserStatusId());
        if (userStatusOptional.isEmpty()) {
            throw new NoSuchElementException("존재하지 않는 상태입니다.");
        }

        UserStatus userStatus = userStatusOptional.get();
        userStatus.setLastActivityAt(dto.getNowTime());

        return userStatusRepository.updateUserStatus(userStatus);
    }


    @Override
    public boolean updateByUserId(UUID userId) {
        Optional<User> user = userRepository.getUser(userId);
        if (user.isEmpty()) {
            throw new NoSuchElementException("존재하지 않는 유저입니다.");
        }
        UserStatus userStatus = userStatusRepository.findByUserId(userId).orElseThrow(() -> new NoSuchElementException("존재하지 않는 데이터 입니다."));
        userStatus.update(Instant.now());
        return userStatusRepository.updateUserStatus(userStatus);
    }

    @Override
    public boolean deleteUserStatus(UUID statusId) {
        if (!userStatusRepository.findStatusById(statusId).isPresent()) {
            throw new NoSuchElementException("존재하지 않는 데이터 입니다.");
        }
        return userStatusRepository.deleteUserStatus(statusId);
    }

    private void validateCreateRequest(UserStatusCreateRequestDto dto) {
        if (dto == null) {
            throw new IllegalArgumentException("요청 데이터가 없습니다.");
        }
        if (!userRepository.getUser(dto.getUserId()).isPresent()) {
            throw new NoSuchElementException("존재하지 않는 사용자입니다.");
        }
        if (userStatusRepository.findByUserId(dto.getUserId()).isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 데이터 입니다.");
        }
    }
}
