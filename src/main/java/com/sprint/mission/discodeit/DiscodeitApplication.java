package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.Dto.channel.ChannelFindResponse;
import com.sprint.mission.discodeit.Dto.channel.ChannelUpdateRequest;
import com.sprint.mission.discodeit.Dto.channel.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.Dto.channel.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.Dto.user.UserCreateRequest;
import com.sprint.mission.discodeit.Dto.user.UserCreateResponse;
import com.sprint.mission.discodeit.Dto.user.UserFindResponse;
import com.sprint.mission.discodeit.Dto.userStatus.*;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.service.*;
import com.sprint.mission.discodeit.service.basic.BasicUserService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@SpringBootApplication
public class DiscodeitApplication {

	public static void main(String[] args) {
		ApplicationContext context = SpringApplication.run(DiscodeitApplication.class, args);

		BasicUserService basicUserService = (BasicUserService) context.getBean("basicUserService", UserService.class);
		ChannelService channelService = context.getBean("basicChannelService", ChannelService.class);
		MessageService messageService = context.getBean("basicMessageService", MessageService.class);
		AuthService authService = context.getBean(AuthService.class);
		ReadStatusService readStatusService = context.getBean("basicReadStatusService", ReadStatusService.class);
		UserStatusService userStatusService = context.getBean("basicUserStatusService", UserStatusService.class);
		BinaryContentService binaryContentService = context.getBean("basicBinaryContentService", BinaryContentService.class);

		// 빈 주입------------------------------------------------------------------------------------------------------------
		System.out.println("==== Start Test ===>");

		System.out.println("==== BasicUserService 테스트 시작 ===>");

		try {
			// 1. create (프로필 이미지 없이 생성)
			UserCreateRequest createRequest = new UserCreateRequest("testuser", "test@example.com", "password");
			UserCreateResponse createdUser = basicUserService.create(createRequest);
			System.out.println("[create] 생성된 사용자 ID: " + createdUser.id());

			// 2. findUserById
			UserFindResponse foundUser = basicUserService.findUserById(createdUser.id());
			System.out.println("[findUserById] 조회된 사용자 이름: " + foundUser.username());

			// 3. findAllUsers
			List<UserFindResponse> allUsers = basicUserService.findAllUsers();
			System.out.println("[findAllUsers] 전체 사용자 수: " + allUsers.size());

			// 4. updateUser (이름 수정)
			String newName = "updatedTestUser";
			basicUserService.updateUser(createdUser.id(), newName);
			UserFindResponse updatedUser = basicUserService.findUserById(createdUser.id());
			System.out.println("[updateUser] 수정된 사용자 이름: " + updatedUser.username());

			// 5. updateImage (프로필 이미지 업로드)
			byte[] dummyImage = "dummyImageContent".getBytes();
			ProfileUploadRequest profileUploadRequest = new ProfileUploadRequest(createdUser.id(), dummyImage);
			ProfileUploadResponse profileUploadResponse = basicUserService.updateImage(profileUploadRequest);
			System.out.println("[updateImage] 새 프로필 ID: " + profileUploadResponse.profileId());

//			// 6. deleteUser
			basicUserService.deleteUser(createdUser.id());
			System.out.println("[deleteUser] 사용자 삭제 완료");

			// 7. 삭제된 유저 조회 시도 (예외 발생 예상)
			try {
				basicUserService.findUserById(createdUser.id());
			} catch (RuntimeException e) {
				System.out.println("[deleteUser 확인] 삭제된 사용자 조회 실패 예상대로 발생: " + e.getMessage());
			}

		} catch (Exception e) {
			System.out.println("BasicUserService 테스트 중 예외 발생: " + e.getMessage());
			e.printStackTrace();
		}

		System.out.println("==== BasicUserService 테스트 종료 ====");

		System.out.println("==== BasicUserService + BasicUserStatusService 통합 테스트 시작 ===>");

		try {
			// 1. User 생성
			UserCreateRequest createRequest = new UserCreateRequest("integratedTester", "integration@example.com", "password");
			UserCreateResponse createdUser = basicUserService.create(createRequest);
			System.out.println("[User create] 생성된 사용자 ID: " + createdUser.id());

			// 2. User 조회
			UserFindResponse foundUser = basicUserService.findUserById(createdUser.id());
			System.out.println("[User find] 조회된 사용자 이름: " + foundUser.username());

			// 3. UserStatus 생성
			UUID userStatusId = createdUser.userStatusId();
			System.out.println("[UserStatus 준비 완료] User 생성 시 같이 생성된 UserStatus ID: " + userStatusId);

			// 4. UserStatus 조회
			UserStatus foundUserStatus = userStatusService.find(userStatusId);
			System.out.println("[UserStatus find] 조회된 UserStatus ID: " + foundUserStatus.getId());

			// 5. 전체 User 조회
			List<UserFindResponse> allUsers = basicUserService.findAllUsers();
			System.out.println("[User findAll] 전체 사용자 수: " + allUsers.size());

			// 6. User 이름 수정
			String newName = "updatedIntegratedTester";
			basicUserService.updateUser(createdUser.id(), newName);
			UserFindResponse updatedUser = basicUserService.findUserById(createdUser.id());
			System.out.println("[User update] 수정된 사용자 이름: " + updatedUser.username());

			// 7. UserStatus 업데이트 (ID 기준)
			Instant updatedTime = Instant.now();
			UserStatusUpdateRequest userStatusUpdateRequest = new UserStatusUpdateRequest(foundUserStatus.getId(), updatedTime);
			userStatusService.update(userStatusUpdateRequest);
			System.out.println("[UserStatus update] UserStatus ID 기준 업데이트 완료");

			// 8. UserStatus 업데이트 (UserId 기준)
			Instant newerTime = Instant.now();
			userStatusService.updateByUserId(createdUser.id(), newerTime);
			System.out.println("[UserStatus updateByUserId] UserId 기준 업데이트 완료");

			// 9. User 프로필 이미지 업로드
			byte[] dummyImage = "dummyImageContentForIntegration".getBytes();
			ProfileUploadRequest profileUploadRequest = new ProfileUploadRequest(createdUser.id(), dummyImage);
			ProfileUploadResponse profileUploadResponse = basicUserService.updateImage(profileUploadRequest);
			System.out.println("[User profile upload] 새 프로필 ID: " + profileUploadResponse.profileId());

			// 10. UserStatus 삭제
//			userStatusService.delete(foundUserStatus.getId());
//			System.out.println("[UserStatus delete] UserStatus 삭제 완료");

			// 11. User 삭제
			basicUserService.deleteUser(createdUser.id());
			System.out.println("[User delete] 사용자 삭제 완료");



			// 12. 삭제된 User 조회 시도 (예외 발생 예상)
			try {
				basicUserService.findUserById(createdUser.id());
			} catch (RuntimeException e) {
				System.out.println("[User delete 확인] 삭제된 사용자 조회 실패 예상대로 발생: " + e.getMessage());
			}

			// 삭제된 UserStatus 조회 시도 (예외 발생 예상)
			try {
				userStatusService.find(foundUserStatus.getId()); // exception 안 던지게 만든것같음
			} catch (RuntimeException e) {
				System.out.println("[UserStatus delete 확인] 삭제된 UserStatus 조회 실패 예상대로 발생: " + e.getMessage());
			}

		} catch (Exception e) {
			System.out.println("통합 테스트 중 예외 발생: " + e.getMessage());
			e.printStackTrace();
		}

		System.out.println("==== BasicUserService + BasicUserStatusService 통합 테스트 종료 ====");


		System.out.println("<=== Test Over  ====");
	}
}