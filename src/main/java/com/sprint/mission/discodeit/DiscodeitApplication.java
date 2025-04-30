package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.dto.auth.LoginDTO;
import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentDTO;
import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentResponseDTO;
import com.sprint.mission.discodeit.dto.channel.PrivateChannelDTO;
import com.sprint.mission.discodeit.dto.channel.PublicChannelDTO;
import com.sprint.mission.discodeit.dto.message.MessageRequestDTO;
import com.sprint.mission.discodeit.dto.message.MessageResponseDTO;
import com.sprint.mission.discodeit.dto.user.UserRequestDTO;
import com.sprint.mission.discodeit.dto.user.UserResponseDTO;
import com.sprint.mission.discodeit.dto.userstatus.UserStatusRequestDTO;
import com.sprint.mission.discodeit.service.basic.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@SpringBootApplication
public class DiscodeitApplication {
	private final static byte[] dummyData = new byte[1];

	public static void main(String[] args) throws IOException {
		ConfigurableApplicationContext context =
				SpringApplication.run(DiscodeitApplication.class, args);

		BasicUserService basicUserService = context.getBean("basicUserService",
				BasicUserService.class);
		BasicChannelService basicChannelService = context.getBean("basicChannelService",
				BasicChannelService.class);
		BasicMessageService basicMessageService = context.getBean("basicMessageService",
				BasicMessageService.class);
		BasicAuthService basicAuthService = context.getBean("basicAuthService",
				BasicAuthService.class);
		BasicUserStatusService basicUserStatusService = context.getBean("basicUserStatusService",
				BasicUserStatusService.class);
		BasicReadStatusService basicReadStatusService = context.getBean("basicReadStatusService",
				BasicReadStatusService.class);
		BasicBinaryContentService basicBinaryContentService = context.getBean("basicBinaryContentService",
				BasicBinaryContentService.class);

		// User, Channel, Message 더미 데이터 (최초 1회만 실행)
		createExampleUsers(basicUserService);
		createExampleChannels(basicChannelService, basicUserService);
		createExampleMessages(basicMessageService, basicUserService, basicChannelService);
//
// 		userServiceTest(basicUserService);
//		authServiceTest(basicAuthService);
//		userStatusServiceTest(basicUserStatusService, basicUserService);
//		channelServiceTest(basicChannelService, basicUserService);
//		messageServiceTest(basicMessageService, basicUserService, basicChannelService);
//		basicReadStatusService(basicUserService, basicReadStatusService);
		basicBinaryContentService(basicUserService, basicMessageService, basicBinaryContentService);

//		partition("User 제거 시 연관 도메인 삭제 확인");
//		basicUserService.deleteById(UUID.fromString("35db56d2-c16b-41ac-b05a-32536a6df8ab"));
//
//		partition("삭제 후 User");
//		basicUserService.findAll().forEach(System.out::println);
//
//		partition("삭제 후 BinaryContent");
//		basicBinaryContentService.findAll().forEach(System.out::println);
//
//		partition("삭제 후 UserStatus");
//		basicUserStatusService.findAll().forEach(System.out::println);
//
//		partition("Channel 제거 시 연관 도메인 삭제 확인");
//		basicChannelService.deleteById(UUID.fromString("180cc3ea-d8bd-4df9-94a0-d27fd64e31bb"));
//
//		partition("삭제 후 Channel");
//		basicChannelService.findAll().forEach(System.out::println);
//
//		partition("삭제 후 Message");
//		basicMessageService.findAll().forEach(System.out::println);
//
//		partition("삭제 후 ReadStatus");
//		basicReadStatusService.findAll().forEach(System.out::println);
//
//		partition("Message 삭제 전");
//		basicMessageService.findAll().forEach(System.out::println);
//		partition("Message 제거 시 연관 도메인 삭제 확인");
//		basicMessageService.deleteById(UUID.fromString("88eda8b0-9ca5-4197-b539-b705bee85049"));
//
//		partition("삭제 후 Message");
//		basicMessageService.findAll().forEach(System.out::println);
//
//		partition("삭제 후 BinaryContent");
//		basicBinaryContentService.findAll().forEach(System.out::println);

	}

	private static void userServiceTest(BasicUserService basicUserService) {
		UserResponseDTO user1 = basicUserService.findByName("edward");
		UserResponseDTO user2 = basicUserService.findByName("alice");

		basicUserService.addFriend(user1.getId(), user2.getId());

		partition("친구 추가 기능 확인");
		basicUserService.findAll().forEach(userResponseDTO -> {
			System.out.println("-----------------------" + userResponseDTO.getName() + "------------------------");
			System.out.println(userResponseDTO);
		});
	}

