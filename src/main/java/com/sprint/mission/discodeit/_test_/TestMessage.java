package com.sprint.mission.discodeit._test_;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;

public class TestMessage {
	public static void main(String[] args) {
		/*
		 * > Common 객체 테스트 후 진행하므로 updatedAt 확인
		 *
		 * # TestMessage
		 * - Message 생성, 결과 출력
		 */
		// User 생성
		User user = new User("user1","pw123");
		// Channel 생성
		Channel channel = new Channel("channel1");

		// Message 생성
		Message message = new Message("blahblah", user.getId(), channel.getId());
		System.out.println(message);
	}
}
