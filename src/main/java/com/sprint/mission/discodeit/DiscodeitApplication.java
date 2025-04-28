package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.Dto.channel.ChannelFindResponse;
import com.sprint.mission.discodeit.Dto.channel.ChannelUpdateRequest;
import com.sprint.mission.discodeit.Dto.channel.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.Dto.channel.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.Dto.user.UserCreateRequest;
import com.sprint.mission.discodeit.Dto.user.UserFindResponse;
import com.sprint.mission.discodeit.Dto.userStatus.ProfileUploadRequest;
import com.sprint.mission.discodeit.Dto.userStatus.ProfileUploadResponse;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.*;
import com.sprint.mission.discodeit.service.basic.BasicUserService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import java.util.List;
import java.util.Set;

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
// 테스트 코드 시작
		try {
			// 1. createUser
			UserCreateRequest createRequest = new UserCreateRequest("testuser", "test@example.com", "password", null);
			UserCreateRequest createRequest1 = new UserCreateRequest("testuser1", "1test@example.com", "password", null);
			UserCreateRequest createRequest2 = new UserCreateRequest("testuse3r", "t3est@example.com", "password", null);
			UserCreateRequest createRequest4 = new UserCreateRequest("testuse21r", "test2345@example.com", "password", null);

			User createdUser = basicUserService.createUser(createRequest);
			System.out.println("[createUser] 생성된 사용자 ID: " + createdUser.getId());

			// 2. findUserById
			UserFindResponse foundUser = basicUserService.findUserById(createdUser.getId());
			System.out.println("[findUserById] 조회된 사용자 이름: " + foundUser.getUsername());

			// 3. updateUser
			basicUserService.updateUser(createdUser.getId(), "updatedName");
			UserFindResponse updatedUser = basicUserService.findUserById(createdUser.getId());
			System.out.println("[updateUser] 수정된 사용자 이름: " + updatedUser.getUsername());

			// 4. findAllUsers
			List<UserFindResponse> allUsers = basicUserService.findAllUsers();
			System.out.println("[findAllUsers] 전체 사용자 수: " + allUsers.size());

			// 5. updateImage
			byte[] dummyImage = "dummy image".getBytes();
			ProfileUploadRequest profileUploadRequest = new ProfileUploadRequest(createdUser.getId(), dummyImage);
			ProfileUploadResponse profileUploadResponse = basicUserService.updateImage(profileUploadRequest);
			System.out.println("[updateImage] 업로드된 프로필 ID: " + profileUploadResponse.getProfileId());

			// 6. deleteUser
			basicUserService.deleteUser(createdUser.getId());
			System.out.println("[deleteUser] 사용자 삭제 완료");

			// 7. 삭제된 사용자 조회 시도 (예외 예상)
			try {
				basicUserService.findUserById(createdUser.getId());
			} catch (RuntimeException e) {
				System.out.println("[deleteUser 확인] 삭제된 사용자 조회 실패 예상대로 발생: " + e.getMessage());
			}

		} catch (Exception e) {
			System.out.println("테스트 중 오류 발생: " + e.getMessage());
			e.printStackTrace();
		}

		System.out.println("==== BasicChannelService 테스트 시작 ===>");

		try {
			// (0) 먼저 유저 하나 생성
			UserCreateRequest createRequest = new UserCreateRequest("channeluser", "channeluser@example.com", "password", null);
			User createdUser = basicUserService.createUser(createRequest);
			System.out.println("[createUser] 채널용 사용자 생성 완료: " + createdUser.getId());

			Set<User> users = Set.of(createdUser);

			// 1. public 채널 생성
			PublicChannelCreateRequest publicRequest = new PublicChannelCreateRequest(
					users,
					"TestPublicChannel",
					"This is a public test channel"
			);
			Channel publicChannel = channelService.createChannel(publicRequest);
			System.out.println("[createPublicChannel] 생성된 Public 채널 ID: " + publicChannel.getId());

			// 2. private 채널 생성
			PrivateChannelCreateRequest privateRequest = new PrivateChannelCreateRequest(
					users
			);
			Channel privateChannel = channelService.createChannel(privateRequest);
			System.out.println("[createPrivateChannel] 생성된 Private 채널 ID: " + privateChannel.getId());

			// 3. 생성된 채널 조회
			ChannelFindResponse publicChannelResponse = channelService.findChannel(publicChannel.getId());
			System.out.println("[findChannel] Public 채널 이름: " + publicChannelResponse.getChannel().getName());

			ChannelFindResponse privateChannelResponse = channelService.findChannel(privateChannel.getId());
			System.out.println("[findChannel] Private 채널 ID 조회 성공: " + privateChannelResponse.getChannel().getName());

			// 4. 전체 채널 조회
			List<Channel> allChannels = channelService.findAllChannel();
			System.out.println("[findAllChannel] 전체 채널 수: " + allChannels.size());

			// 5. 채널 이름 변경 (Public 채널만 가능)
			ChannelUpdateRequest updateRequest = new ChannelUpdateRequest(publicChannel.getId(), "UpdatedPublicChannelName");
			channelService.updateChannelName(updateRequest);
			Channel updatedChannel = channelService.findChannelById(publicChannel.getId());
			System.out.println("[updateChannelName] 수정된 Public 채널 이름: " + updatedChannel.getName());

			// 6. 채널 삭제
			channelService.deleteChannel(publicChannel.getId());
			System.out.println("[deleteChannel] Public 채널 삭제 완료");

			channelService.deleteChannel(privateChannel.getId());
			System.out.println("[deleteChannel] Private 채널 삭제 완료");

			// 7. 삭제된 채널 조회 시도 (예외 예상)
			try {
				channelService.findChannelById(publicChannel.getId());
			} catch (RuntimeException e) {
				System.out.println("[deleteChannel 확인] 삭제된 Public 채널 조회 실패 예상대로 발생: " + e.getMessage());
			}

			try {
				channelService.findChannelById(privateChannel.getId());
			} catch (RuntimeException e) {
				System.out.println("[deleteChannel 확인] 삭제된 Private 채널 조회 실패 예상대로 발생: " + e.getMessage());
			}

		} catch (Exception e) {
			System.out.println("BasicChannelService 테스트 중 오류 발생: " + e.getMessage());
			e.printStackTrace();
		}

		System.out.println("==== BasicChannelService 테스트 종료 ====");


		System.out.println("<=== Test Over  ====");
	}
}