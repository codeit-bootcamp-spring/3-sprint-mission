package com.sprint.mission.discodeit.entity;

import java.util.UUID;

public class Common {
	/*
	 * # Common field
	 * - id(UUID)
	 * - createdAt(최초 생성)
	 * - updateAt(최초 생성, 수정 시)
	 */
	private final UUID id; // 생성 후 변경 없음.
	private final long createdAt; // 생성 후 변경 없음.
	private long updatedAt;

    public Common() {
        this.id = UUID.randomUUID();
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = this.createdAt;
    }

	public UUID getId() {
		return id;
	}

	public long getCreatedAt() {
		return createdAt;
	}

	public long getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt() {
		this.updatedAt = System.currentTimeMillis();
	}
}
