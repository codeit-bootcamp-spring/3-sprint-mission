package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.entity.Common;

public class JavaApplication {
	public static void main(String[] args) {

		// 여러 개의 Common 객체 테스트
		try {
			Common common1 = new Common();
			System.out.println("common1 - id: " + common1.getId()
					+ "\n common1 - createdAt: " + common1.getCreatedAt()
					+ "\n common1 - updatedAt: " + common1.getUpdatedAt());
			Thread.sleep(1000);
			Common common2 = new Common();
			System.out.println("common2 - id: " + common2.getId()
					+ "\n common2 - createdAt: " + common2.getCreatedAt()
					+ "\n common2 - updatedAt: " + common2.getUpdatedAt());
			Thread.sleep(1000);
			Common common3 = new Common();
			System.out.println("common2 - id: " + common3.getId()
					+ "\n common2 - createdAt: " + common3.getCreatedAt()
					+ "\n common2 - updatedAt: " + common3.getUpdatedAt());
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}

		// 등록

		// 조회(단건, 다건)

		// 수정

		// 수정된 데이터 조회

		// 삭제

		// 조회를 통해 삭제되었는지 확인

	}
}
