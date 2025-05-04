package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.dto.request.*;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.basic.BasicChannelService;
import com.sprint.mission.discodeit.service.basic.BasicUserService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.*;

@SpringBootApplication
public class DiscodeitApplication {

	public static void main(String[] args) {
		ConfigurableApplicationContext context = SpringApplication.run(DiscodeitApplication.class, args);

		// Spring Context에서 Bean 조회
		UserService userService = context.getBean(UserService.class);
		ChannelService channelService = context.getBean(ChannelService.class);
		MessageService messageService = context.getBean(MessageService.class);

		// 순환 참조 setter 주입
		if (userService instanceof BasicUserService && channelService instanceof BasicChannelService) {
			((BasicUserService) userService).setChannelService(channelService);
		}

//		// === 유저 CRUD 테스트 ===
//		//User test01 = userService.createUser("test01", "test01@.com");
//		User test01 = userService.createUser(new UserCreateRequest("test01", "test01@.com","1234"), Optional.empty());
//
//		UserCreateRequest test02userRequest = new UserCreateRequest("test02", "test02@.com","12345");
//		byte[] content = {123}; // 파일 바이트
//		BinaryContentCreateRequest test02profileRequest = new BinaryContentCreateRequest("123.jpg", content, "image/jpeg");
//		User test02 = userService.createUser(test02userRequest, Optional.of(test02profileRequest));
//
//		//User test03 = userService.createUser("test03", "test03@.com");
//		User test03 = userService.createUser(new UserCreateRequest("test03", "test03@.com","1234"), Optional.empty());
//		//User test04 = userService.createUser("test04", "test04@.com");
//
//
//		userService.find(test01.getId()).ifPresentOrElse(userDTO -> {
//			System.out.println("===개별 User 조회 ===\n" + userDTO);
//
//			System.out.println("\n=== test01 이름변경 + 프로필이미지 추가 후 조회 ===");
//			//userService.updateUserName(userDTO.getId(), "test0101");
//			UserUpdateRequest updateRequest = new UserUpdateRequest("test0101", "test01@.com");
//			byte[] newImageContent = {42, 24, 55};  // 새로운 이미지 바이트
//			BinaryContentCreateRequest profileImageRequest =
//					new BinaryContentCreateRequest("profile_test01.jpg", newImageContent, "image/jpeg");
//			userService.update(test01.getId(), updateRequest, Optional.of(profileImageRequest));
//
//			//System.out.println(userService.find(userDTO.getId()).orElse(null));
//			System.out.println(userService.find(test01.getId()).orElse(null));
//		}, () -> {
//			System.out.println("test01 유저를 찾을 수 없습니다.");
//		});
//
//		System.out.println("\n=== 전체 유저 조회 ===");
//		userService.findAll().forEach(System.out::println);
//
//		// test02 삭제 및 조회
//		userService.delete(test02.getId());
//		System.out.println("\n=== test02 삭제 후 전체 조회 ===");
//		userService.findAll().forEach(System.out::println);
//		System.out.println();
//
//		System.out.println("===============================================================================================");
//
//		// test01이 public채널 생성 & test03이 private채널 생성
//		//Channel channel1 = channelService.create("2025_Channel", test01);
//		PublicChannelCreateRequest test01publicRequest = new PublicChannelCreateRequest("2025_Channel", test01.getId());
//		Channel channel1 = channelService.create(test01publicRequest);
//
//		//Channel channel2 = channelService.create("2024_Channel", test02);
//		List<UUID> memberIds = new ArrayList<>();
//		memberIds.add(test01.getId());
//		PrivateChannelCreateRequest privateRequest = new PrivateChannelCreateRequest("2024_Channel", test03.getId(), memberIds);
//		Channel channel2 = channelService.create(privateRequest);
//
//		// 개별 채널 조회
//		System.out.println("\n=== 개별 채널 조회 ===");
//		channelService.find(channel2.getId()).ifPresentOrElse(
//				channel -> System.out.println(channel),
//				() -> System.out.println("해당 채널이 존재하지 않습니다.")
//		);
//
//		System.out.println("\n=== 채널 이름(2024_Channel) 수정 후 전체 채널 조회 ===");
//		PublicChannelUpdateRequest updateRequest = new PublicChannelUpdateRequest("2023_channel");
//		channelService.update(channel2.getId(), updateRequest);
//		try {
//			channelService.update(channel2.getId(), updateRequest);
//		} catch (IllegalArgumentException e) {
//			System.out.println("[채널명 변경 실패: " + e.getMessage() + "]\n");
//		}
//		List<ChannelDto> allChannels = channelService.findAllByUserId(test01.getId());
//		allChannels.forEach(channelDto -> System.out.println(channelDto));
//
//		// 유저 추가 (test02, test03을 채널에 추가)  & 삭제된 유저는 data에 담을 수 없다.
//		System.out.println("\n=== 2025_Channel에 멤버(test02(탈퇴),test03) 추가 ===");
//		try {
//			channelService.addMember(channel1.getId(), test02.getId()); // test02는 삭제된 유저
//		} catch (IllegalArgumentException e) {
//			System.out.println("예외 발생: " + e.getMessage());
//		}
//		channelService.addMember(channel1.getId(), test03.getId());
//
//		// 채널 멤버 조회
//		System.out.println("\n=== 2025_Channel 전체멤버(채널소유자 포함) 조회 ===");
//		List<User> channelMembers = channelService.getChannelMembers(channel1.getId());
//		channelMembers.forEach(user -> System.out.println("멤버: " + user.getUsername()));
//
//		// 유저 제거 테스트 (test03 제거)
//		System.out.println("\n=== 2025_Channel에서 멤버 제거(test03) 후 조회 ===");
//		channelService.removeMember(channel1.getId(), test03.getId());
//		channelService.getChannelMembers(channel1.getId()).forEach(user -> System.out.println("멤버: " + user.getUsername()));
//
//		// 채널 삭제 후 전체 채널 조회
//		System.out.println("\n=== 2025_Channel 삭제 후 남은 채널 조회 ===");
//		channelService.deleteChannel(channel1.getId());
//		List<ChannelDto> totalChannels = channelService.findAllByUserId(test01.getId());
//		if (totalChannels.isEmpty()) {
//			System.out.println("남은 채널 없음");
//		} else {
//			totalChannels.forEach(channel -> System.out.println(channel));
//		}

		System.out.println("\n============================================\n[##유저+채널+메시지 crud 테스트]====================\n");

		User user1 = userService.createUser(
				new UserCreateRequest("test01", "test01@.com", "1234"),
				Optional.empty()
		);

		byte[] content = {123,68,12};
		User user2 = userService.createUser(
				new UserCreateRequest("test02", "test02@.com", "12345"),
				Optional.of(new BinaryContentCreateRequest("123.jpg", content, "image/jpeg"))
		);

		User user3 = userService.createUser(
				new UserCreateRequest("test03", "test03@.com", "asd"),
				Optional.empty()
		);

		User user4 = userService.createUser(
				new UserCreateRequest("test04", "test04@.com", "134"),
				Optional.empty()
		);

//		Map<String, User> users = new HashMap<>();
//		String[][] userInfos = {
//				{"john", "john@.com"},
//				{"jane", "john@.com"}, // 중복
//				{"tom", "tom@.com"},
//				{"john", "bren@.com"},
//				{"Ryan", "Ryan@.com"}
//		};
//
//		for (String[] info : userInfos) {
//			try {
//				User user = userService.createUser(info[0], info[1]);
//				users.put(info[1], user);
//			} catch (IllegalArgumentException e) {
//				System.out.println("[##이메일 중복하여 유저 생성 시도] \n유저 생성 실패: " + e.getMessage());
//			}
//		}
//
//		User user1 = users.get("john@.com");
//		User user2 = users.get("Ryan@.com");
//		User user3 = users.get("tom@.com");
//		User user4 = users.get("bren@.com");

		//Channel studyRoom = channelService.createChannel("공부방", user1);
		PublicChannelCreateRequest user1publicRequest = new PublicChannelCreateRequest("공부방", user1.getId());

		Channel studyRoom = channelService.create(user1publicRequest);
		channelService.addMember(studyRoom.getId(), user2.getId());
		channelService.addMember(studyRoom.getId(), user3.getId());
		channelService.addMember(studyRoom.getId(), user4.getId());

		//Channel noticeRoom = channelService.createChannel("공지방", user2);
		List<UUID> memberIds = new ArrayList<>();
		memberIds.add(user1.getId());
		PrivateChannelCreateRequest privateRequest = new PrivateChannelCreateRequest("공지방", user2.getId(), memberIds);

		Channel noticeRoom = channelService.create(privateRequest);
		channelService.addMember(noticeRoom.getId(), user3.getId());
		channelService.addMember(noticeRoom.getId(), user4.getId());
		channelService.addMember(noticeRoom.getId(), user1.getId());



		byte[] content1 = {1, 2, 3};
		byte[] content2 = {4, 5, 6};
		BinaryContentCreateRequest file1 = new BinaryContentCreateRequest("123.jpg", content1, "image/jpeg");
		BinaryContentCreateRequest file2 = new BinaryContentCreateRequest("456.pdf", content2, "pdf");

		messageService.createMessage(
				new MessageCreateRequest(studyRoom.getId(), user1.getId(), "안녕하세요 저는 user1입니다. 첨부파일 0개."),
				null
		);

		messageService.createMessage(
				new MessageCreateRequest(studyRoom.getId(), user2.getId(), "user2 입니다. 첨부파일 1개"),
				List.of(file1)
		);

		messageService.createMessage(
				new MessageCreateRequest(studyRoom.getId(), user3.getId(), "user3 입니다. 첨부파일 2개"),
				List.of(file1, file2)
		);

		messageService.createMessage(
				new MessageCreateRequest(noticeRoom.getId(), user4.getId(), "안녕하세요 저는 user4입니다. 첨부파일 0개."),
				null
		);

		messageService.createMessage(
				new MessageCreateRequest(noticeRoom.getId(), user1.getId(), "공지없습니다. 첨부파일 0개."),
				null
		);

//		messageService.createMessage(studyRoom.getId(), user4.getId(), "공부 열심히 하자!");
//		messageService.createMessage(noticeRoom.getId(), user2.getId(), "공지사항입니다.");
//		messageService.createMessage(noticeRoom.getId(), user1.getId(), "user1입니다.");
//		messageService.createMessage(noticeRoom.getId(), user4.getId(), "user4입니다.");
//		messageService.createMessage(noticeRoom.getId(), user3.getId(), "user3입니다.");

		// 메세지 생성시간 & 메세지내용변경 수정시간 텀을 확인
		try {
			Thread.sleep(1000); // 1000ms = 1초
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// === 메시지 기능 테스트 ===

		//공지방에서 user1이 보낸 메시지 조회
		System.out.println("\n[##공지방에서 user1이 보낸 메시지]");
		messageService.getMessagesBySenderInChannel(noticeRoom.getId(), user1.getId())
				.forEach(msg -> System.out.println(messageService.formatMessage(msg)));

		//공부방에서 user4가 받은 메시지 조회
		System.out.println("\n[##공부방에서 user4가 받은 메시지]");
		messageService.getMessagesByReceiverInChannel(studyRoom.getId(), user4.getId())
				.forEach(msg -> System.out.println(messageService.formatMessage(msg)));

		System.out.println("\n[##user3 탈퇴 처리]");
		userService.delete(user3.getId());

		//공지방 전체 메시지 조회 (user3가 요청자라고 가정)
		System.out.println("\n[##공지방 전체 메시지 조회]");
		messageService.findAllByChannelId(noticeRoom.getId())
				.forEach(msg -> System.out.println(messageService.formatMessage(msg)));

		System.out.println("\n[##user1이 공부방에서 보낸 메시지 수정]");
		List<Message> messages = messageService.getMessagesBySenderInChannel(studyRoom.getId(), user1.getId());

		if (!messages.isEmpty()) {
			UUID messageId = messages.get(0).getId(); // 첫 번째 메시지를 대상으로 수정

			// 수정 전 메시지 조회
			messageService.getMessageById(messageId).ifPresent(originalMessage -> {
				String oldContent = originalMessage.getContent(); // 수정 전 내용

				// 메시지 수정
				messageService.updateMessage(
						messageId, new MessageUpdateRequest(user1.getId(), "안녕하세요 저는 user1입니다.(수정)")
				);

				// 수정 후 메시지 조회
				messageService.getMessageById(messageId).ifPresent(updatedMessage -> {
					System.out.println("메시지 수정 완료");
					System.out.println("수정 전: \"" + oldContent + "\"");
					System.out.println("수정 후: \"" + updatedMessage.getContent() + "\"");
				});
			});
		} else {
			System.out.println("수정할 메시지가 없습니다.");
		}

		System.out.println("\n[##user4가 공지방에서 보낸 메시지 삭제]");
		List<Message> user4Msgs = messageService.getMessagesBySenderInChannel(noticeRoom.getId(), user4.getId());
		if (!user4Msgs.isEmpty()) {
			UUID msgId = user4Msgs.get(0).getId();
			messageService.deleteMessage(msgId, user4.getId());
			System.out.println("삭제 완료");
		}

		System.out.println("\n[##user2가 공부방에서 받은 메시지 조회]");
		messageService.getMessagesByReceiverInChannel(studyRoom.getId(), user2.getId())
				.forEach(msg -> System.out.println(messageService.formatMessage(msg)));

		System.out.println("\n[##user2 탈퇴 처리 - 공지방 소유자]");
		userService.delete(user2.getId());

		System.out.println("\n[##공지방 채널소유주인 user2가 탈퇴후 공지방 채널 메시지 조회 시도]");
		try {
			List<Message> selectMsg = messageService.findAllByChannelId(noticeRoom.getId());
			selectMsg.forEach(msg -> System.out.println(messageService.formatMessage(msg)));
		} catch (IllegalArgumentException e) {
			System.out.println("예외 발생: " + e.getMessage()); // 예상: 채널이 존재하지 않습니다.
		} catch (SecurityException e) {
			System.out.println("예외 발생: " + e.getMessage()); // 예상: 채널에 접근할 수 있는 권한이 없습니다.
		}
	}
}

