package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.Dto.authService.LoginRequest;
import com.sprint.mission.discodeit.Dto.authService.LoginResponse;
import com.sprint.mission.discodeit.Dto.binaryContent.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.Dto.binaryContent.BinaryContentCreateResponse;
import com.sprint.mission.discodeit.Dto.channel.*;
import com.sprint.mission.discodeit.Dto.message.*;
import com.sprint.mission.discodeit.Dto.readStatus.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.Dto.readStatus.ReadStatusCreateResponse;
import com.sprint.mission.discodeit.Dto.readStatus.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.Dto.user.UserCreateRequest;
import com.sprint.mission.discodeit.Dto.user.UserCreateResponse;
import com.sprint.mission.discodeit.Dto.user.UserFindResponse;
import com.sprint.mission.discodeit.Dto.userStatus.*;
import com.sprint.mission.discodeit.entity.*;
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
		SpringApplication.run(DiscodeitApplication.class, args);

//		ApplicationContext context = SpringApplication.run(DiscodeitApplication.class, args);
//		UserService userService = context.getBean("basicUserService", UserService.class);
//		ChannelService channelService = context.getBean("basicChannelService", ChannelService.class);
//		MessageService messageService = context.getBean("basicMessageService", MessageService.class);
//		AuthService authService = context.getBean(AuthService.class);
//		ReadStatusService readStatusService = context.getBean("basicReadStatusService", ReadStatusService.class);
//		UserStatusService userStatusService = context.getBean("basicUserStatusService", UserStatusService.class);
//		BinaryContentService binaryContentService = context.getBean("basicBinaryContentService", BinaryContentService.class);
//
//		// 빈 주입------------------------------------------------------------------------------------------------------------
//
//		try {
//			System.out.println("\n\nUserService = " + userService);
//			byte[] attachment = {1, 2, 3};
//			byte[] attachment2 = {4, 5, 6};
//
//
//			UserCreateRequest userCreateRequest = new UserCreateRequest("user", "user@gmail.com", "pw");
//			UserCreateRequest userCreateRequest1 = new UserCreateRequest("user1", "user1@gmail.com", "pw");
//			UserCreateRequest userCreateRequest2 = new UserCreateRequest("user2", "user2@gmail.com", "pw", attachment);
//			UserCreateRequest userCreateRequest3 = new UserCreateRequest("user3", "user3@gmail.com", "pw", attachment);
//
//			UserCreateResponse userCreateResponse = userService.create(userCreateRequest);
//			UserCreateResponse userCreateResponse1 = userService.create(userCreateRequest1);
//			System.out.println("userService.create() - 프로필 없음: OK");
//
//			UserCreateResponse userCreateResponse2 = userService.create(userCreateRequest2);
//			UserCreateResponse userCreateResponse3 = userService.create(userCreateRequest3);
//			System.out.println("userService.create() - 프로필 있음: OK");
//
//			try{
//				UserCreateRequest userCreateRequest400 = new UserCreateRequest("user", "user@gmail.com", "pw");
//				userService.create(userCreateRequest400);
//				System.out.println("예외 처리 중복 유저 생성: WRONG");
//			} catch (Exception e) {
//				System.out.println("예외 확인 : OK");
//			}
//
//			if (userStatusService.findAll().size() == 4) {
//				System.out.println("userStatus 자동 생성 확인 : OK");
//			} else {
//				System.out.println("userStatus 자동 생성 확인 : WRONG");
//			}
//
//			try{
//				binaryContentService.find(userCreateResponse2.profileId());
//				binaryContentService.find(userCreateResponse3.profileId());
//				System.out.println("binaryContent 자동생성 : OK");
//			} catch (Exception e) {
//				System.out.println("binaryContent 자동생성 : WRONG");
//			}
//
//			UserFindResponse userFindResponse = userService.findUserById(userCreateResponse.id());
//			UserFindResponse userFindResponse1 = userService.findUserById(userCreateResponse.id());
//			System.out.println("userService.findUserById() : OK");
//			if ((userFindResponse.isOnline()) && (userFindResponse1.isOnline())) System.out.println("유저 온라인 여부 확인 : OK");
//
//			if (userService.findAllUsers().size() == 4) {
//				System.out.println("userService.findAllUsers() : OK");
//			} else {
//				System.out.println("userService.findAllUsers() : WRONG");
//			}
//			ProfileUploadRequest profileUploadRequest1 = new ProfileUploadRequest(userCreateResponse1.id(), attachment2);
//			System.out.println("userService.updateImage() - 프로필 없음 : OK");
//			ProfileUploadRequest profileUploadRequest2 = new ProfileUploadRequest(userCreateResponse2.id(), attachment2);
//			System.out.println("userService.updateImage() - 프로필 있음 : OK");
//
//			if ((profileUploadRequest1.image() == attachment2) && (profileUploadRequest2.image() == attachment2)) {
//				System.out.println("바뀐 프로필 확인: OK");
//			} else {
//				System.out.println("바뀐 프로필 확인: WRONG");
//			}
//			UUID profileId = userCreateResponse3.profileId();
//			userService.deleteUser(userCreateResponse3.id());
//			System.out.println("userService.deleteUser() : OK");
//			if(userStatusService.findAll().size()!=3) System.out.println("userStatus 자동 삭제: WRONG");
//
//			try{
//				binaryContentService.find(profileId);
//				System.out.println("userStatus 자동 삭제: WRONG");
//			} catch (Exception e) {
//				System.out.println("userStatus 자동 삭제: OK");
//			}
//
//
//			System.out.println("UserService 테스트 종료\n");
//			// --------------------------------------------------------------------------------------------------------
//			System.out.println("authService = " + authService);
//			LoginRequest loginRequest = new LoginRequest("user", "pw");
//			LoginRequest loginRequest2 = new LoginRequest("WrongUser", "pw");
//
//			authService.login(loginRequest);
//			System.out.println("authService.login() : OK");
//			try {
//				authService.login(loginRequest2);
//			} catch (Exception e) {
//				System.out.println("예외 확인 : OK");
//			}
//
//			System.out.println("AuthService 테스트 종료\n");
//
//			// --------------------------------------------------------------------------------------------------------
//
//			System.out.println("channelService = " + channelService);
//			PrivateChannelCreateRequest privateChannelCreateRequest = new PrivateChannelCreateRequest(
//					Set.of(userCreateResponse.id(), userCreateResponse1.id())
//			);
//			ChannelCreateResponse privateChannel = channelService.createChannel(privateChannelCreateRequest); // channel
//			System.out.println("channelService.privateChannelCreateRequest() - private : OK");
//
//
//			if ((!privateChannel.getName().isBlank()) || (!privateChannel.getDescription().isBlank())) {
//				System.out.println("private channel 요구사항 확인: WRONG");
//			}
//			PublicChannelCreateRequest publicChannelCreateRequest = new PublicChannelCreateRequest (
//					Set.of(userCreateResponse.id(), userCreateResponse2.id()), "public channel for 0 n 2", "this is public channel made by 0 n 2"
//			);
//			ChannelCreateResponse publicChannel = channelService.createChannel(publicChannelCreateRequest);  // channel
//			if ((publicChannel.getName().isBlank()) && (publicChannel.getDescription().isBlank())) {
//				System.out.println("public channel 요구사항 확인: WRONG");
//			}
//			System.out.println("channelService.publicChannelCreateRequest() - public : OK");
//
//			ChannelFindRequest channelFindRequest = new ChannelFindRequest(privateChannel.getId());
//			ChannelFindRequest channelFindRequest1 = new ChannelFindRequest(publicChannel.getId());
//			ChannelFindResponse privateChannelFindResponse = channelService.find(channelFindRequest); // private find response
//			ChannelFindResponse publicChannelFindResponse = channelService.find(channelFindRequest1); // public find response
//			System.out.println("channelService.find() : OK");
//			if ((privateChannelFindResponse.getRecentMessageTime() != null) && (publicChannelFindResponse.getRecentMessageTime() != null)) {
//				System.out.println("최근 메세지 시간정보 확인 : WRONG");
//			}
//			if (privateChannelFindResponse.getUserIds().size() <= 0) {
//				System.out.println("private channel 유저id 확인: WRONG");
//			}
//			if (publicChannelFindResponse.getUserIds() != null) {
//				System.out.println("public channel 유저id 확인: WRONG");
//			}
//
//			ChannelFindByUserIdRequest channelFindByUserIdRequest = new ChannelFindByUserIdRequest(userCreateResponse.id());
//			ChannelFindByUserIdRequest channelFindByUserIdRequest2 = new ChannelFindByUserIdRequest(userCreateResponse2.id());
//			if(channelService.findAllByUserId(channelFindByUserIdRequest).size()==2) {
//				System.out.println("channelService.findAllByUserId() - private, public : OK");
//			}else{
//				System.out.println("channelService.findAllByUserId() - private, public : WRONG");
//			}
//
//			if(channelService.findAllByUserId(channelFindByUserIdRequest2).size()==1) {
//				System.out.println("channelService.findAllByUserId() - public only : OK");
//			} else{
//				System.out.println("channelService.findAllByUserId() - public only : WRONG");
//			}
//			ChannelUpdateRequest channelUpdateRequest = new ChannelUpdateRequest(privateChannel.getId(), "new name for test");
//			ChannelUpdateRequest channelUpdateRequest2 = new ChannelUpdateRequest(publicChannel.getId(), "new name for test");
//
//			try{ //private의 이름을 수정 하는데 private은 이름이 없어야 하니까 throw exception
//				channelService.update(channelUpdateRequest);
//				System.out.println("channelService.update() - private 예외발생 실패 : WRONG");
//			} catch (Exception e) {
//				System.out.println("channelService.update() - private : OK");
//			}
//
//			channelService.update(channelUpdateRequest2);
//			System.out.println("channelService.update() - public : OK");
//			if (!channelService.find(channelFindRequest1).getChannel().getName().equals("new name for test")) {
//				System.out.println("이름 변경 확인 : WRONG");
//			}
//			// 삭제 확인을 위한 메세지 생성
//			MessageCreateRequest messageCreateRequest = new MessageCreateRequest(userCreateResponse.id(), privateChannel.getId(), "my first Message");
//			MessageCreateResponse message = messageService.createMessage(messageCreateRequest);
//			channelService.deleteChannel(privateChannel.getId());
//			System.out.println("channelService.deleteChannel() : OK");
//
//			if (readStatusService.findAllByUserId(privateChannel.getId()).isEmpty()) {
//				System.out.println("readStatus 삭제 확인 : OK");
//			} else {
//				System.out.println("readStatus 삭제 확인 : WRONG");
//			}
//
//			try {
//				messageService.findMessageById(message.id());
//				System.out.println("메세지 삭제 확인 : WRONG");
//			} catch (Exception e) {
//				System.out.println("메세지 삭제 확인 : OK");
//			}
//			System.out.println("ChannelService 테스트 종료\n");
//
//			// --------------------------------------------------------------------------------------------------------
//
//			System.out.println("messageService = " + messageService);
//			MessageCreateRequest messageCreateRequest1 = new MessageCreateRequest(userCreateResponse.id(), publicChannel.getId(), "my second Message");
//			MessageAttachmentsCreateRequest messageAttachmentsCreateRequest = new MessageAttachmentsCreateRequest(
//					userCreateResponse.id(), publicChannel.getId(), List.of(attachment)
//			);
//
//			MessageCreateResponse message1 = messageService.createMessage(messageCreateRequest1);
//			System.out.println("messageService.createMessage() - content : OK");
//			MessageAttachmentsCreateResponse message2 = messageService.createMessage(messageAttachmentsCreateRequest);
//			System.out.println("messageService.createMessage() - attachment : OK");
//
//			if (messageService.findAllByChannelId(publicChannel.getId()).size()==2) {
//				System.out.println("메세지 개수 확인 : OK");
//			} else {
//				System.out.println("메세지 개수 확인 : WRONG");
//			}
//
//			MessageUpdateRequest messageUpdateRequest = new MessageUpdateRequest(message1.id(), "updated new message");
//			messageService.updateMessage(messageUpdateRequest);
//			System.out.println("messageService.updateMessage() : OK");
//
//			messageService.deleteMessage(message2.id());
//			System.out.println("messageService.deleteMessage() : OK");
//
//			try{
//				binaryContentService.find(message2.attachmentIds().get(0));
//				System.out.println("binaryContent 자동 삭제 : WRONG");
//			} catch (Exception e) {
//				System.out.println("binaryContent 자동 삭제 : OK");
//			}
//
//			System.out.println("MessageService 테스트 종료\n");
//
//			// --------------------------------------------------------------------------------------------------------
//
//			System.out.println("userStatusService = " + userStatusService);
//
//			UserCreateRequest tempRequest = new UserCreateRequest("temp", "temp@gmail.com", "pw");
//			UserCreateResponse tempUser = userService.create(tempRequest);
//
//			UserStatus tempUserStatus = userStatusService.find(tempUser.userStatusId());
//			System.out.println("userStatusService.find() : OK");
//
//			userStatusService.findAll();
//			System.out.println("userStatusService.findAll() : OK");
//
//			Instant before = tempUserStatus.getUpdatedAt();
//			UserStatusUpdateRequest userStatusUpdateRequest = new UserStatusUpdateRequest(tempUserStatus.getId(), Instant.now());
//			userStatusService.update(userStatusUpdateRequest);
//			System.out.println("userStatusService.update() : OK");
//
//			UserStatus userStatus = userStatusService.find(tempUserStatus.getId());
//			if (userStatus.getUpdatedAt().isAfter(before)) {
//				System.out.println("update 시간 수정 확인: OK");
//			} else {
//				System.out.println("update 시간 수정 확인: WRONG");
//			}
//
//			Instant after = userStatus.getUpdatedAt();
//			userStatusService.updateByUserId(userStatus.getUserId(), Instant.now());
//			UserStatus neoUserStatus = userStatusService.find(tempUserStatus.getId());
//			if (neoUserStatus.getUpdatedAt().isAfter(after)) {
//				System.out.println("updateByUserId 시간 수정 확인: OK");
//			} else {
//				System.out.println("updateByUserId 시간 수정 확인: WRONG");
//			}
//
//			userStatusService.delete(tempUser.userStatusId());
//			System.out.println("userStatusService.delete() : OK");
//
//			UserStatusCreateRequest userStatusCreateRequest =
//					new UserStatusCreateRequest(tempUser.id());
//			userStatusService.create(userStatusCreateRequest);
//			System.out.println("userStatusService.create() : OK");
//
//			System.out.println("UserStatusService 테스트 종료\n");
//
//			// --------------------------------------------------------------------------------------------------------
//
//			System.out.println("readStatusService = " + readStatusService);
//
//
//			ReadStatusCreateRequest readStatusCreateRequest =
//					new ReadStatusCreateRequest(userCreateResponse2.id(), publicChannel.getId());
//			ReadStatusCreateResponse readStatusCreateResponse = readStatusService.create(readStatusCreateRequest);
//			System.out.println("readStatusService.create() : OK");
//
//			readStatusService.findById(readStatusCreateResponse.id());
//			System.out.println("readStatusService.findById() : OK");
//
//			readStatusService.findAllByUserId(userCreateResponse2.id());
//			System.out.println("readStatusService.findAllByUserId() : OK");
//
//			ReadStatus readStatus = readStatusService.findById(readStatusCreateResponse.id());
//
//			Instant timeBefore = readStatus.getUpdatedAt();
//			ReadStatusUpdateRequest readStatusUpdateRequest = new ReadStatusUpdateRequest(readStatusCreateResponse.id(), Instant.now());
//			readStatusService.update(readStatusUpdateRequest);
//			System.out.println("readStatusService.update() : OK");
//
//			ReadStatus newReadStatus = readStatusService.findById(readStatusCreateResponse.id());
//			if (!newReadStatus.getUpdatedAt().isAfter(timeBefore)) {
//				System.out.println("readStatusService.update() : WRONG");
//			}
//
//			readStatusService.delete(newReadStatus.getId());
//			System.out.println("readStatusService.delete() : OK");
//
//			System.out.println("ReadStatus 테스트 종료\n");
//
//
//			// --------------------------------------------------------------------------------------------------------
//
//			System.out.println("binaryContentService = " + binaryContentService);
//
//			BinaryContentCreateRequest binaryContentCreateRequest = new BinaryContentCreateRequest(attachment);
//			BinaryContentCreateRequest binaryContentCreateRequest1 = new BinaryContentCreateRequest(attachment2);
//
//			BinaryContentCreateResponse binaryContentCreateResponse = binaryContentService.create(binaryContentCreateRequest);
//			BinaryContentCreateResponse binaryContentCreateResponse1 = binaryContentService.create(binaryContentCreateRequest1);
//			System.out.println("binaryContentService.create() : OK");
//
//			BinaryContent binaryContent = binaryContentService.find(binaryContentCreateResponse.id());
//			System.out.println("binaryContentService.find() : OK");
//
//			List<UUID> attachmentIds = List.of(binaryContent.getId(),binaryContentCreateResponse1.id());
//			binaryContentService.findAllByIdIn(attachmentIds);
//			System.out.println("binaryContentService.findAllByIdIn() : OK");
//
//			try{
//				binaryContentService.delete(binaryContentCreateResponse1.id());
//				binaryContentService.find(binaryContentCreateResponse1.id());
//			} catch (Exception e) {
//				System.out.println("binaryContentService.delete() : OK");
//			}
//			System.out.println("BinaryContent 테스트 종료\n");
//
//			System.out.println("\n\n---------------------------------------------\n\n");
//
//		} catch (Exception e) {
//			System.out.println("예기치 못한 오류 발생: "+e);
//		}
	}
}




