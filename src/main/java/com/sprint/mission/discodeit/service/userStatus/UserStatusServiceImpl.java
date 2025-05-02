package com.sprint.mission.discodeit.service.userStatus;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.entity.dto.userStatus.UserStatusCreateRequestDto;
import com.sprint.mission.discodeit.entity.dto.userStatus.UserStatusUpdateRequestDto;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
    public UserStatus createUserStatus(UserStatusCreateRequestDto userStatusCreateRequestDto) {
        Optional<User> user = userRepository.getUser(userStatusCreateRequestDto.getUserId());

        if (user.isEmpty()) {
            throw new NoSuchElementException("존재하지 않는 유저입니다.");
        }

        return userStatusRepository.createUserStatus(userStatusCreateRequestDto);
    }

    @Override
    public UserStatus findStatusById(UUID statusId) {
        return userStatusRepository.findStatusById(statusId);
    }

    @Override
    public List<UserStatus> findAllStatus() {
        return userStatusRepository.findAllStatus();
    }

    @Override
    public boolean updateUserStatus(UserStatusUpdateRequestDto userStatusUpdateRequestDto) {
        return userStatusRepository.updateUserStatus(userStatusUpdateRequestDto);
    }

    @Override
    public boolean updateByUserId(UUID userId) {
        Optional<User> user = userRepository.getUser(userId);
        if (user.isEmpty()) {
            throw new NoSuchElementException("존재하지 않는 유저입니다.");
        }

        UserStatus userStatus = userStatusRepository.updateByUserId(userId);
        user.get().setOnline(userStatus.isOnline(userStatus.getLastActivityAt()));

        return true;
    }
}
