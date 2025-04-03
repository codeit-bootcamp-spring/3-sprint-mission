package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class JCFChannelService implements ChannelService {
	// 채널 리스트
	// HashMap은 순서를 보장하지 않는다.
	private final Map<UUID, Channel> data = new HashMap<>();

	// 등록
	@Override
	public Channel create(Channel channel) {
		data.put(channel.getId(), channel);
		return channel;
	};

	// 단건 조회
	@Override
	public Channel read(UUID id) {
		return data.get(id);
	};

	// 생성자 Id로 조회
	@Override
	public List<Channel> readByCreatorId(UUID userId) {
		return data.values().stream()
				.filter(m -> m.getCreator().getId().equals(userId))
				.collect(Collectors.toList());
	};

	// 참여자 Id로 조회
	@Override
	public List<Channel> readByParticipantId(UUID userId) {
    return data.values().stream()
            .filter(m -> m.getParticipants().stream()
					// 참여자 중 일치하는 게 있다면
					// contains 보다 유연성이 좋다는 것 같아서 도전.
                    .anyMatch(p -> p.getId().equals(userId)))
            .collect(Collectors.toList());
	};

	// 채널 이름 수정
	@Override
	public Channel update(Channel channel, String newName) {
        Channel target = data.get(channel.getId());
        target.setName(newName);
        return channel;
	};

	// 참여자 초대
	@Override
	public Channel inviteParticipant(Channel channel, User user) throws Exception {
        Channel target = data.get(channel.getId());

        // 이미 참여 중인지 확인
        boolean isAlreadyParticipant = target.getParticipants().stream()
                .anyMatch(p -> p.getId().equals(user.getId()));

        if (isAlreadyParticipant) {
			throw new Exception("이미 참여 중입니다!");
		}
		// 참여자에 user 추가
        target.getParticipants().add(user);
        return target;
	};

	// 참여자 추방
	@Override
    public Channel kickParticipant(Channel channel, User user) {
        Channel target = data.get(channel.getId());
		// 참여자에서 user 제거
        target.getParticipants().remove(user);
        return target;
	};

	// 삭제
	@Override
	public void delete(UUID id) {
		data.remove(id);
	};
}
