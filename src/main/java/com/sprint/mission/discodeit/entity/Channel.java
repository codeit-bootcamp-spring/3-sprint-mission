package com.sprint.mission.discodeit.entity;

import java.util.ArrayList;
import java.util.List;

public class Channel extends Common {
	/*
	 * # Channel field
	 *
	 * ## Local
	 * - creator (생성자)
	 * - name (채널명)
	 * - participants (참여자) // 방보다 사용자가 더 많지 않을까
	 *
	 * ## Common
	 * - id(UUID)
	 * - createdAt(최초 생성)
	 * - updatedAt(생성 직후?, 수정 시)
	 */
	private final User creator;
	private String name;
	private List<User> participants = new ArrayList<User>();

	public Channel(User user, String name) {
		super(); // Common 객체 생성 - id, createdAt, updatedAt
		this.name = name;
		this.creator = user; // 생성자 초기화
		this.participants.add(user); // 생성자를 첫 번째 참여자로!
	}

	public User getCreator() {
		return creator;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
		super.setUpdatedAt(); // 수정 시간 업데이트
	}

	public List<User> setParticipants(List<User> participants) {
		this.participants = participants;
		return participants;
	}

	public List<User> getParticipants() {
		return participants;
	}

	@Override
	public String toString() {
		return "Channel{" +
				"name='" + getName() + '\'' +
				", creator='" + getCreator() + '\'' +
				", participants='" + getParticipants() + '\'' +
				", createdAt='" + getCreatedAt() + '\'' +
				", updatedAt='" + getUpdatedAt() + '\'' +
				'}';
	}
}
