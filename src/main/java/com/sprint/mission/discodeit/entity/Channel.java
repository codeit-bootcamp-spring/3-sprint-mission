package com.sprint.mission.discodeit.entity;

public class Channel extends Common {
	/*
	 * # Channel field
	 *
	 * ## Local
	 * - name(회원명)
	 *
	 * ## Common
	 * - id(UUID)
	 * - createdAt(최초 생성)
	 * - updatedAt(생성 직후?, 수정 시)
	 */
	private String name;

	public Channel(String name) {
		super(); // Common 객체 생성 - id, createdAt, updatedAt
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
		super.setUpdatedAt(); // 수정 시간 업데이트
	}

	@Override
	public String toString() {
		return "Channel{" +
				"name='" + name + '\'' +
				", createdAt='" + getCreatedAt() + '\'' +
				", updatedAt='" + getUpdatedAt() + '\'' +
				'}';
	}
}
