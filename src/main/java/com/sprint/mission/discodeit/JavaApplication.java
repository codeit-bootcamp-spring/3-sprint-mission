package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.jcf.JCFChannelService;
import com.sprint.mission.discodeit.service.jcf.JCFMessageService;
import com.sprint.mission.discodeit.service.jcf.JCFUserService;

import java.util.ArrayList;
import java.util.List;

public class JavaApplication {
	public static void main(String[] args) {
		JCFUserService userService = new JCFUserService();
		JCFChannelService channelService = new JCFChannelService();
		JCFMessageService messageService = new JCFMessageService(userService, channelService);

		/* 등록 */
		// 유저 생성
		User user1 = userService.create(new User("user1","pwd1"));
		User user2 = userService.create(new User("user1","pwd2"));
		User user3 = userService.create(new User("user3","pwd3"));
		User user4 = userService.create(new User("user4","pwd4"));
		User user5 = userService.create(new User("user5","pwd5"));

		// 채널 생성
		Channel channel1_1 = channelService.create(new Channel(user1,"channel1-1"));
		Channel channel1_2 = channelService.create(new Channel(user1,"channel1-2"));
		Channel channel2_1 = channelService.create(new Channel(user2,"channel2-1"));
		Channel channel2_2 = channelService.create(new Channel(user2,"channel2-2"));
		Channel channel2_3 = channelService.create(new Channel(user2,"channel2-3"));

		// 메시지 생성
		System.out.println("[============= 메시지 생성 시 유효성 검사 =============]");
		Message message1_1_1 = messageService.create(new Message("channel1-1의 메시지", user1.getId(), channel1_1.getId()));
		Message message1_1_2 = messageService.create(new Message("channel1-1의 메시지", user1.getId(), channel1_1.getId()));
		Message message1_2_1 = messageService.create(new Message("channel1-2의 메시지", user1.getId(), channel1_2.getId()));
		Message message1_2_2 = messageService.create(new Message("channel1-2의 메시지", user1.getId(), channel1_2.getId()));
		Message message1_2_3 = messageService.create(new Message("channel1-2의 메시지", user1.getId(), channel1_2.getId()));

		/* 조회(단건, 다건) */
		System.out.println("\n[============= 조회 =============]");
		// 유저 단건 조회
		System.out.println("[유저 단건 조회] - user의 id로 조회");
		System.out.println(userService.read(user1.getId()));
		// 유저 다건 조회
		System.out.println("[유저 다건 조회] - user의 name으로 조회");
		userService.readByName(user1.getName()).forEach(System.out::println);
		
		// 채널 단건 조회
		System.out.println("[채널 단건 조회] - channel의 id로 조회");
		System.out.println(channelService.read(channel1_1.getId()));
		// 채널 다건 조회
		System.out.println("[채널 다건 조회] - channel의 생성자 id로 조회");
		channelService.readByCreatorId(user1.getId()).forEach(System.out::println);
		
		// 메시지 조회
		System.out.println("[메시지 단건 조회] - message의 id로 조회");
		System.out.println(messageService.read(message1_1_1.getId()));
		System.out.println("[메시지 다건 조회] - message의 생성자 id로 조회");
		messageService.readByUserId(user1.getId()).forEach(System.out::println);

		/* 수정 */
		System.out.println("\n[============= 수정 =============]");
		// 유저 수정
		userService.update(user1, "라이온", null);
		// 유저 조회
		System.out.println("[유저 수정 조회] - 변경된 유저명으로 조회");
		userService.readByName(user1.getName()).forEach(System.out::println);
		
		// 채널 수정
		channelService.update(channel1_1, "라이온의 채널");
		// 채널 조회
		System.out.println("[채널 수정 조회] - 변경된 유저명, 채널명");
		channelService.readByCreatorId(user1.getId()).forEach(System.out::println);

		// 메시지 수정
		messageService.update(message1_1_1.getId(),"폭싹 속았수다.");
		// 메시지 조회
		System.out.println("[메시지 수정 조회] - 변경된 content");
		messageService.readByChannelIdAndUserId(channel1_1.getId(),user1.getId()).forEach(System.out::println);

		/* 삭제 */
		System.out.println("\n[============= 삭제 =============]");
		// 유저 삭제
		// idea: 유저 정보 중 개인정보는 하드 삭제, 관련 데이터(채널의 참여자, 메시지 이력 등)는 소프트 삭제
		userService.delete(user1.getId());
		// 유저 조회
		System.out.println("[유저 삭제 조회] - 같은 유저명이 존재하니 UUID로 비교");
		userService.readAll().forEach(System.out::println);

		// 메시지 삭제
		messageService.delete(message1_1_1.getId());
		messageService.delete(message1_1_2.getId());
		// 메시지 조회
		System.out.println("[메시지 삭제 조회]");
		messageService.readByChannelIdAndUserId(channel1_1.getId(),user1.getId()).forEach(System.out::println);

		// 채널 삭제
		channelService.delete(channel1_1.getId());
		// 채널 조회
		channelService.readByCreatorId(user1.getId()).forEach(System.out::println);

		/* 유효성 검사 */
		System.out.println("\n[============= 유효성 검사 =============]");
		System.out.println("[메시지 유효성 검사 케이스 1]");
		Message message_valid_1 = messageService.create(new Message("삭제된 유저 & 삭제된 채널의 경우 유저를 먼저 검증해서 오류 우선순위가 다름", user1.getId(), channel1_1.getId()));
		System.out.println("[메시지 유효성 검사 케이스 2]");
		Message message_valid_2 = messageService.create(new Message("채널 참여자가 아닌 경우", user2.getId(), channel1_2.getId()));
		System.out.println("[메시지 유효성 검사 케이스 3]");
		Message message_valid_3 = messageService.create(new Message("채널이 삭제된 경우", user3.getId(), channel1_1.getId()));
	}
}
