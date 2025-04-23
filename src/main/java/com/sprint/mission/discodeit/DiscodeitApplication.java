package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.jcf.JcfChannelRepository;
import com.sprint.mission.discodeit.repository.jcf.JcfMessageRepository;
import com.sprint.mission.discodeit.repository.jcf.JcfUserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.basic.BasicChannelService;
import com.sprint.mission.discodeit.service.basic.BasicMessageService;
import com.sprint.mission.discodeit.service.basic.BasicUserService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.beans.beancontext.BeanContext;

@SpringBootApplication
public class DiscodeitApplication {



	public static void main(String[] args) {
//		SpringApplication.run(DiscodeitApplication.class, args);
		ApplicationContext context = SpringApplication.run(DiscodeitApplication.class, args);

		UserService userService = context.getBean("basicUserService", UserService.class);
		ChannelService channelService = context.getBean("basicChannelService", ChannelService.class);
		MessageService messageService = context.getBean("basicMessageService", MessageService.class);


		System.out.println("=== User 테스트 ===");
		User user1 = userService.createUser("user1","user1@user.com","1234");
		User user2 = userService.createUser("user2","user2@user.com","1234");
		User user3 = userService.createUser("user3","user3@user.com","1234");


		System.out.println("\n[모든 유저 출력]");
		userService.findAllUsers().forEach(u -> System.out.println("- " + u.getUsername()));

		System.out.println("\n[단일 유저 조회]");
		System.out.println("조회된 유저: " + userService.findUserById(user1.getId()).getUsername());

		System.out.println("\n[유저 이름 수정]");
		userService.updateUser(user2.getId(), "new user");
		System.out.println("수정 결과: " + userService.findUserById(user2.getId()).getUsername());

		System.out.println("\n[유저 삭제]");
		userService.deleteUser(user1.getId());

		System.out.println("\n[최종 유저 목록]");
		userService.findAllUsers().forEach(u -> System.out.println("- " + u.getUsername()));


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
