package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.HashMap;   
import java.util.List;      
import java.util.Map;       
import java.util.UUID;      
import java.util.stream.Collectors; 

/**
 * Java Collection Framework를 사용하여 메시지 데이터를 메모리에 저장하고 관리하는 서비스 구현체.
 * {@link UserService}와 {@link ChannelService}에 의존하여 메시지 생성 시
 * 작성자 및 채널 존재 여부, 작성자의 채널 참여 여부를 검증합니다.
 */
public class JCFMessageService implements MessageService {
    private final Map<UUID, Message> data;// 데이터를 저장하는 필드 (ID를 키로 사용)
    private final UserService userService;
    private final ChannelService channelService;

    /**
     * JCFMessageService의 새 인스턴스를 생성합니다.
     * 주어진 {@link UserService}와 {@link ChannelService}를 사용하여
     * 메시지 생성 시 필요한 검증 로직을 수행합니다.
     *
     * @param userService    사용자 관련 로직을 처리하는 서비스
     * @param channelService 채널 관련 로직을 처리하는 서비스
     */
    public JCFMessageService(UserService userService, ChannelService channelService) {
        this.data = new HashMap<>();
        this.userService = userService;
        this.channelService = channelService;
    }

    /**
     * 새로운 메시지를 생성하고 저장합니다.
     * 메시지 작성자와 대상 채널이 존재하는지, 그리고 작성자가 해당 채널의 참여자인지 확인합니다.
     * 모든 검증을 통과하면 메시지를 생성하고 내부 데이터 저장소에 추가합니다.
     *
     * @param content   메시지의 내용
     * @param authorId  메시지 작성자의 UUID
     * @param channelId 메시지를 보낼 채널의 UUID
     * @return 생성된 {@link Message} 객체
     * @throws IllegalArgumentException 작성자 또는 채널이 존재하지 않거나, 작성자가 해당 채널의 참여자가 아닐 경우 발생합니다.
     */
    @Override
    public Message createMessage(String content, UUID authorId, UUID channelId) {
        // 1. 사용자(Author) 존재 여부 검증
        User author = userService.getUserById(authorId);
        if (author == null) {
            throw new IllegalArgumentException("메시지 작성자(User)가 존재하지 않습니다. ID: " + authorId);
        }

        // 2. 채널 존재 여부 검증
        Channel channel = channelService.getChannelById(channelId);
        if (channel == null) {
            throw new IllegalArgumentException("메시지를 작성할 채널이 존재하지 않습니다. ID: " + channelId);
        }

        // 3. 사용자가 채널 참여자인지 검증 (Channel 엔티티의 로직 활용)
        if (!channel.isParticipant(authorId)) {
            throw new IllegalArgumentException("사용자(ID: " + authorId + ")는 해당 채널(ID: " + channelId + ")의 참여자가 아닙니다.");
        }

        // 모든 검증 통과 시 메시지 생성
        Message message = new Message(content, authorId, channelId);
        data.put(message.getMessageId(), message);
        return message;
    }

    /**
     * 지정된 UUID에 해당하는 메시지를 조회합니다.
     *
     * @param messageId 조회할 메시지의 UUID
     * @return 조회된 {@link Message} 객체. 해당 ID의 메시지가 없으면 null을 반환합니다.
     */
    @Override
    public Message getMessageById(UUID messageId) {
        return data.get(messageId); // ID로 메시지 조회
    }

    /**
     * 특정 채널에 속한 모든 메시지를 조회합니다.
     *
     * @param channelId 메시지를 조회할 채널의 UUID
     * @return 해당 채널에 속한 {@link Message} 객체들의 리스트. 메시지가 없으면 빈 리스트를 반환합니다.
     */
    @Override
    public List<Message> getMessagesByChannel(UUID channelId) {
        // 특정 채널의 메시지 필터링
        return data.values().stream()
                .filter(message -> message.getChannelId().equals(channelId))
                .collect(Collectors.toList());
    }

    /**
     * 특정 사용자가 작성한 모든 메시지를 조회합니다.
     *
     * @param authorId 메시지를 조회할 작성자의 UUID
     * @return 해당 작성자가 작성한 {@link Message} 객체들의 리스트. 메시지가 없으면 빈 리스트를 반환합니다.
     */
    @Override
    public List<Message> getMessagesByAuthor(UUID authorId) {
        // 특정 작성자의 메시지 필터링
        return data.values().stream()
                .filter(message -> message.getAuthorId().equals(authorId))
                .collect(Collectors.toList());
    }

    /**
     * 지정된 UUID를 가진 메시지의 내용을 업데이트합니다.
     * 해당 ID의 메시지가 존재할 경우에만 내용이 업데이트됩니다.
     *
     * @param messageId      업데이트할 메시지의 UUID
     * @param updatedContent 새로운 메시지 내용
     */
    @Override
    public void updateMessage(UUID messageId, String updatedContent) {
        Message message = data.get(messageId);
        if (message != null) {
            message.updateContent(updatedContent); // 메시지 내용 업데이트
        }
    }

    /**
     * 지정된 UUID를 가진 메시지를 삭제합니다.
     * 해당 ID의 메시지가 존재하면 내부 데이터 저장소에서 제거됩니다.
     *
     * @param messageId 삭제할 메시지의 UUID
     */
    @Override
    public void deleteMessage(UUID messageId) {
        data.remove(messageId); // 메시지 데이터 삭제
    }
}