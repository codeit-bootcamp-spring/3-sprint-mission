package com.sprint.mission.discodeit._test_;

import com.sprint.mission.discodeit.entity.User;

public class TestUser {
	public static void main(String[] args) {
		/*
		 * > Common 객체 테스트 후 진행하므로 updatedAt 확인
		 *
		 * # TestUser
		 * - User 생성, 결과 출력
		 * - 비밀번호 변경, 결과 출력, updatedAt 변했는지?
		 */
		try {
			// User 생성
			User user = new User("user1","pw123");
			System.out.println(user);

			// 이름 변경
			Thread.sleep(100); // 시간 차이를 위해
			user.setName("user222");
			System.out.println(user);

			// 비밀번호 변경
			Thread.sleep(100); // 시간 차이를 위해
			user.setPwd("pw456");
			System.out.println(user);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}
}
