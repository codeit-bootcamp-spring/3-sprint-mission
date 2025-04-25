package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.Dto.user.ProfileUploadRequest;
import com.sprint.mission.discodeit.Dto.user.ProfileUploadResponse;
import com.sprint.mission.discodeit.Dto.user.UserCreateRequest;
import com.sprint.mission.discodeit.Dto.user.UserFindResponse;
import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import java.util.UUID;

@SpringBootApplication
public class DiscodeitApplication {



	public static void main(String[] args) {
		ApplicationContext context = SpringApplication.run(DiscodeitApplication.class, args);

		UserService userService = context.getBean("basicUserService", UserService.class);
		ChannelService channelService = context.getBean("basicChannelService", ChannelService.class);
		MessageService messageService = context.getBean("basicMessageService", MessageService.class);
//		BinaryContentRepository binaryContentRepository = context.getBean("fileBinaryContentRepository", BinaryContentRepository.class);
//		BinaryContentRepository binaryContentRepository = context.getBean("jcfBinaryContentRepository", BinaryContentRepository.class);
		UserStatusRepository userStatusRepository = context.getBean("fileUserStatusRepository", UserStatusRepository.class);
//		UserStatusRepository userStatusRepository = context.getBean("jcfUserStatusRepository", UserStatusRepository.class);


		byte[] image1 = new byte[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
		byte[] image2 = new byte[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9};

		UserCreateRequest userCreateDto1 = new UserCreateRequest("user1", "user1@user.com", "1234", image1);
		UserCreateRequest userCreateDto2 = new UserCreateRequest("user2", "user2@user.com", "1234", image2);
		UserCreateRequest userCreateDto3 = new UserCreateRequest("user3", "user3@user.com", "1234");

		System.out.println("=== User 테스트 ===");

		User user1 = userService.createUser(userCreateDto1);
		User user2 = userService.createUser(userCreateDto2);
		User user3 = userService.createUser(userCreateDto3);


		System.out.println("\n[모든 유저 출력]");
		userService.findAllUsers().forEach(u -> System.out.println("- " + u.getUsername()+" online "+u.isOnline()));

		System.out.println("\n[단일 유저 조회]");
		System.out.println("조회된 유저: " + userService.findUserById(user1.getId()).getUsername());

		System.out.println("\n[유저 이름 수정]");
		userService.updateUser(user1.getId(), "new user");
		System.out.println("수정 결과: " + userService.findUserById(user1.getId()).getUsername());

		System.out.println("\n삭제 예정 user1 관련 객체 id(호출 후 data에서 확인)");
		System.out.println("user1의 profileId = " + user1.getProfileId());
		System.out.println("user1의 userStatusId = " + userStatusRepository.findUserStatusByUserId(user1.getId()).getId());

		System.out.println("\n[유저 삭제]");
		userService.deleteUser(user1.getId());

		System.out.println("\n[최종 유저 목록]");
		userService.findAllUsers().forEach(u -> System.out.println("- " + u.getUsername()));

		System.out.println("\n\n=== User profileId 테스트 ===");
		System.out.println(user1.getProfileId());
		System.out.println(user2.getProfileId());
		System.out.println(user3.getProfileId());

		UserFindResponse user2Response = userService.findUserById(user2.getId());
		UserFindResponse user3Response = userService.findUserById(user3.getId());

		System.out.println("user2Response.getProfileId() = " + user2Response.getProfileId());
		System.out.println("user3Response.getProfileId() = " + user3Response.getProfileId());

		System.out.println("\n\n=== User profileId update 테스트 ===");
		ProfileUploadRequest profileUploadRequest = new ProfileUploadRequest(user2.getId(), image2);
		ProfileUploadRequest profileUploadRequest2 = new ProfileUploadRequest(user3.getId(), image1);

		userService.updateImage(profileUploadRequest);
		userService.updateImage(profileUploadRequest2);

		user2Response = userService.findUserById(user2.getId());
		user3Response = userService.findUserById(user3.getId());

		System.out.println("user2Response.getProfileId() = " + user2Response.getProfileId());
		System.out.println("user3Response.getProfileId() = " + user3Response.getProfileId());



		System.out.println("\n\n=== Channel 테스트 ===");
		Channel ch1 = channelService.createChannel("test channel 1");
		Channel ch2 = channelService.createChannel("test channel 2");
		Channel ch3 = channelService.createChannel("test channel 3");

		System.out.println("\n[모든 채널 출력]");
		channelService.findAllChannel().forEach(c -> System.out.println("- " + c.getName()));

		System.out.println("\n[단일 채널 조회]");
		System.out.println("조회된 채널: " + channelService.findChannelById(ch1.getId()).getName());

		System.out.println("\n[채널 이름 수정]");
		channelService.updateChannel(ch2.getId(), "updated channel");
		System.out.println("수정 결과: " + channelService.findChannelById(ch2.getId()).getName());

		System.out.println("\n[채널 삭제]");
		channelService.deleteChannel(ch1.getId());

		System.out.println("\n[최종 채널 목록]");
		channelService.findAllChannel().forEach(c -> System.out.println("- " + c.getName()));


		System.out.println("\n\n=== Message 테스트 ===");
		Message m1 = messageService.createMessage(user2.getId(), ch2.getId(), "Hello from Korea!");
		Message m2 = messageService.createMessage(user3.getId(), ch3.getId(), "Hello from America!");

		System.out.println("\n[모든 메시지 출력]");
		messageService.findAllMessages().forEach(m -> System.out.println("- " + m.getContent()));

		System.out.println("\n[단일 메시지 조회]");
		System.out.println("조회된 메시지: " + messageService.findMessageById(m1.getId()).getContent());

		System.out.println("\n[메시지 수정]");
		messageService.updateMessage(m2.getId(), "Hello from Japan!");
		System.out.println("수정 결과: " + messageService.findMessageById(m2.getId()).getContent());

		System.out.println("\n[메시지 삭제]");
		messageService.deleteMessage(m1.getId());

		System.out.println("\n[최종 메시지 목록]");
		messageService.findAllMessages().forEach(m -> System.out.println("- " + m.getContent()));


	}

}
