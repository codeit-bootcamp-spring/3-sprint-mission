package com.sprint.mission.discodeit.integration;

import com.sprint.mission.discodeit.DiscodeitApplication;
import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.message.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.message.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.request.user.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.channel.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Message 엔드포인트의 통합 테스트 클래스입니다.
 * <p>
 * 실제 DB와 연동하여 전체 애플리케이션 레이어를 검증합니다.
 * - 메시지 생성, 수정, 삭제, 조회 등 다양한 시나리오를 검증합니다.
 */
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    classes = DiscodeitApplication.class
)
@ActiveProfiles("test")
class MessageIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private ChannelRepository channelRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * [성공] 메시지를 DB에 저장할 수 있는지 검증합니다.
     */
    @Test
    @DisplayName("[성공] 메시지 DB 저장")
    void shouldCreateMessageInDatabase() {
        // given: 메시지 생성에 필요한 사용자와 채널을 DB에 저장
        User author = userRepository.save(new User("testauthor_create", "author_create@test.com", "password123", null));
        Channel channel = channelRepository.save(new Channel(ChannelType.PUBLIC, "테스트 채널", "테스트 채널 설명"));
        
        MessageCreateRequest createRequest = new MessageCreateRequest(
            "테스트 메시지 내용", channel.getId(), author.getId()
        );

        // multipart/form-data로 요청 생성
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("messageCreateRequest", createRequest);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        // when: 메시지 생성 API 호출
        ResponseEntity<MessageDto> response = restTemplate.exchange(
            "/api/messages",
            HttpMethod.POST,
            new HttpEntity<>(body, headers),
            MessageDto.class
        );

        // then: 응답 상태코드와 데이터 확인
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().content()).isEqualTo("테스트 메시지 내용");
        assertThat(response.getBody().channelId()).isEqualTo(channel.getId());
        assertThat(response.getBody().author().id()).isEqualTo(author.getId());

        // DB에서 실제 저장되었는지 확인
        Message savedMessage = messageRepository.findById(response.getBody().id()).orElse(null);
        assertThat(savedMessage).isNotNull();
        assertThat(savedMessage.getContent()).isEqualTo("테스트 메시지 내용");
    }

    /**
     * [성공] 메시지를 DB에서 수정할 수 있는지 검증합니다.
     */
    @Test
    @DisplayName("[성공] 메시지 DB 수정")
    void shouldUpdateMessageInDatabase() {
        // given: 기존 메시지를 DB에 저장
        User author = userRepository.save(new User("testauthor_update", "author_update@test.com", "password123", null));
        Channel channel = channelRepository.save(new Channel(ChannelType.PUBLIC, "테스트 채널", "테스트 채널 설명"));
        Message existingMessage = messageRepository.save(new Message("기존 메시지", channel, author, null));
        
        MessageUpdateRequest updateRequest = new MessageUpdateRequest("수정된 메시지 내용");

        // when: 메시지 수정 API 호출
        ResponseEntity<MessageDto> response = restTemplate.exchange(
            "/api/messages/" + existingMessage.getId(),
            HttpMethod.PATCH,
            new HttpEntity<>(updateRequest),
            MessageDto.class
        );

        // then: 응답 상태코드와 데이터 확인
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().content()).isEqualTo("수정된 메시지 내용");

        // DB에서 실제 수정되었는지 확인
        Message updatedMessage = messageRepository.findById(existingMessage.getId()).orElse(null);
        assertThat(updatedMessage).isNotNull();
        assertThat(updatedMessage.getContent()).isEqualTo("수정된 메시지 내용");
    }

    /**
     * [성공] 메시지를 DB에서 삭제할 수 있는지 검증합니다.
     */
    @Test
    @DisplayName("[성공] 메시지 DB 삭제")
    void shouldDeleteMessageFromDatabase() {
        // given: 메시지를 DB에 저장
        User author = userRepository.save(new User("testauthor_delete", "author_delete@test.com", "password123", null));
        Channel channel = channelRepository.save(new Channel(ChannelType.PUBLIC, "테스트 채널", "테스트 채널 설명"));
        Message message = messageRepository.save(new Message("삭제할 메시지", channel, author, null));
        UUID messageId = message.getId();

        // when: 메시지 삭제 API 호출
        ResponseEntity<Void> response = restTemplate.exchange(
            "/api/messages/" + messageId,
            HttpMethod.DELETE,
            null,
            Void.class
        );

        // then: 응답 상태코드 확인
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        // DB에서 실제 삭제되었는지 확인
        Message deletedMessage = messageRepository.findById(messageId).orElse(null);
        assertThat(deletedMessage).isNull();
    }

    /**
     * [성공] 메시지를 DB에서 조회할 수 있는지 검증합니다.
     */
    @Test
    @DisplayName("[성공] 메시지 DB 조회")
    void shouldRetrieveMessageFromDatabase() {
        // given: 메시지를 DB에 저장
        User author = userRepository.save(new User("testauthor_retrieve", "author_retrieve@test.com", "password123", null));
        Channel channel = channelRepository.save(new Channel(ChannelType.PUBLIC, "테스트 채널", "테스트 채널 설명"));
        Message message = messageRepository.save(new Message("조회할 메시지", channel, author, null));

        // when: 메시지 조회 API 호출
        ResponseEntity<MessageDto> response = restTemplate.exchange(
            "/api/messages/" + message.getId(),
            HttpMethod.GET,
            null,
            MessageDto.class
        );

        // then: 응답 상태코드와 데이터 확인
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().content()).isEqualTo("조회할 메시지");
        assertThat(response.getBody().channelId()).isEqualTo(channel.getId());
        assertThat(response.getBody().author().id()).isEqualTo(author.getId());
    }

    /**
     * [성공] 채널별 메시지 목록을 DB에서 조회할 수 있는지 검증합니다.
     */
    @Test
    @DisplayName("[성공] 채널별 메시지 목록 DB 조회")
    void shouldRetrieveMessagesByChannelId() {
        // given: 사용자와 채널을 API로 생성
        UserCreateRequest userCreateRequest = new UserCreateRequest(
            "testauthor_list", "author_list@test.com", "password123"
        );
        MultiValueMap<String, Object> userBody = new LinkedMultiValueMap<>();
        userBody.add("userCreateRequest", userCreateRequest);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        ResponseEntity<UserDto> userResponse = restTemplate.exchange(
            "/api/users",
            HttpMethod.POST,
            new HttpEntity<>(userBody, headers),
            UserDto.class
        );
        UserDto author = userResponse.getBody();

        PublicChannelCreateRequest channelCreateRequest = new PublicChannelCreateRequest(
            "테스트 채널", "테스트 채널 설명"
        );
        ResponseEntity<ChannelDto> channelResponse = restTemplate.postForEntity(
            "/api/channels/public",
            channelCreateRequest,
            ChannelDto.class
        );
        ChannelDto channel = channelResponse.getBody();

        // 메시지 3개 API로 생성
        for (int i = 1; i <= 3; i++) {
            MessageCreateRequest createRequest = new MessageCreateRequest(
                "메시지" + i, channel.id(), author.id()
            );
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("messageCreateRequest", createRequest);
            restTemplate.exchange(
                "/api/messages",
                HttpMethod.POST,
                new HttpEntity<>(body, headers),
                MessageDto.class
            );
        }

        // when: 채널별 메시지 목록 조회 API 호출
        ResponseEntity<PageResponse<MessageDto>> response = restTemplate.exchange(
            "/api/messages?channelId=" + channel.id() + "&page=0&size=10",
            HttpMethod.GET,
            null,
            new ParameterizedTypeReference<PageResponse<MessageDto>>() {}
        );

        // then: 응답 상태코드와 데이터 확인
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().content()).hasSizeGreaterThanOrEqualTo(3);
        
        // 메시지 내용 확인 - 안전한 타입 변환
        List<MessageDto> messages = response.getBody().content().stream()
            .map(obj -> (MessageDto) obj)
            .toList();
        assertThat(messages).anyMatch(msg -> msg.content().equals("메시지1"));
        assertThat(messages).anyMatch(msg -> msg.content().equals("메시지2"));
        assertThat(messages).anyMatch(msg -> msg.content().equals("메시지3"));
    }
} 