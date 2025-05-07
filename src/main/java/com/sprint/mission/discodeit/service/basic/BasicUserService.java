package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.entity.dto.*;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.Arrays.stream;

@RequiredArgsConstructor
@Service
public class BasicUserService implements UserService {

    private final UserRepository userRepository;
    UserStatusRepository userStatusRepository;
    BinaryContentRepository binaryContentRepository;

    @Override
    public boolean hasDuplicate(String username, String email) {

        // TODO
        /* 1. 파라미터와 비교할 UserRepository의 username과 email 데이터를 불러온다.
        *   1-1. findAll()에 있는 UserDTO(List<User>, List<Optional<UserStatus>>
            1-2. List<User>을 userList에 할당한다.
        *   1-3. username과 email을 추출해서 각각 중복검사 하여 결과를 return 한다.
        * */
        List<User> userList = findAll().userList();

        for (User user : userList) {
            if(user.getUsername().equals(username) && user.getEmail().equals(email)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public User create(CreateUserRequest request, Optional<CreateBinaryContentRequest> binaryContentRequest) {

        /* 중복검사 선행 */
        if (hasDuplicate(request.username(), request.email())) {
            throw new IllegalArgumentException("사용자 이름 또는 이메일 주소가 중복됩니다.");
        }

        // User 객체 생성
        User user = new User(request.username(), request.email(), request.password(), null); // profileId는 어떻게 설정해주냐

        // UserStatus 객체 생성만 하고 상태정보를 가져오진 않음
        UserStatus userStatus = new UserStatus(user);

        // FIXME 선택적으로를 아직 구현 안함
        // BinaryContent 객체 생성 및 저장 후 User 객체의 profileId과 연결
        binaryContentRequest.ifPresent(binaryContent -> {
            BinaryContent newBinaryContent = new BinaryContent(UUID.randomUUID(), binaryContent.fileName(),
                    binaryContent.fileType(), binaryContent.content());

            binaryContentRepository.save(newBinaryContent);

            user.setProfileId(newBinaryContent.getId());
        });

        // 트랜잭션 시작
        try {
            // 데이터베이스에 UserStatus와 BinaryContent 저장
            userStatusRepository.save(userStatus);
//            binaryContentRepository.save(binaryContent);

            return userRepository.save(user);
        } catch (Exception e) {
            throw new RuntimeException("사용자 생성에 실패했습니다.", e);
        }
    }

    @Override
    public UserDTO find(UUID id) {

        Optional<User> user = userRepository.findById(id);

        if (user.isPresent()) {
            UserStatus userStatus = userStatusRepository.findByUserId(id);
            UserDTO userDTO = new UserDTO(user, userStatus);
            return userDTO;
        } else {
            throw new IllegalArgumentException("해당 User의 UserStatus가 존재하지 않습니다.");
        }
    }

    // FIXME : List<UserDTO>를 return하도록 수정
    @Override
    public UserDTO findAll() {
        // 1. User 목록 조회
        List<User> userList = userRepository.findAll();

        // 2. 각 User에 대해 UserStatus 조회 후 Optional 래퍼 클래스로 감싸기
        List<UserStatus> userStatuses = new ArrayList<>();

        // 3. User와 UserStatus 연결
        for (User user : userList) {
            // 3-1. password 제외
            user.update(user.getUsername(), user.getEmail(), null);
            // 3-2. 연결
            UserStatus userStatus = Optional.ofNullable(userStatusRepository.findByUserId(user.getId()))
                    .orElseThrow(() -> new IllegalArgumentException("해당 유저의 UserStatus가 존재하지 않습니다."));

            userStatuses.add(userStatus);
        }

        return new UserDTO(userList, userStatuses);
    }

    @Override
    public User update(UUID userId, UpdateUserRequest request, Optional<CreateBinaryContentRequest> binaryContentRequest) {
        // 1. User를 userId로 찾아오기
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User with id " + userId + " not found"));

        // 2. User 객채의 필드 update
        user.update(request.username(), request.email(), request.password());

        // 3. BinaryContent 업데이트 (profileId가 조ㅓㄴ재하는 경우)
        request.binaryContent().ifPresent(binaryContent -> {
            // 3.1 기존 BinaryContent 삭제 (profileId가 일치하는 BinaryContent 삭제)
            BinaryContent existingBinaryContent = Optional.ofNullable(binaryContentRepository.findById(user.getProfileId()))
                    .orElseThrow(() -> new NoSuchElementException("BinaryContent with profileId " + binaryContent.getId() + " not found" ));


            binaryContentRepository.delete(existingBinaryContent.getId());

            // 3.2 새로운 BinaryContent 저장 (새로운 프로필 이미지)
            BinaryContent newBinaryContent = new BinaryContent(binaryContent.getId(), binaryContent.getFileName(), binaryContent.getFileType(), binaryContent.getContent());
            binaryContentRepository.save(newBinaryContent);
        });

        return userRepository.save(user);
    }

    @Override
    public void delete(UUID userId) {
        if (!userRepository.existsById(userId)) {
            throw new NoSuchElementException("User with id " + userId + " not found");
        }
        userRepository.deleteById(userId);
        if (!binaryContentRepository.existsById(userId)) {
            throw new NoSuchElementException("BinaryContent with profileId " + userId + " not found");
        }
        binaryContentRepository.delete(userId);
        if (!userStatusRepository.existsByUserId(userId)) {
            throw new NoSuchElementException("UserStatus with profileId " + userId + " not found");
        }
        userStatusRepository.delete(userId);
    }
}
