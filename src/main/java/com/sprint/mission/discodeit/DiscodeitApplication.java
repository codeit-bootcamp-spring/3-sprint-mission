package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.Scanner;

@SpringBootApplication
public class DiscodeitApplication {

	public static void main(String[] args) {
		ConfigurableApplicationContext context = SpringApplication.run(DiscodeitApplication.class, args);

		UserService userService = context.getBean(UserService.class);
		ChannelService channelService = context.getBean(ChannelService.class);
		MessageService messageService = context.getBean(MessageService.class);

		User user = setupUser(userService);
		Channel channel = setupChannel(channelService);

		messageCreateTest(messageService, channel, user);
	}

	private static User setupUser(UserService userService) {
		Scanner scanner = new Scanner(System.in);
		System.out.print("사용자 이름 입력: ");
		String userName = scanner.nextLine();
		User user = new User(userName);
		userService.createUser(user);
		System.out.println("생성된 사용자: " + user);
		return user;
	}

	private static Channel setupChannel(ChannelService channelService) {
		Scanner scanner = new Scanner(System.in);
		System.out.print("채널 이름 입력: ");
		String channelName = scanner.nextLine();
		Channel channel = new Channel(channelName);
		channelService.createChannel(channel);
		System.out.println("생성된 채널: " + channel);
		return channel;
	}

	private static void messageCreateTest(MessageService messageService, Channel channel, User user) {
		Scanner scanner = new Scanner(System.in);
		System.out.print("보낼 메시지 입력: ");
		String msgContent = scanner.nextLine();
		Message message = new Message(msgContent, user.getId(), channel.getId());
		messageService.createMessage(message);
		System.out.println("메시지 전송: " + message);
	}

}
