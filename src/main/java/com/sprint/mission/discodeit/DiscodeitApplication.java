package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.LoginRequest;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.AuthService;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.basic.BasicAuthService;
import java.util.ArrayList;
import java.util.Optional;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class DiscodeitApplication {
	static User setupUser(UserService userService) {
		UserCreateRequest request = new UserCreateRequest("okodee", "okodee@naver.com", "okodee1234");
		User user = userService.create(request, Optional.empty());
		return user;
	}

	static Channel setupChannel(ChannelService channelService) {
		PublicChannelCreateRequest request = new PublicChannelCreateRequest("공지", "공지 채널입니다.");
		Channel channel = channelService.createPublic(request);
		return channel;
	}

	static void messageCreateTest(MessageService messageService, Channel channel, User author) {
		MessageCreateRequest request = new MessageCreateRequest("안녕하세요.", channel.getId(), author.getId());
		Message message = messageService.create(request, new ArrayList<>());
		System.out.println("메시지 생성: " + message.getId());
	}

	static void loginTest(AuthService authService) {
		try {
			LoginRequest request = new LoginRequest("okodee", "okodee1234");
			UserDto userDto = authService.login(request);
			System.out.println("로그인 성공: " + userDto.username() + ", 이메일: " + userDto.email());
		} catch (IllegalArgumentException e) {
			System.out.println("로그인 실패: " + e.getMessage());
		}
	}

	public static void main(String[] args) {
		ConfigurableApplicationContext context = SpringApplication.run(DiscodeitApplication.class, args);
		// 서비스 초기화
		UserService userService = context.getBean(UserService.class);
		ChannelService channelService = context.getBean(ChannelService.class);
		MessageService messageService = context.getBean(MessageService.class);
		AuthService authService = context.getBean(AuthService.class);

		// 셋업
		User user = setupUser(userService);
		loginTest(authService);
		Channel channel = setupChannel(channelService);
		// 테스트
		messageCreateTest(messageService, channel, user);
	}

}
