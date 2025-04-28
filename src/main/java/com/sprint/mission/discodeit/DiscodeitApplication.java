package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.Dto.channel.ChannelFindResponse;
import com.sprint.mission.discodeit.Dto.channel.ChannelUpdateRequest;
import com.sprint.mission.discodeit.Dto.channel.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.Dto.channel.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.Dto.message.MessageAttachmentsCreateRequest;
import com.sprint.mission.discodeit.Dto.message.MessageCreateRequest;
import com.sprint.mission.discodeit.Dto.user.*;
import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.AuthService;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@SpringBootApplication
public class DiscodeitApplication {

	public static void main(String[] args) {
		ApplicationContext context = SpringApplication.run(DiscodeitApplication.class, args);

		UserService userService = context.getBean("basicUserService", UserService.class);
		ChannelService channelService = context.getBean("basicChannelService", ChannelService.class);
		MessageService messageService = context.getBean("basicMessageService", MessageService.class);
		AuthService authService = context.getBean(AuthService.class);

		BinaryContentRepository binaryContentRepository = context.getBean(BinaryContentRepository.class);
		UserStatusRepository userStatusRepository = context.getBean(UserStatusRepository.class);

		System.out.println("\n=== findAllByUserId 테스트 ===");

		byte[] image = {1, 2, 3, 4};
		byte[] image1 = {1, 2, 3, 4, 5};

		List<byte[]> images = new ArrayList<>();
		images.add(image);
		images.add(image1);


// 1. 유저 생성
		User user1 = userService.createUser(new UserCreateRequest("user1", "user1@email.com", "1234"));
		User user2 = userService.createUser(new UserCreateRequest("user2", "user2@email.com", "1234"));

// 2. 채널 생성
		Set<User> users = Set.of(user1, user2);

// public 채널
		PublicChannelCreateRequest publicRequest = new PublicChannelCreateRequest(users, "Public Channel", "공개 채널");
		Channel publicChannel = channelService.createChannel(publicRequest);

// private 채널 (user1만)
		PrivateChannelCreateRequest privateRequestUser1 = new PrivateChannelCreateRequest(Set.of(user1));
		Channel privateChannelUser1 = channelService.createChannel(privateRequestUser1);

// private 채널 (user2만)
		PrivateChannelCreateRequest privateRequestUser2 = new PrivateChannelCreateRequest(Set.of(user2));
		Channel privateChannelUser2 = channelService.createChannel(privateRequestUser2);

// 3. findAllByUserId 호출
		List<ChannelFindResponse> channelsForUser1 = channelService.findAllByUserId(user1.getId());

// 4. 출력
		System.out.println("User1이 볼 수 있는 채널 목록:");
		for (ChannelFindResponse response : channelsForUser1) {
			System.out.println("- 채널 이름: " + response.getChannel().getName());
			System.out.println("  타입: " + response.getChannel().getType());
			System.out.println("  최근 메시지 시간: " + response.getRecentMessageTime());
			if (response.getUserIds() != null) {
				System.out.println("  참여 유저 IDs:");
				for (UUID id : response.getUserIds()) {
					System.out.println("    * " + id);
				}
			}
			System.out.println();
		}
//
//		System.out.println("이름 업데이트");
//		ChannelUpdateRequest channelUpdateRequest = new ChannelUpdateRequest(publicChannel.getId(), "new name for test update");
//		ChannelUpdateRequest channelUpdateRequest1 = new ChannelUpdateRequest(privateChannelUser1.getId(), "new name for test update");
//		channelService.updateChannelName(channelUpdateRequest);
//		channelService.updateChannelName(channelUpdateRequest1);
//
//		System.out.println("\n=== 채널 삭제 테스트 ===");
//
//		// 1. 삭제 대상 채널 ID
//		UUID deleteTargetChannelId = privateChannelUser1.getId();
//
//		// 2. 삭제 실행
//		System.out.println("Deleting Channel: " + deleteTargetChannelId);
//		channelService.deleteChannel(deleteTargetChannelId);
//
//		// 3. 삭제 검증
//		ChannelFindResponse deletedChannelResponse = null;
//		try {
//			deletedChannelResponse = channelService.findChannel(deleteTargetChannelId);
//		} catch (Exception e) {
//			System.out.println("삭제 확인: 채널을 찾을 수 없습니다. (정상)");
//		}

//		if (deletedChannelResponse == null) {
//			System.out.println("채널 삭제 성공: " + deleteTargetChannelId);
//		} else {
//			System.out.println("채널 삭제 실패: " + deleteTargetChannelId);
//		}


		MessageCreateRequest messageCreateRequest = new MessageCreateRequest(user1.getId(), publicChannel.getId(), "first message");
		MessageAttachmentsCreateRequest messageAttachmentsCreateRequest =
				new MessageAttachmentsCreateRequest(user1.getId(), publicChannel.getId(), images);
		messageService.createMessage(messageCreateRequest);
		Message message1 = messageService.createMessage(messageAttachmentsCreateRequest);

		messageService.findAllMessages().forEach(message -> System.out.println(
				"messageId "+message.getId()+
				"\nchannelId "+message.getChannelId()+
				"\nattachment "+message.getAttachmentIds()+
				"\ncontent "+ message.getContent()+"\n\n"));

		channelService.findAllByUserId(user1.getId())
				.forEach(channelFindResponse ->
						System.out.println(channelFindResponse.getType()+" \nchannelId "+
								channelFindResponse.getChannel().getId()+" \nuserIds "+
								channelFindResponse.getUserIds()+" \nrecentMessageTime "+
								channelFindResponse.getRecentMessageTime()+"\n\n"));

		messageService.deleteMessage(message1.getId());


	}

}


