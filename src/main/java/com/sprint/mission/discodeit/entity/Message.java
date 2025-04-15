package com.sprint.mission.discodeit.entity;

import java.util.UUID;

public class Message extends Common {
	/*
	 * # Message field
	 *
	 * ## Local
	 * > 메시지가 userId, channelId를 가져서 1:N 구조
	 * - content
	 * - userId
	 * - channelId
	 *
	 * ## Common
	 * - id(UUID)
	 * - createdAt(최초 생성)
	 * - updatedAt(생성 직후?, 수정 시)
	 */
	private String content;
	private final UUID userId; // 메시지 주인이 바뀌는 건 요상하니까..? 유저명이 변경돼도 id로 조회하니까 유지될 듯.
	private final UUID channelId;  // 채널명은 바뀔 수 있겠지만, channelId는 유지될 듯?

	public Message(String content, UUID userId, UUID channelId) {
		super();
		this.content = content;
		this.userId = userId;
		this.channelId = channelId;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public UUID getUserId() {
		return userId;
	}

	public UUID getChannelId() {
		return channelId;
	}

	@Override
	public String toString() {
		return "Message{" +
				"content='" + getContent() + '\'' +
				", id='" + getId() + '\'' +
				", userId='" + getUserId() + '\'' +
				", channelId='" + getChannelId() + '\'' +
				", createdAt='" + getCreatedAt() + '\'' +
				", updatedAt='" + getUpdatedAt() + '\'' +
				'}';
	}
}
