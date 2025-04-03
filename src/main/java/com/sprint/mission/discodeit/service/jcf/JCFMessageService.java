package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.MessageService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class JCFMessageService implements MessageService {
	// 메시지 리스트
	// HashMap은 순서를 보장하지 않는다.
	private final Map<UUID, Message> data = new HashMap<>();

	// 등록
	public Message create(Message message) {
		data.put(message.getId(), message);
		return data.get(message.getId());
	}

	// 단건 조회
	public Message read(UUID uuid) {
		return data.get(uuid);
	};

	// 내용을 포함하는 메시지 조회
	public List<Message> read(String content) {
		// data에서 content를 포함하는 결과를 담아서 리턴
		return data.values().stream() // Map의 User 객체들을 Stream으로 변환
				// 내용이 null이 아니고, content를 포함하는 것만
				.filter(m -> m.getContent() != null && m.getContent().contains(content))
				.collect(Collectors.toList()); // List로 반환
	}

	// 특정 유저의 메시지 조회
	public List<Message> readByUserId(UUID userId) {
		// data에서 userId가 일치하는 결과를 리턴
		return data.values().stream() // Map의 User 객체들을 Stream으로 변환
				.filter(m -> m.getUserId().equals(userId))
				.collect(Collectors.toList()); // List로 반환
	}

	// 특정 채널의 메시지 조회
	public List<Message> readByChannelId(UUID channelId) {
		// data에서 channelId가 일치하는 결과를 리턴
		return data.values().stream() // Map의 User 객체들을 Stream으로 변환
				.filter(m -> m.getChannelId().equals(channelId))
				.collect(Collectors.toList()); // List로 반환
	}

	// 특정 채널 && 특정 유저의 메시지 조회
	public List<Message> readByChannelIdAndUserId(UUID channelId, UUID userId) {
		// data에서 channelId와 userId가 동시에 일치하는 결과를 리턴
		return data.values().stream() // Map의 User 객체들을 Stream으로 변환
				.filter(m -> m.getChannelId().equals(channelId) && m.getUserId().equals(userId))
				.collect(Collectors.toList()); // List로 반환
	}

	// 전체 조회?
	// List<Message> readAll();

	// 수정
	public Message update(UUID uuid, String content) {
		Message target = data.get(uuid);
		target.setContent(content);
		return target;
	}

	// 삭제
	public void delete(UUID uuid) {
		data.remove(uuid);
	};
}
