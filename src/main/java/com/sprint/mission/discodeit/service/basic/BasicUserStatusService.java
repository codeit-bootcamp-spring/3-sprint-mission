package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.UserStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.OnlineStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import java.util.logging.Logger;

@RequiredArgsConstructor
@Service
public class BasicUserStatusService implements UserStatusService {

    private static final Logger logger = Logger.getLogger(BasicUserStatusService.class.getName()); // 필드로 Logger 선언


    private final UserStatusRepository userStatusRepository;
    private final UserRepository userRepository;

    // 상태 생성
    @Override
    public UserStatus createUserStatus(UserStatusCreateRequest request) {
        if (userRepository.findUserById(request.userId()) == null) {
            logger.info("UserStatusService: " + request.userId()+ "UserStatus 저장에 실패했습니다");
            return null;
        }

        // 이미 UserStatus가 존재하는지 검사
        if (userStatusRepository.findStatus(request.userId()).isPresent()) {
            logger.info("UserStatusService: 이미 UserStatus가 존재합니다.");
            return null;
        }

        // 새 UserStatus 생성
        UserStatus userStatus = new UserStatus(request.userId());

        return userStatusRepository.saveUserStatus(userStatus);
    }

    // 유저의 접속 상태 조회
    @Override
    public UserStatus findUserStatusById(UUID userId) {
        Optional<UserStatus> findResult = userStatusRepository.findStatus(userId);

        if (findResult.isPresent()){
            UserStatus userStatus = findResult.get();
            System.out.println("UserStatusService: 유저 상태 조회 성공");
            return userStatus;
        }
        else{
            System.out.println("UserStatusService: 해당 유저의 상태를 찾을 수 없습니다.");
            return null;
        }
    }

    // 저장된 상태 전체 조회
    @Override
    public List<UserStatus> findAllStatus() {
        return userStatusRepository.findAllStatus().stream()
                .toList();
    }

    // 유저 상태 업데이트 (접속 시간을 기준으로)
    @Override
    public boolean updateUserStatus(UserStatusUpdateRequest request) {
        UUID userId = request.userId();

        Optional<UserStatus> foundStatus = userStatusRepository.findStatus(userId);

        if (foundStatus.isEmpty()) {
            logger.warning("UserStatusService: UserStatus가 존재하지 않습니다: " + userId);
            return false;
        }

        UserStatus status = foundStatus.get();
        Instant now = Instant.now();

        // 도메인의 isOnline() 메서드 사용
        OnlineStatus currentStatus = status.isOnline() ? OnlineStatus.ONLINE : OnlineStatus.OFFLINE;

        // 상태가 다를 때만 업데이트
        if (!status.getStatus().equals(currentStatus)) {
            status.updateStatus(currentStatus);
            status.updateUpdatedAt(Instant.now());
            userStatusRepository.updateUserStatus(status);
            System.out.println("UserStatusService: 유저 상태 변경됨");
            return true;
        }
        return true;
    }

    // 상태 아이디로 삭제
    @Override
    public void deleteUserStatus(UUID statusId) {
        if((userStatusRepository.findById(statusId)).isEmpty()){
            throw new NoSuchElementException("UserStatusService: 해당하는 UserStatus가 없습니다. ");
        }
        userStatusRepository.deleteById(statusId);
    }

    //유저 아이디로 상태 삭제
    @Override
    public void deleteUserStatusByUserId(UUID userId) {
        UserStatus userStatus = userStatusRepository.findStatus(userId)
                .orElseThrow(() -> new NoSuchElementException("UserStatusService: 유저가 존재하지 않습니다."));

        userStatusRepository.deleteUserStatus(userId);
    }
}