	private static void authServiceTest(BasicAuthService basicAuthService) {
		basicAuthService.login(new LoginDTO("edward", "edwardpw"));
	}

	private static void userStatusServiceTest(BasicUserStatusService basicUserStatusService,
											  BasicUserService basicUserService) {
		UUID testUser = basicUserService.findByName("diana").getId();

		Instant instant = Instant.parse("2025-04-28T07:10:00Z");

		partition("Diana 마지막 접속 시간 임의 변경");
		basicUserStatusService.findAll().stream()
				.filter(userStatusResponseDTO -> userStatusResponseDTO.getUserId().equals(testUser))
				.forEach(userStatusResponseDTO -> {
					basicUserStatusService.update(userStatusResponseDTO.getId(), new UserStatusRequestDTO(testUser, instant));
				});

		partition("변경 후 이름, 접속 상태 확인");

		basicUserService.findAll().forEach(userResponseDTO -> {
			System.out.println(userResponseDTO.getName() + " " + userResponseDTO.isLogin());
		});
	}

	private static void channelServiceTest(BasicChannelService basicChannelService,
										   BasicUserService basicUserService) {
		basicChannelService.findAll().forEach(channelResponseDTO -> {
			System.out.println("-------------------------" + channelResponseDTO.getChannelName() + "---------------------------------------");
			System.out.println(channelResponseDTO);
		});

		UUID user1 = basicUserService.findByName("alice").getId();
		UUID user2 = basicUserService.findByName("charlie").getId();
		UUID user3 = basicUserService.findByName("diana").getId();

		basicChannelService.findByNameContaining("sport").forEach(channelResponseDTO -> {
			basicChannelService.addUser(channelResponseDTO.getId(), user1);
			basicChannelService.addUser(channelResponseDTO.getId(), user2);
		});

		partition("유저 추가 기능 확인");
		basicChannelService.findAll().forEach(channelResponseDTO -> {
			System.out.println(channelResponseDTO.getChannelName() + "의 유저 목록");
			System.out.println(channelResponseDTO.getUsers());
		});
		partition("Alice가 참여한 채널 목록");
		basicChannelService.findAllByUserId(user1).forEach(System.out::println);

		basicChannelService.findByNameContaining("general").forEach(channelResponseDTO -> {
			basicChannelService.update(channelResponseDTO.getId(), new PublicChannelDTO("General",
					user3, "General Channel"));
		});

		partition("전체 채널 목록");
		basicChannelService.findAll().forEach(System.out::println);
	}

	private static void messageServiceTest(BasicMessageService basicMessageService,
										   BasicUserService basicUserService,
										   BasicChannelService basicChannelService) {
		UUID user1 = basicUserService.findByName("alice").getId();
		UUID generalChannelId = basicChannelService.findByNameContaining("neral").get(0).getId();

		partition("메시지 내용 변경 전");
		basicMessageService.findAllByUserId(user1).forEach(System.out::println);

		basicMessageService.findAllByUserId(user1).forEach(messageResponseDTO -> {
			basicMessageService.updateContent(messageResponseDTO.getId(), "I'm alice");
		});

		partition("메시지 내용 변경 확인");
		basicMessageService.findAllByUserId(user1).forEach(System.out::println);
	}

	private static void basicReadStatusService(BasicUserService basicUserService,
											   BasicReadStatusService basicReadStatusService) {
		UUID testUser = basicUserService.findByName("edward").getId();

		partition("Edward의 ReadStatus 확인");
		basicReadStatusService.findAllByUserId(testUser).forEach(System.out::println);
	}

