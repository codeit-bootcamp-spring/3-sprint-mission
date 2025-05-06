package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Optional;

@SpringBootApplication
public class DiscodeitApplication {

	static User setupUser(UserService userService) {
		UserCreateRequest request = new UserCreateRequest("woody", "woody@codeit.com", "woody1234");
		User user = userService.create(request, Optional.empty());
		return user;
	}

	static Channel setupChannel(ChannelService channelService) {
		PublicChannelCreateRequest request = new PublicChannelCreateRequest("공지", "공지 채널입니다.");
		Channel channel = channelService.create(request);
		return channel;
	}

	static void messageCreateTest(MessageService messageService, Channel channel, User author) {
		MessageCreateRequest request = new MessageCreateRequest(channel.getId(), author.getId(), "안녕하세요.");
		Message message = messageService.create(request, new ArrayList<>());
		System.out.println("메시지 생성: " + message.getId());
	}

	public static void main(String[] args) {
		ConfigurableApplicationContext context = SpringApplication.run(DiscodeitApplication.class, args);

		UserService userService = context.getBean(UserService.class);
		ChannelService channelService = context.getBean(ChannelService.class);
		MessageService messageService = context.getBean(MessageService.class);

		User user = setupUser(userService);
		Channel channel = setupChannel(channelService);

		messageCreateTest(messageService, channel, user);
	}
}