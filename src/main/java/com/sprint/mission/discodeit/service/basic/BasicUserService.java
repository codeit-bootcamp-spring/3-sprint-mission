package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.response.UserDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.OnlineStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Logger;

/**
 * DTO로 파라미터 받는 테스트용 서비스 구현체
 */

@RequiredArgsConstructor
@Service
public class BasicUserService implements UserService {

    private static final Logger logger = Logger.getLogger(BasicUserService.class.getName());

    private final UserRepository userRepositoryService;
    private final UserStatusRepository userStatusRepository;
    private final BinaryContentRepository fileBinaryContentRepository;

//    // 유저 생성
//    @Override
//    public UserResponse createUser(UserCreateRequest request, Optional<BinaryContentCreateRequest> ProfileImage) {
//
//        // 이메일 중복 검사 없을 수도 있으니까 옵셔널로.. 이미 존재하면 유저 객체 만들지 않고 생성 종료
//        Optional<User> existUserEmail = userRepositoryService.findUserByEmail(request.userEmail());
//        if (existUserEmail.isPresent()){
//            throw new IllegalArgumentException(request.userEmail() + "은 이미 존재하는 이메일 입니다.");
//        }
//        // 이름 중복 검사
//        Optional<User> existUserName = userRepositoryService.findUserByName(request.name());
//        if (existUserName.isPresent()){
//            throw new IllegalArgumentException(request.name() + "은 이미 존재하는 이름 입니다.");
//        }
//
//        //1. UserEmail 객체 생성
//        User user = new User(
//                request.name(),
//                request.userEmail(),
//                request.userPassword()
//        );
//
//        // 2. 프로필 이미지 있으면 저장
//        UUID profileId = ProfileImage
//                .map(profileImageRequest -> {
//                    String fileName = profileImageRequest.fileName();
//                    String contentType = profileImageRequest.contentType();
//                    byte[] bytes = profileImageRequest.data();
//
//                    BinaryContent profileImage = new BinaryContent(fileName, contentType, bytes);
//                    fileBinaryContentRepository.saveBinaryContent(profileImage);
//                    user.updateProfileId(profileImage.getId());
//                    return profileImage.getId();
//                })
//                .orElse(null); // 없으면 그냥 null
//
//        // 3. 접속 상태 생성
//        UserStatus userStatus = new UserStatus(user.getId());
//        userStatusRepository.saveUserStatus(userStatus);
//
//        // 4. 유저 저장
//        userRepositoryService.saveUser(user);
//
//        // UserStatus 조회
//        OnlineStatus status = getUserStatus(user.getId());
//
//        // 5. 최종적으로 생성된 User 반환
//        // 이 때 User + UserStatus → UserResponse로 변환
//        return new UserResponse(
//                user.getId(),
//                user.getName(),
//                user.getUserEmail(),
//                user.getProfileId(),
//                status
//        );
//    }

    // 기본 프로필 저장
    private Optional<BinaryContentCreateRequest> loadDefaultProfileImage() {
        try {
            ClassPathResource resource = new ClassPathResource("static/images/default-avatar.png");
            InputStream inputStream = resource.getInputStream();
            byte[] data = inputStream.readAllBytes();

            BinaryContentCreateRequest defaultImage = new BinaryContentCreateRequest(
                    "default-avatar.png",
                    "image/png",
                    data
            );
            return Optional.of(defaultImage);
        } catch (IOException e) {
            throw new RuntimeException("기본 프로필 이미지 로드 실패", e);
        }
    }

    // 유저 생성
    @Override
    public User createUser(UserCreateRequest request, Optional<BinaryContentCreateRequest> profileImage) {

        // 이메일 중복 검사 없을 수도 있으니까 옵셔널로.. 이미 존재하면 유저 객체 만들지 않고 생성 종료
        Optional<User> existUserEmail = userRepositoryService.findUserByEmail(request.userEmail());
        if (existUserEmail.isPresent()){
            throw new IllegalArgumentException(request.userEmail() + "은 이미 존재하는 이메일 입니다.");
        }
        // 이름 중복 검사
        Optional<User> existUserName = userRepositoryService.findUserByName(request.name());
        if (existUserName.isPresent()){
            throw new IllegalArgumentException(request.name() + "은 이미 존재하는 이름 입니다.");
        }

        //1. UserEmail 객체 생성
        User user = new User(
                request.name(),
                request.userEmail(),
                request.userPassword()
        );

        Optional<BinaryContentCreateRequest> imageToUse = profileImage.isPresent()
                ? profileImage
                : loadDefaultProfileImage(); // 여기서 기본 이미지 설정

//        // ✅ 프로필 이미지 저장
//        imageToUse.ifPresent(profile -> {
//            BinaryContent newProfileImage = new BinaryContent(
//                    profile.fileName(),
//                    profile.contentType(),
//                    profile.data()
//            );
//            fileBinaryContentRepository.saveBinaryContent(newProfileImage);
//            user.updateProfileId(newProfileImage.getId());
//        });

        // 2. 프로필 이미지 있으면 저장
        UUID profileId = imageToUse
                .map(profileImageRequest -> {
                    String fileName = profileImageRequest.fileName();
                    String contentType = profileImageRequest.contentType();
                    byte[] bytes = profileImageRequest.data();

                    BinaryContent newProfileImage = new BinaryContent(fileName, contentType, bytes);
                    fileBinaryContentRepository.saveBinaryContent(newProfileImage);
                    user.updateProfileId(newProfileImage.getId());
                    userRepositoryService.saveUser(user);
                    return newProfileImage.getId();
                })
                .orElse(null); // 없으면 그냥 null



        // 3. 접속 상태 생성
        UserStatus userStatus = new UserStatus(user.getId());
        userStatusRepository.saveUserStatus(userStatus);

        // 4. 유저 저장
        userRepositoryService.saveUser(user);

        // UserStatus 조회
        OnlineStatus status = getUserStatus(user.getId());

        // 5. 최종적으로 생성된 User 반환
        // 이 때 User + UserStatus → UserResponse로 변환
        return user;
    }


