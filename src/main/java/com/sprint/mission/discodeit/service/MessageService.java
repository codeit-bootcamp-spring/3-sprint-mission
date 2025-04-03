package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Message;

import java.util.List;
import java.util.UUID;

public interface MessageService {
	Message create(Message message); // 등록
	Message read(UUID uuid); // 단건 조회
	List<Message> readByUserId(UUID userId);
	List <Message> readByChannelId(UUID channelId);
	List <Message> readByChannelIdAndUserId(UUID channelId, UUID userId);
	//List<Message> readAll(); // 전체 조회?
	Message update(UUID uuid,String content); // 수정
	void delete(UUID uuid); // 삭제
}
