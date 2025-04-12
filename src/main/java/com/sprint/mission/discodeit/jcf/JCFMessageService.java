package com.sprint.mission.discodeit.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.ChannelService;

import java.util.*;

// 실제 메시지를 저장하고 관리하는 JCFMessageService 클래스
public class JCFMessageService implements MessageService {

    // 메시지를 저장할 공간 (Map 자료구조 사용)
    // key는 UUID, value는 Message 객체
    private final Map<UUID, Message> data = new HashMap<UUID, Message>();

    // 의존성 주입을 위한 필드
    private final UserService userService;
    private final ChannelService channelService;

    // 생성자에서 서비스 주입
    public JCFMessageService(UserService userService, ChannelService channelService) {
        this.userService = userService;
        this.channelService = channelService;
    }


    @Override // 메시지 등록 기능 (Map에 저장)
    public void create(Message message) {
        // 유저와 채널이 존재하는지 검증
        if (userService.getById(message.getUserId()) == null) {
            throw new IllegalArgumentException("존재하지 않는 유저입니다.");
        }

        if (channelService.getById(message.getChannelId()) == null) {
            throw new IllegalArgumentException("존재하지 않는 채널입니다.");
        }

        data.put(message.getId(), message);
    }

    @Override // 메시지를 ID로 조회하는 기능
    public Message getById(UUID id) {
        return data.get(id);
    }

    @Override // 저장된 모든 메시지를 리스트로 가져오는 기능
    public List<Message> getAll() {
        return new ArrayList<>(data.values()); // Map의 값들만 모아서 리스트로 반환
    }

    @Override  // 메시지를 수정하는 기능
    public void update(Message message) {
        data.put(message.getId(), message);
    }

    @Override // 메시지를 삭제하는 기능
    public void delete(UUID id) {
        data.remove(id);
    }
}