	private static void basicBinaryContentService(BasicUserService basicUserService,
												  BasicMessageService basicMessageService,
												  BasicBinaryContentService basicBinaryContentService)
	throws IOException {
		UUID user = basicUserService.findByName("bob").getId();
		UUID userMessages = basicMessageService.findAllByUserId(user).get(0).getId();

		partition("전체 BinaryContent 확인");
		basicBinaryContentService.findAll().forEach(System.out::println);

		partition("유저 한명이 프로필 변경");
		basicUserService.updateProfileImage(user, new BinaryContentDTO( "dummyName.jpg", "image/jpeg", dummyData));
		basicBinaryContentService.findAll().forEach(System.out::println);

		partition("메시지에 있던 첨부 파일 삭제");
		basicMessageService.updateBinaryContent(userMessages, new ArrayList<>());
		basicBinaryContentService.findAll().forEach(System.out::println);

		partition("실제 이미지로 테스트");
		BufferedImage image = ImageIO.read(new File("src/test.jpg"));
		ByteArrayOutputStream outputStreamObj = new ByteArrayOutputStream();

		ImageIO.write(image, "jpg", outputStreamObj);

		byte[] byteArray = outputStreamObj.toByteArray();

		basicUserService.updateProfileImage(user, new BinaryContentDTO("test.jpg",
				"image/jpg", byteArray));

		UUID profileId = basicUserService.findById(user).getProfileId();
		byte[] readByteArray = basicBinaryContentService.findById(profileId).getData();
		ByteArrayInputStream inStreamObj = new ByteArrayInputStream(readByteArray);

		BufferedImage readImage = ImageIO.read(inStreamObj);
		ImageIO.write(readImage, "jpg", new File("src/readTestImg.jpg"));
	}

	private static UserRequestDTO createUser(String name, String email, String password, String statusMessage) {
		return new UserRequestDTO(name, email, password, statusMessage);
	}

	private static void createExampleUsers(BasicUserService basicUserService) {
		List<UserRequestDTO> users = new ArrayList<>();
		users.add(createUser("alice", "alice@example.com", "pass123", "hello!"));
		users.add(createUser("bob", "bob@example.com", "secure456", "busy right now"));
		users.add(createUser("charlie", "charlie@example.com", "mypassword", "feeling good"));
		users.add(createUser("diana", "diana@example.com", "abc123", "at work"));
		users.add(createUser("edward", "edward@example.com", "edwardpw", "available"));

		for (UserRequestDTO user : users) {
			BinaryContentDTO profileImage = new BinaryContentDTO(user.getName() + ".jpg", "image/jpeg", dummyData);
			basicUserService.save(user, profileImage);
		}
	}

	private static PublicChannelDTO createPublicChannel(String channelName, UUID channelMaster, String description) {
		return new PublicChannelDTO(channelName, channelMaster, description);
	}

	private static PrivateChannelDTO createPrivateChannel(UUID channelMaster, List<UUID> users) {
		return new PrivateChannelDTO(channelMaster, users);
	}

	private static void createExampleChannels(BasicChannelService basicChannelService,
											  BasicUserService basicUserService) {
		UUID user1 = basicUserService.findByName("alice").getId();
		UUID user2 = basicUserService.findByName("bob").getId();
		UUID user3 = basicUserService.findByName("charlie").getId();
		UUID user4 = basicUserService.findByName("diana").getId();
		UUID user5 = basicUserService.findByName("edward").getId();

		List<UUID> list1 = new ArrayList<>();
		list1.add(user4);
		list1.add(user5);

		basicChannelService.savePublicChannel(createPublicChannel("general", user1, "General discussion"));
		basicChannelService.savePublicChannel(createPublicChannel("sports", user2, "Sports talk"));

		basicChannelService.savePrivateChannel(createPrivateChannel(user3, list1));
	}

	private static void createExampleMessages(BasicMessageService basicMessageService,
											  BasicUserService basicUserService,
											  BasicChannelService basicChannelService) {
		// 유저 찾기
		UUID user1 = basicUserService.findByName("alice").getId();
		UUID user2 = basicUserService.findByName("bob").getId();

		// 채널 찾기
		UUID generalChannelId = basicChannelService.findByNameContaining("general").get(0).getId();
		UUID sportsChannelId = basicChannelService.findByNameContaining("sports").get(0).getId();

		// 메시지 생성
		MessageRequestDTO message1DTO = new MessageRequestDTO("Hello everyone!", user1, generalChannelId);
		MessageRequestDTO message2DTO = new MessageRequestDTO("Who's up for a game this weekend?", user2, sportsChannelId);
		MessageRequestDTO message3DTO = new MessageRequestDTO("Count me in!", user2, sportsChannelId);

		BinaryContentDTO dummyAttachment = new BinaryContentDTO("dummy.jpg", "image/jpeg", dummyData);
		List<BinaryContentDTO> dummyAttachments = List.of(dummyAttachment);

		// 메시지 생성 메소드 호출
		basicMessageService.save(message1DTO, new ArrayList<>());
		basicMessageService.save(message2DTO, dummyAttachments);
		basicMessageService.save(message3DTO, dummyAttachments);
	}

	private static void partition(String str) {
		System.out.println("----------------------" + str + "----------------------");
	}
}
