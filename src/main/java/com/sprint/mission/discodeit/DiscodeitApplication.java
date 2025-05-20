package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.dto.request.*;
import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.service.*;
import org.springframework.boot.CommandLineRunner;
import java.util.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;


@SpringBootApplication
public class DiscodeitApplication {
	public static void main(String[] args) {
		ConfigurableApplicationContext context = SpringApplication.run(DiscodeitApplication.class, args);
	}
//	@Bean
//	public CommandLineRunner serviceTestRunner(
//			UserService userService,
//			UserStatusService userStatusService,
//			ChannelService channelService,
//			MessageService messageService,
//			BinaryContentService binaryContentService,
//			ReadStatusService readStatusService
//	) {
//		return args -> {
//			// === UserService: User 생성 ===
//			UserCreateRequest userCreateRequest = new UserCreateRequest("tester", "test@abc.com", "pw1234");
//			User user = userService.create(userCreateRequest);
//			System.out.println("User 생성: " + user);
//			// === ChannelService: 채널 생성/조회/수정/삭제 ===
//			PublicChannelCreateRequest channelCreateReq = new PublicChannelCreateRequest("공개채널", "소개");
//			Channel channel = channelService.create(channelCreateReq);
//			System.out.println("채널 생성: " + channel);
//
//			PublicChannelUpdateRequest channelUpdateReq = new PublicChannelUpdateRequest("채널명수정", "소개수정");
//			channelService.update(channel.getId(), channelUpdateReq);
//			System.out.println("채널명 수정: " + channelService.find(channel.getId()));
//
//			channelService.delete(channel.getId());
//			System.out.println("채널 삭제 완료");
//
//			// === MessageService: 메시지 생성/조회/수정/삭제 ===
//			// 채널 다시 생성
//			channel = channelService.create(channelCreateReq);
//			MessageCreateRequest msgReq = new MessageCreateRequest("안녕",channel.getId(),user.getId());
//			Message message = messageService.create(msgReq);
//			System.out.println("메시지 생성: " + message);
//			BinaryContentCreateRequest fileReq = new BinaryContentCreateRequest("file.txt","text" , new byte[]{1,2,3});
//			Message message2 = messageService.create(msgReq, List.of(fileReq));
//			System.out.println("첨부 메시지 생성: " + message2);
//			Message foundMsg = messageService.findById(message.getId());
//			System.out.println("메시지 조회: " + foundMsg);
//
//			messageService.update(message.getId(), "메시지수정");
//			System.out.println("메시지 수정: " + messageService.findById(message.getId()));
//		};
//	}
}
