package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

public interface ChannelService {
	Channel create(Channel channel); // 등록
	Channel read(UUID id); // 단건 조회
	List<Channel> readByCreatorId(UUID userId); // 생성자 Id로 조회
	List<Channel> readByParticipantId(UUID userId); // 참여자 Id로 조회
	Channel update(Channel channel, String newName); // 채널 이름 수정
	Channel inviteParticipant(Channel channel, User user) throws Exception; // 참여자 초대
    Channel kickParticipant(Channel channel, User user); // 참여자 추방
	void delete(UUID id); // 삭제
}
