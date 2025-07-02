
package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.domain.Message;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.exception.message.MessageNotFoundException;
import com.sprint.mission.discodeit.repository.MessageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

class MessageServiceTest {

    @Mock
    private MessageRepository messageRepository;

    @InjectMocks
    private MessageService messageService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("메시지 생성 성공")
    void createMessage_success() {
        MessageCreateRequest request = new MessageCreateRequest("안녕하세요", UUID.randomUUID(), UUID.randomUUID());
        Message message = new Message(request.content(), request.channelId(), request.senderId());

        given(messageRepository.save(message)).willReturn(message);

        Message result = messageService.create(request);

        assertThat(result.getContent()).isEqualTo("안녕하세요");
    }

    @Test
    @DisplayName("메시지 조회 실패")
    void findMessage_notFound() {
        UUID id = UUID.randomUUID();
        given(messageRepository.findById(id)).willReturn(Optional.empty());

        assertThatThrownBy(() -> messageService.findById(id))
            .isInstanceOf(MessageNotFoundException.class);
    }
}
