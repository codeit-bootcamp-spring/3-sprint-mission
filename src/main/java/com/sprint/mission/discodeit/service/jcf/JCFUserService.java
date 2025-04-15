package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;

import java.util.*;
import java.util.stream.Collectors;

public class JCFUserService implements UserService {
	// 유저 리스트
	// HashMap은 순서를 보장하지 않는다.
	private final Map<UUID, User> data = new HashMap<>();

	// 등록
	@Override
	public User create(User user) {
		data.put(user.getId(), user);
		return user;
	}

	// 단건 조회
	@Override
	public User read(UUID id) {
		return data.get(id);
	}

	// name으로 조회
	@Override
	public List<User> readByName(String name) {
		// data에서 name으로 조회된 결과를 담아서 리턴
		return data.values().stream() // Map의 User 객체들을 Stream으로 변환
				.filter(user -> name.equals(user.getName())) // 이름이 일치하는 것만 필터링
				.collect(Collectors.toList()); // List로 반환
	}

	// 전체 조회
	@Override
	public List<User> readAll() {
		return new ArrayList<>(data.values());
	}

	// 이름 또는 비밀번호 수정
	@Override
	public User update(User user, String name, String pwd) {
		if(name!=null && !name.isEmpty()) {
			user.setName(name);
		}
		if(pwd != null && !pwd.isEmpty()) {
			user.setPwd(pwd);
		}
		data.put(user.getId(), user);
		return user;
	}

	// 삭제
	// 실제 삭제가 아닌 flag로 변경할 수도?
	@Override
	public void delete(UUID id) {
		data.remove(id);
	}
}
