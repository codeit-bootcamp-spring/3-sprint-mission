package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.dto.channel.*;
import com.sprint.mission.discodeit.entity.dto.message.MessageCreateRequestDto;
import com.sprint.mission.discodeit.entity.dto.message.MessageDeleteRequestDto;
import com.sprint.mission.discodeit.entity.dto.message.MessageResponseDto;
import com.sprint.mission.discodeit.entity.dto.message.MessageUpdateRequestDto;
import com.sprint.mission.discodeit.entity.dto.user.UserCreateDto;
import com.sprint.mission.discodeit.entity.dto.user.UserUpdateRequestDto;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
public class DiscodeitApplication {

	@Value("${discodeit.repository.file-directory}")
	private String fileDirectory;

	public void createRepositoryDirectory() {
		File directory = new File(fileDirectory);
		if (!directory.exists()) {
			boolean result = directory.mkdirs();
			if (result) {
				System.out.println("디렉토리 생성 완료: " + fileDirectory);
			} else {
				System.err.println("디렉토리 생성 실패: " + fileDirectory);
			}
		} else {
			System.out.println("디렉토리 이미 존재함: " + fileDirectory);
		}
	}

	public static void main(String[] args) {
		ApplicationContext context = SpringApplication.run(DiscodeitApplication.class, args);

		DiscodeitApplication app = context.getBean(DiscodeitApplication.class);
		app.createRepositoryDirectory();

		UserService userService = context.getBean(UserService.class);
		ChannelService channelService = context.getBean(ChannelService.class);
		MessageService messageService = context.getBean(MessageService.class);

		Path path = Path.of("test.png");
		byte[] imageBytes;
        try {
			imageBytes = Files.readAllBytes(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        UserCreateDto userCreateDto = new UserCreateDto("1234", "kangho", imageBytes);
		UserCreateDto userCreateDto2 = new UserCreateDto("1234", "test");
		UserCreateDto userCreateDto3 = new UserCreateDto("1234", "철수");

		User user = userService.createUser(userCreateDto);
		User user2 = userService.createUser(userCreateDto2);
		User user3 = userService.createUser(userCreateDto3);

		// 유저 단건 조회
		userService.getUser(user2.getId());

		// 전체 유저 조회
		userService.getAllUsers();

		// 유저 비밀번호 수정
		UserUpdateRequestDto userUpdateRequestDto = new UserUpdateRequestDto(user.getId(), user.getUserName(), "1234", "111111", null);
		userService.modifyUser(userUpdateRequestDto);

		// 수정된 데이터 조회
		userService.getUser(user.getId());

		// 채널 생성
		ChannelCreateDto channelCreateDto = new ChannelCreateDto(user,"코드잇","codeit");
		ChannelCreateDto channelCreateDto2 = new ChannelCreateDto(user2,"테스트","FileIO");

		ChannelCreatePrivateDto channelCreatePrivateDto = new ChannelCreatePrivateDto(user);
		ChannelCreatePrivateDto channelCreatePrivateDto3 = new ChannelCreatePrivateDto(user);
		ChannelCreatePrivateDto channelCreatePrivateDto2 = new ChannelCreatePrivateDto(user2);

		Channel channel = channelService.createChannel(channelCreateDto);
		Channel channel2 = channelService.createChannel(channelCreateDto2);

		Channel channel3 = channelService.createChannel(channelCreatePrivateDto);
		Channel channel4 = channelService.createChannel(channelCreatePrivateDto2);
		Channel channel5 = channelService.createChannel(channelCreatePrivateDto3);

		GetPublicChannelRequestDto getPublicChannelRequestDto = new GetPublicChannelRequestDto(channel.getId());
		GetPrivateChannelRequestDto getPrivateChannelRequestDto = new GetPrivateChannelRequestDto(channel3.getId());

		channelService.joinChannel(channel, user2);
		channelService.joinChannel(channel, user3);
		channelService.joinChannel(channel2, user);
		channelService.joinChannel(channel5, user2);
		channelService.joinChannel(channel5, user3);

		channelService.findAllByUserId(user.getId());

		// 채널 수정
		// 채널의 주인이 아닌경우
		ChannelUpdateRequestDto channelUpdateRequestDto = new ChannelUpdateRequestDto(user.getId(), "수정한 채널 설명", channel.getId(), "수정한 채널 명");
		ChannelUpdateRequestDto channelUpdateRequestDto2 = new ChannelUpdateRequestDto(user2.getId(), "수정한 채널 설명", channel.getId(), "수정한 채널 명");
		channelService.modifyChannel(channelUpdateRequestDto);

		// 채널의 주인인 경우
		channelService.modifyChannel(channelUpdateRequestDto2);


		channelService.findAllByUserId(user.getId());

		// 채널 강퇴
		// 채널의 주인이 아닐경우
//        log("유저 강퇴", () -> channelService.kickOutChannel(channel.getId(), user, user2));

		// 채널의 주인일 경우
		channelService.kickOutChannel(channel.getId(), user2, user);

		System.out.println("--------강퇴 이후 채널 유저------");
		channelService.getChannel(getPrivateChannelRequestDto);

//		channelService.getChannel(channel.getId())
//				.ifPresent(ch -> logAll("채널 유저", ch.getMembers(), System.out::println));
//
//		channelService.getChannel(channel2.getId())
//				.ifPresent(ch -> logAll("채널 유저", ch.getMembers(), System.out::println));

		// 메세지 보내기

		List<byte[]> messageFiles = new ArrayList<>();
		byte[] file = "file Data 1".getBytes();
		byte[] file2 = "file Data 2".getBytes();
		byte[] file3 = "file Data 3".getBytes();
		messageFiles.add(file);
		messageFiles.add(file2);
		messageFiles.add(file3);



		MessageCreateRequestDto messageCreateRequestDto = new MessageCreateRequestDto(channel.getId(), user.getId(), "안녕하세요", messageFiles);
		MessageCreateRequestDto messageCreateRequestDto2 = new MessageCreateRequestDto(channel.getId(), user2.getId(), "hello");

		MessageResponseDto message = messageService.createMessage(messageCreateRequestDto);
		MessageResponseDto message2 = messageService.createMessage(messageCreateRequestDto2);

		messageService.getChannelMessages(channel.getId());

		MessageUpdateRequestDto messageUpdateRequestDto = new MessageUpdateRequestDto(message.getMessageId(), "수정할 내용", channel.getId(), user.getId());

		messageService.updateMessage(messageUpdateRequestDto);

		messageService.getChannelMessages(channel.getId());

		// 메세지 삭제
		// 본인 외 메세지 삭제일 경우
//        log("메세지 삭제", () -> messageService.deleteMessage(channel, message2, user));

		// 본인이 작성한 메세지일 경우
		MessageDeleteRequestDto messageDeleteRequestDto = new MessageDeleteRequestDto(message.getMessageId(), channel.getId(), user.getId());
		messageService.deleteMessage(messageDeleteRequestDto);
//		log("메세지 삭제", () -> messageService.deleteMessage(messageDeleteRequestDto));

		messageService.getChannelMessages(channel.getId());

		// 채널 삭제
//		log("채널 삭제", () -> channelService.deleteChannel(channel.getId(), user));

//		log("채널 삭제 후", () -> channelService.getChannel(channel.getId()));

		channelService.findAllByUserId(user.getId());

		List<ChannelResponseDto> allByUserId = channelService.findAllByUserId(user.getId());

		channelService.findAllByUserId(user2.getId());
	}

}
