package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.request.*;
import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.ReadStatusService;
import com.sprint.mission.discodeit.service.UserService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@SpringBootApplication
public class DiscodeitApplication {

	public static void main(String[] args) {
		ConfigurableApplicationContext context = SpringApplication.run(DiscodeitApplication.class, args);

		// Spring Context에서 Bean 조회
		UserService userService = context.getBean(UserService.class);
		ChannelService channelService = context.getBean(ChannelService.class);
		MessageService messageService = context.getBean(MessageService.class);
		ReadStatusService readStatusService = context.getBean(ReadStatusService.class);

		// === 유저 CRUD 테스트 ===
		//User test01 = userService.createUser("test01", "test01@.com");
		User test01 = userService.createUser(new UserCreateRequest("test01", "test01@.com","1234"), Optional.empty());

		UserCreateRequest test02userRequest = new UserCreateRequest("test02", "test02@.com","12345");
		byte[] content = {123}; // 파일 바이트
		BinaryContentCreateRequest test02profileRequest = new BinaryContentCreateRequest("123.jpg", content, "image/jpeg");
		User test02 = userService.createUser(test02userRequest, Optional.of(test02profileRequest));

		//User test03 = userService.createUser("test03", "test03@.com");
		User test03 = userService.createUser(new UserCreateRequest("test03", "test03@.com","1234"), Optional.empty());


		userService.find(test01.getId()).ifPresentOrElse(userDTO -> {
			System.out.println("===개별 User 조회 ===\n" + userDTO);

			System.out.println("\n=== test01 이름변경 + 프로필이미지 추가 후 조회 ===");
			//userService.updateUserName(userDTO.getId(), "test0101");
			UserUpdateRequest updateRequest = new UserUpdateRequest("test0101", "test01@.com");
			byte[] newImageContent = {42, 24, 55};  // 새로운 이미지 바이트
			BinaryContentCreateRequest profileImageRequest =
					new BinaryContentCreateRequest("profile_test01.jpg", newImageContent, "image/jpeg");
			userService.update(test01.getId(), updateRequest, Optional.of(profileImageRequest));

			//System.out.println(userService.find(userDTO.getId()).orElse(null));
			System.out.println(userService.find(test01.getId()).orElse(null));
		}, () -> {
			System.out.println("test01 유저를 찾을 수 없습니다.");
		});

		System.out.println("\n=== 전체 유저 조회 ===");
		userService.findAll().forEach(System.out::println);

		// test02 삭제 및 조회
		userService.delete(test02.getId());
		System.out.println("\n=== test02 삭제 후 전체 조회 ===");
		userService.findAll().forEach(System.out::println);
		System.out.println();

		System.out.println("===============================================================================================");

		// test01이 public채널 생성 & test03이 private채널 생성
		//Channel channel1 = channelService.create("2025_Channel", test01);
		PublicChannelCreateRequest test01publicRequest = new PublicChannelCreateRequest("2025_Channel", test01.getId());
		Channel channel1 = channelService.create(test01publicRequest);

		//Channel channel2 = channelService.create("2024_Channel", test02);
		List<UUID> memberIds = new ArrayList<>();
		memberIds.add(test01.getId());
		PrivateChannelCreateRequest privateRequest = new PrivateChannelCreateRequest("2024_Channel", test03.getId(), memberIds);
		Channel channel2 = channelService.create(privateRequest);

		// 개별 채널 조회
		System.out.println("\n=== 개별 채널 조회 ===");
		channelService.find(channel2.getId()).ifPresentOrElse(
				channel -> System.out.println(channel),
				() -> System.out.println("해당 채널이 존재하지 않습니다.")
		);

		System.out.println("\n=== 채널 이름(2024_Channel) 수정 후 전체 채널 조회 ===");
		PublicChannelUpdateRequest updateRequest = new PublicChannelUpdateRequest("2023_channel");
		channelService.update(channel2.getId(), updateRequest);
		try {
			channelService.update(channel2.getId(), updateRequest);
		} catch (IllegalArgumentException e) {
			System.out.println("[채널명 변경 실패: " + e.getMessage() + "]\n");
		}
		List<ChannelDto> allChannels = channelService.findAllByUserId(test01.getId());
		allChannels.forEach(channelDto -> System.out.println(channelDto));

		// 유저 추가 (test02, test03을 채널에 추가)  & 삭제된 유저는 data에 담을 수 없다.
		System.out.println("\n=== 2025_Channel에 멤버(test02(탈퇴),test03) 추가 ===");
		try {
			channelService.addMember(channel1.getId(), test02.getId()); // test02는 삭제된 유저
		} catch (IllegalArgumentException e) {
			System.out.println("예외 발생: " + e.getMessage());
		}
		channelService.addMember(channel1.getId(), test03.getId());

		// 채널 멤버 조회
		System.out.println("\n=== 2025_Channel 전체멤버(채널소유자 포함) 조회 ===");
		List<User> channelMembers = channelService.getChannelMembers(channel1.getId());
		channelMembers.forEach(user -> System.out.println("멤버: " + user.getUsername()));

		// 유저 제거 테스트 (test03 제거)
		System.out.println("\n=== 2025_Channel에서 멤버 제거(test03) 후 조회 ===");
		channelService.removeMember(channel1.getId(), test03.getId());
		channelService.getChannelMembers(channel1.getId()).forEach(user -> System.out.println("멤버: " + user.getUsername()));

		// 채널 삭제 후 전체 채널 조회
		System.out.println("\n=== 2025_Channel 삭제 후 남은 채널 조회 ===");
		channelService.deleteChannel(channel1.getId());
		List<ChannelDto> totalChannels = channelService.findAllByUserId(test01.getId());
		if (totalChannels.isEmpty()) {
			System.out.println("남은 채널 없음");
		} else {
			totalChannels.forEach(channel -> System.out.println(channel));
		}

		System.out.println("\n============================================\n[##유저+채널+메시지 crud 테스트]====================\n");

		User user1 = userService.createUser(
				new UserCreateRequest("user1", "test01@.abc", "1234"),
				Optional.empty()
		);

		byte[] content3 = {123,68,12};
		User user2 = userService.createUser(
				new UserCreateRequest("user2", "test02@.abc", "12345"),
				Optional.of(new BinaryContentCreateRequest("123.jpg", content3, "image/jpeg"))
		);

		User user3 = userService.createUser(
				new UserCreateRequest("user3", "test03@.abc", "asd"),
				Optional.empty()
		);

		User user4 = userService.createUser(
				new UserCreateRequest("user4", "test04@.abc", "134"),
				Optional.empty()
		);


		//Channel studyRoom = channelService.createChannel("공부방", user1);
		PublicChannelCreateRequest user1publicRequest = new PublicChannelCreateRequest("공부방", user1.getId());

		Channel studyRoom = channelService.create(user1publicRequest);
		channelService.addMember(studyRoom.getId(), user2.getId());
		channelService.addMember(studyRoom.getId(), user3.getId());
		channelService.addMember(studyRoom.getId(), user4.getId());

		//Channel noticeRoom = channelService.createChannel("공지방", user2);
		List<UUID> member_Ids = List.of(user1.getId(), user3.getId(), user4.getId());
		PrivateChannelCreateRequest private_Request = new PrivateChannelCreateRequest("공지방", user2.getId(), member_Ids);

		Channel noticeRoom = channelService.create(private_Request);


		byte[] content1 = {1, 2, 3};
		byte[] content2 = {4, 5, 6};
		BinaryContentCreateRequest file1 = new BinaryContentCreateRequest("123.jpg", content1, "image/jpeg");
		BinaryContentCreateRequest file2 = new BinaryContentCreateRequest("456.pdf", content2, "pdf");

		messageService.createMessage(
				new MessageCreateRequest(studyRoom.getId(), user1.getId(), "안녕하세요 저는 user1입니다. 첨부파일 0개."),
				null
		);

		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		messageService.createMessage(
				new MessageCreateRequest(studyRoom.getId(), user2.getId(), "user2 입니다. 첨부파일 1개"),
				List.of(file1)
		);

		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		messageService.createMessage(
				new MessageCreateRequest(studyRoom.getId(), user3.getId(), "user3 입니다. 첨부파일 2개"),
				List.of(file1, file2)
		);

		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		messageService.createMessage(
				new MessageCreateRequest(noticeRoom.getId(), user4.getId(), "안녕하세요 저는 user4입니다. 첨부파일 0개."),
				null
		);

		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		messageService.createMessage(
				new MessageCreateRequest(noticeRoom.getId(), user1.getId(), "공지없습니다. 첨부파일 0개."),
				null
		);

		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}


