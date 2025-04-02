package com.sprint.mission.discodeit._test_;

import com.sprint.mission.discodeit.entity.Channel;

public class TestChannel {
	public static void main(String[] args) {
		/*
		 * > Common 객체 테스트 후 진행하므로 updatedAt 확인
		 *
		 * # TestChannel
		 * - Channel 생성, 결과 출력
		 */
		try {
			// Channel 생성
			Channel channel = new Channel("channel1");
			System.out.println(channel);

			// 이름 변경
			Thread.sleep(100); // 시간 차이를 위해
			channel.setName("channel222");
			System.out.println(channel);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}
}
