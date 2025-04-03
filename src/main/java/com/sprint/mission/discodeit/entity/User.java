package com.sprint.mission.discodeit.entity;

import java.util.UUID;

public class User extends Common {
	/*
	 * # User field
	 *
	 * ## Local
	 * - name(회원명)
	 * - pwd(비밀번호)
	 *
	 * ## Common
	 * - id(UUID)
	 * - createdAt(최초 생성)
	 * - updatedAt(생성 직후?, 수정 시)
	 */
	private String name;
	private String pwd;

	public User(String name, String pwd) {
		super(); // Common 객체 생성 - id, createdAt, updatedAt
		this.name = name;
		this.pwd = pwd;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
		super.setUpdatedAt(); // 수정 시간 업데이트
	}

	public void setPwd(String pwd) {
		this.pwd = pwd;
		super.setUpdatedAt(); // 수정 시간 업데이트
	}

	@Override
	public String toString() {
		return "User{" +
				"name='" + getName() + '\'' +
				", pwd='" + pwd + '\'' +
				", id='" + getId() + '\'' +
				", createdAt='" + getCreatedAt() + '\'' +
				", updatedAt='" + getUpdatedAt() + '\'' +
				'}';
	}
}
