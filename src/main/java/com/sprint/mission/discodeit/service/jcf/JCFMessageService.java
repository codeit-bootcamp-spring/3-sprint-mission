package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
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
	private final JCFUserService userService;
	private final JCFChannelService channelService;

	// 생성자
	public JCFMessageService(JCFUserService userService, JCFChannelService channelService) {
		this.userService = userService;
		this.channelService = channelService;
	}

	// 등록
	@Override
	public Message create(Message message) {
		try {
			// 유저 존재 여부 확인
			if(userService.read(message.getUserId())==null) {
				throw new IllegalStateException("유저를 찾을 수 없음: " + message.getUserId());
			}

			// 채널 존재 여부 확인
			Channel channel = channelService.read(message.getChannelId());
			if(channel == null) {
				throw new IllegalStateException("채널을 찾을 수 없음: " + message.getChannelId());
			}

			// 유저가 채널에 참여 중인지 확인
			boolean isNotParticipant = channel.getParticipants().stream()
												//.peek(user -> System.out.println("create > isNotParticipant > peek - 참여자 ID: " + user.getId())) // 중간 값 출력 테스트
												.noneMatch(user -> {
													// 일치하는 참여자가 없다면 true
													//System.out.println("participant userId: " + user.getId() + " | 메시지 생성 시 userId: " + message.getUserId());
													return user.getId().equals(message.getUserId());
												});
			if(isNotParticipant) {
				throw new IllegalStateException("채널 참여자가 아닙니다.");
			}

			data.put(message.getId(), message);
			return data.get(message.getId());
		} catch (Exception e) {
			System.out.println("Unexptexted error: " + e.getMessage());
			return null;
		}
	}

	// 단건 조회
	@Override
	public Message read(UUID uuid) {
		return data.get(uuid);
	};

	// 내용을 포함하는 메시지 조회
	@Override
	public List<Message> readByContent(String content) {
		// data에서 content를 포함하는 결과를 담아서 리턴
		return data.values().stream() // Map의 User 객체들을 Stream으로 변환
				// 내용이 null이 아니고, content를 포함하는 것만
				.filter(m -> m.getContent() != null && m.getContent().contains(content))
				.collect(Collectors.toList()); // List로 반환
	}

	// 특정 유저의 메시지 조회
	@Override
	public List<Message> readByUserId(UUID userId) {
		// data에서 userId가 일치하는 결과를 리턴
		return data.values().stream() // Map의 User 객체들을 Stream으로 변환
				.filter(m -> m.getUserId().equals(userId))
				.collect(Collectors.toList()); // List로 반환
	}

	// 특정 채널의 메시지 조회
	@Override
	public List<Message> readByChannelId(UUID channelId) {
		// data에서 channelId가 일치하는 결과를 리턴
		return data.values().stream() // Map의 User 객체들을 Stream으로 변환
				.filter(m -> m.getChannelId().equals(channelId))
				.collect(Collectors.toList()); // List로 반환
	}

	// 특정 채널 && 특정 유저의 메시지 조회
	@Override
	public List<Message> readByChannelIdAndUserId(UUID channelId, UUID userId) {
		// data에서 channelId와 userId가 동시에 일치하는 결과를 리턴
		return data.values().stream() // Map의 User 객체들을 Stream으로 변환
				.filter(m -> m.getChannelId().equals(channelId) && m.getUserId().equals(userId))
				.collect(Collectors.toList()); // List로 반환
	}

	// 전체 조회?
	// List<Message> readAll();

	// 수정
	@Override
	public Message update(UUID uuid, String content) {
		Message target = data.get(uuid);
		target.setContent(content);
		return target;
	}

	// 삭제
	@Override
	public void delete(UUID uuid) {
		data.remove(uuid);
	};
}