		System.out.println("[## user1의 readStatus 수정 전 lastReadAt 조회]"); // lastReadAt 기본값을 Instant.EPOCH로 설정
		List<ReadStatus> user1StatusesBeforeUpdate = readStatusService.findAllByUserId(user1.getId());
		for (ReadStatus rs : user1StatusesBeforeUpdate) {
			if (rs.getChannelId().equals(noticeRoom.getId())) {
				System.out.println("user1의 lastReadAt마지막 읽은 시간: " + rs.getLastReadAt());
			}
		}

		List<ReadStatus> user1Statuses = readStatusService.findAllByUserId(user1.getId());
		for (ReadStatus rs : user1Statuses) {
			if (rs.getChannelId().equals(noticeRoom.getId())) {
				readStatusService.update(rs.getId(), new ReadStatusUpdateRequest(Instant.now()));
			}
		}

		System.out.println("\n[## user1의 readStatus 수정 후 lastReadAt 조회]");
		List<ReadStatus> user1StatusesAfterUpdate = readStatusService.findAllByUserId(user1.getId());
		for (ReadStatus rs : user1StatusesAfterUpdate) {
			if (rs.getChannelId().equals(noticeRoom.getId())) {
				System.out.println("마지막 읽은 시간: " + rs.getLastReadAt());
			}
		}

		System.out.println("\n[##user1의 readStatus 조회 및 마지막으로 읽은 메시지 출력]");

		List<ReadStatus> user1ReadStatuses = readStatusService.findAllByUserId(user1.getId());

		for (ReadStatus rs : user1ReadStatuses) {
			UUID channelId = rs.getChannelId();
			Optional<ChannelDto> optionalChannel = channelService.find(channelId);

			if (optionalChannel.isEmpty()) continue;

			ChannelDto channelDto = optionalChannel.get();

			if (channelDto.channelType() != ChannelType.PRIVATE) continue;

			Instant lastReadAt = rs.getLastReadAt();
			String channelName = channelDto.name();

			List<Message> messages = messageService.findAllByChannelId(channelId).stream()
					.filter(m -> !m.getCreatedAt().isAfter(lastReadAt))
					.sorted(Comparator.comparing(Message::getCreatedAt).reversed())
					.collect(Collectors.toList());

			if (!messages.isEmpty()) {
				Message latestReadMessage = messages.get(0);
				String formatted = messageService.formatMessage(latestReadMessage);
				System.out.println("[" + channelName + "] " + formatted);
			} else {
				System.out.println("[" + channelName + "] 읽은 메시지가 없습니다.");
			}
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

