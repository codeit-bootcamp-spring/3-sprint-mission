package com.sprint.mission.discodeit;

import java.util.Optional;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import com.sprint.mission.discodeit.dto.channel.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.message.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.user.UserCreateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.dto.data.UserDto;

@SpringBootApplication
public class DiscodeitApplication {
	static User setupUser(UserService userService) {
		UserCreateRequest request = new UserCreateRequest("woody1", "woody@codeit1.com", "woody01234");
		User user = userService.createUser(request, Optional.empty());
		return user;
	}

	static Channel setupChannel(ChannelService channelService) {
		PublicChannelCreateRequest request = new PublicChannelCreateRequest("publicChannelName","publicChannelPassword",null);
		Channel channel = channelService.createChannel(request);
		return channel;
	}

	static Message messageCreateTest(MessageService messageService, Channel channel, User author) {
		MessageCreateRequest request = new MessageCreateRequest("안녕하세요. 반갑습니다.", channel.getChannelId(), author.getUserId(), null);
		Message message = messageService.createMessage(request);
		System.out.println("메시지 생성: " + message.getMessageId());
		return message;
	}

	public static void main(String[] args) {

		ConfigurableApplicationContext context = SpringApplication.run(DiscodeitApplication.class, args);
		// 서비스 초기화
		UserService userService = context.getBean(UserService.class);
		ChannelService channelService = context.getBean(ChannelService.class);
		MessageService messageService = context.getBean(MessageService.class);

		// 테스트를 위해 이미 존재하는 사용자 삭제 시도
		String testUserEmail = "woody@codeit1.com";
		try {
			UserDto existingUser = userService.getUserByEmail(testUserEmail);
			userService.deleteUser(existingUser.id());
			System.out.println("Existing user with email " + testUserEmail + " deleted.");
		} catch (IllegalArgumentException e) {
			// 사용자가 존재하지 않으면 삭제할 필요 없음
			System.out.println("User with email " + testUserEmail + " does not exist or could not be deleted. " + e.getMessage());
		} catch (Exception e) {
			System.err.println("Error deleting existing user with email " + testUserEmail + ". " + e.getMessage());
		}

		// 셋업
		User user = setupUser(userService);
		Channel channel = setupChannel(channelService);
		// 테스트
		Message message = messageCreateTest(messageService, channel, user);

		// 생성된 객체 정보 출력 (선택 사항)
		System.out.println("(사용자 생성) 사용자ID: " + user.getUserId() + " 이름: " + user.getUsername() + " 이메일: " + user.getEmail() + " 비밀번호: " + user.getPassword());
		System.out.println("(채널 생성) 채널ID: " + channel.getChannelId() + " 이름: " + channel.getChannelName() + " 비밀번호: " + channel.getPassword());
		System.out.println("(메시지 생성) 메시지ID: " + message.getMessageId() + " 내용: " + message.getContent() + " 채널ID: " + message.getChannelId() + " 작성자ID: " + message.getAuthorId());

		// 생성된 파일 데이터 삭제
		try {
			messageService.deleteMessage(message.getMessageId());
			System.out.println("(메시지 삭제) 채널ID: " + message.getChannelId() + "삭제한 메시지ID: " + message.getMessageId());
		} catch (Exception e) {
			System.err.println("메시지 삭제 실패: " + e.getMessage());
		}

		try {
			channelService.deleteChannel(channel.getChannelId());
			System.out.println("(채널 삭제) 채널ID: " + channel.getChannelId());
		} catch (Exception e) {
			System.err.println("채널 삭제 실패: " + e.getMessage());
		}

		try {
			userService.deleteUser(user.getUserId());
			System.out.println("(사용자 삭제) 사용자ID: " + user.getUserId());
		} catch (Exception e) {
			System.err.println("사용자 삭제 실패: " + e.getMessage());
		}

		context.close(); // 애플리케이션 컨텍스트 종료
	}
}
