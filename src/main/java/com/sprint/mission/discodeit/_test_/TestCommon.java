package com.sprint.mission.discodeit._test_;

import com.sprint.mission.discodeit.entity.Common;

public class TestCommon {
	/*
	 * # TestCommon
	 * 생성 시 각 Common 객체의 필드 값이 다르게 확인되는지 테스트
	 */
	public static void main(String[] args) {
		// 여러 개의 Common 객체 테스트
		try {
			Common common1 = new Common();
			System.out.println("common1 - id: " + common1.getId()
					+ "\n common1 - createdAt: " + common1.getCreatedAt()
					+ "\n common1 - updatedAt: " + common1.getUpdatedAt());
			Thread.sleep(100);
			Common common2 = new Common();
			System.out.println("common2 - id: " + common2.getId()
					+ "\n common2 - createdAt: " + common2.getCreatedAt()
					+ "\n common2 - updatedAt: " + common2.getUpdatedAt());
			Thread.sleep(100);
			Common common3 = new Common();
			System.out.println("common2 - id: " + common3.getId()
					+ "\n common2 - createdAt: " + common3.getCreatedAt()
					+ "\n common2 - updatedAt: " + common3.getUpdatedAt());
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}
}
