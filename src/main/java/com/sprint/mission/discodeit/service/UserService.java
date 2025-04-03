package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

public interface UserService {
	User create(User user); // 등록
	User read(UUID id); // 단건 조회
	List<User> readByName(String name); // 이름으로 다건 조회
	List<User> readAll(); // 전체 조회
	User update(User user, String name, String pwd); // 이름 수정
	void delete(UUID id); // 삭제
}