    @Override
    public void addUserToRepository(User user) {
        userRepositoryService.saveUser(user);

    }

    // 아이디로 검색
    @Override
    public UserDto getUserById(UUID id) {
        //유저 조회
        Optional<User> foundUser = userRepositoryService.findUserById(id);
        if (foundUser.isEmpty()){
            logger.warning("조회된 유저가 없습니다.");
        }
        // UserStatus 조회
        OnlineStatus status = getUserStatus(id);

        User user = foundUser.get();

        // 패스워드는 반환하지 않기
        return new UserDto(
                user.getId(),
                user.getCreatedAt(),
                user.getUpdatedAt(),
                user.getName(),
                user.getUserEmail(),
                user.getProfileId(),
                status
        );
    }

    // 이름으로 검색하기
    @Override
    public Optional<UserDto> searchUsersByName(String name) {

        //유저 조회
        Optional<User> foundUserResult = userRepositoryService.findUserByName(name);

        if (foundUserResult.isEmpty()) {
            logger.warning("조회된 유저가 없습니다.");
            return Optional.empty();
        }

        User user = foundUserResult.get();

        // UserStatus 조회
        OnlineStatus status = getUserStatus(user.getId());

        // 패스워드는 반환하지 않기
        UserDto response = new UserDto(
                user.getId(),
                user.getCreatedAt(),
                user.getUpdatedAt(),
                user.getName(),
                user.getUserEmail(),
                user.getProfileId(),
                status
        );

        return Optional.of(response);
    }

    // 전체 유저 조회
    @Override
    public List<UserDto> getAllUsers() {

        List<User> foundResult = userRepositoryService.findUserAll();

        return foundResult.stream()
                .map(user -> {
                    // 요소(유저)마다 상태 추출
                    OnlineStatus status = getUserStatus(user.getId());

                    return new UserDto(
                            user.getId(),
                            user.getCreatedAt(),
                            user.getUpdatedAt(),
                            user.getName(),
                            user.getUserEmail(),
                            user.getProfileId(),
                            status
                    );
                })
                .toList();
    }

    // 이름 수정
    @Override
    public boolean updateUserName(UUID id, String newName) {
        //유저 조회
        Optional<User> foundUser = userRepositoryService.findUserById(id);

        if (foundUser.isEmpty()){
            logger.warning("조회된 유저가 없습니다.");
            return false;
        }

        // 이름 중복 검사
        Optional<User> existUserName = userRepositoryService.findUserByName(newName);
        if (existUserName.isPresent()){
            throw new IllegalArgumentException(newName + "은 이미 존재하는 이름 입니다.");
        }

        User user = foundUser.get();

        boolean update = userRepositoryService.updateUserName(user, newName);
        if (update){
            logger.info("유저의 이름이 변경되었습니다.");
            return true;
        }
        else{
            logger.warning("유저의 이름 변경에 실패했습니다.");
            return false;
        }
    }

    //유저 프로필 사진 변경
    @Override
    public boolean updateProfileImage(UUID userId, Optional<BinaryContentCreateRequest> request) {
        //유저 조회
        Optional<User> foundUser = userRepositoryService.findUserById(userId);
        if(foundUser.isEmpty()){
            logger.warning("조회된 유저가 없습니다.");
            return false;
        }

        User user = foundUser.get();

        // 기존 이미지 있으면 삭제
        if (user.getProfileId() != null){
            boolean deleteResult = fileBinaryContentRepository.deleteById(user.getProfileId());
            if (deleteResult){
                logger.info("프로필 이미지가 삭제되었습니다.");
            } else{
                logger.warning("프로필 이미지 삭제에 실패했습니다.");
                return false;
            }
        }

        // 새 프로필 이미지 저장
        UUID profileId = request
                .map(profileImageUpdate -> {
                    String fileName = profileImageUpdate.fileName();
                    String contentType = profileImageUpdate.contentType();
                    byte[] bytes = profileImageUpdate.data();

                    BinaryContent profileImage = new BinaryContent(fileName, contentType, bytes);
                    fileBinaryContentRepository.saveBinaryContent(profileImage);
                    user.updateProfileId(profileImage.getId());
                    return profileImage.getId();
                })
                .orElse(null); // 없으면 그냥 null

        // 유저 저장
        userRepositoryService.saveUser(user);

        logger.info("프로필 이미지가 변경되었습니다");

        return true;
    }

    // 유저 삭제
    @Override
    public boolean deleteUserById(UUID id) {
        //유저 조회
        Optional<User> foundUser = userRepositoryService.findUserById(id);
        if(foundUser.isEmpty()){
            logger.warning("조회된 유저가 없습니다.");
            return false;
        }

        User user = foundUser.get();

        // 이미지 있으면 같이 삭제
        if (user.getProfileId() != null){
            boolean deleteResult = fileBinaryContentRepository.deleteById(user.getProfileId());
            if (deleteResult){
                logger.info("프로필 이미지가 삭제되었습니다.");
            } else{
                logger.warning("프로필 이미지 삭제에 실패했습니다.");
                return false;
            }
        }

        // 접속 상태 삭제
        userStatusRepository.deleteUserStatus(id);

        // 유저 삭제
        userRepositoryService.deleteUser(id);
        return true;
    }

    // UserStatus 조회
    private OnlineStatus getUserStatus(UUID userId) {
        return userStatusRepository.findStatus(userId)
                .map(UserStatus::getStatus)
                .orElse(OnlineStatus.Unknown);
    }
}
