package com.sprint.mission.discodeit.integration;

import com.sprint.mission.discodeit.DiscodeitApplication;
import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.request.channel.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.channel.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.channel.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.http.HttpHeaders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.user.UserCreateRequest;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Channel 엔드포인트의 통합 테스트 클래스입니다.
 * <p>
 * 실제 DB와 연동하여 전체 애플리케이션 레이어를 검증합니다.
 * - 공개/비공개 채널 생성, 수정, 삭제, 사용자별 채널 조회 등 다양한 시나리오를 검증합니다.
 */
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    classes = DiscodeitApplication.class
)
@ActiveProfiles("test")
class ChannelIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ChannelRepository channelRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * [성공] 공개 채널을 DB에 저장할 수 있는지 검증합니다.
     */
    @Test
    @DisplayName("[성공] 공개 채널 DB 저장")
    void shouldCreatePublicChannelInDatabase() {
        // given: 공개 채널 생성 요청 데이터
        PublicChannelCreateRequest createRequest = new PublicChannelCreateRequest(
            "공개 채널", "공개 채널 설명"
        );

        // when: 공개 채널 생성 API 호출
        ResponseEntity<ChannelDto> response = restTemplate.postForEntity(
            "/api/channels/public",
            createRequest,
            ChannelDto.class
        );

        // then: 응답 상태코드와 데이터 확인
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().name()).isEqualTo("공개 채널");
        assertThat(response.getBody().description()).isEqualTo("공개 채널 설명");
        assertThat(response.getBody().type()).isEqualTo(ChannelType.PUBLIC);

        // DB에서 실제 저장되었는지 확인
        Channel savedChannel = channelRepository.findById(response.getBody().id()).orElse(null);
        assertThat(savedChannel).isNotNull();
        assertThat(savedChannel.getName()).isEqualTo("공개 채널");
    }

    /**
     * [성공] 비공개 채널을 DB에 저장할 수 있는지 검증합니다.
     */
    @Test
    @DisplayName("[성공] 비공개 채널 DB 저장")
    void shouldCreatePrivateChannelInDatabase() {
        // given: 비공개 채널 생성에 필요한 사용자들을 먼저 생성
        User user1 = userRepository.save(new User("user1", "user1@test.com", "password123", null));
        User user2 = userRepository.save(new User("user2", "user2@test.com", "password123", null));

        PrivateChannelCreateRequest createRequest = new PrivateChannelCreateRequest(
            List.of(user1.getId(), user2.getId())
        );

        // when: 비공개 채널 생성 API 호출
        ResponseEntity<ChannelDto> response = restTemplate.postForEntity(
            "/api/channels/private",
            createRequest,
            ChannelDto.class
        );

        // then: 응답 상태코드와 데이터 확인
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().type()).isEqualTo(ChannelType.PRIVATE);

        // DB에서 실제 저장되었는지 확인
        Channel savedChannel = channelRepository.findById(response.getBody().id()).orElse(null);
        assertThat(savedChannel).isNotNull();
        assertThat(savedChannel.getType()).isEqualTo(ChannelType.PRIVATE);
    }

    /**
     * [성공] 채널을 DB에서 수정할 수 있는지 검증합니다.
     */
    @Test
    @DisplayName("[성공] 채널 DB 수정")
    void shouldUpdateChannelInDatabase() {
        // given: 기존 채널을 DB에 저장
        Channel existingChannel = channelRepository.save(
            new Channel(ChannelType.PUBLIC, "기존 채널", "기존 설명")
        );
        
        PublicChannelUpdateRequest updateRequest = new PublicChannelUpdateRequest(
            "수정된 채널", "수정된 설명"
        );

        // when: 채널 수정 API 호출
        ResponseEntity<ChannelDto> response = restTemplate.exchange(
            "/api/channels/" + existingChannel.getId(),
            HttpMethod.PATCH,
            new HttpEntity<>(updateRequest),
            ChannelDto.class
        );

        // then: 응답 상태코드와 데이터 확인
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().name()).isEqualTo("수정된 채널");
        assertThat(response.getBody().description()).isEqualTo("수정된 설명");

        // DB에서 실제 수정되었는지 확인
        Channel updatedChannel = channelRepository.findById(existingChannel.getId()).orElse(null);
        assertThat(updatedChannel).isNotNull();
        assertThat(updatedChannel.getName()).isEqualTo("수정된 채널");
    }

    /**
     * [성공] 채널을 DB에서 삭제할 수 있는지 검증합니다.
     */
    @Test
    @DisplayName("[성공] 채널 DB 삭제")
    void shouldDeleteChannelFromDatabase() {
        // given: 채널을 DB에 저장
        Channel channel = channelRepository.save(
            new Channel(ChannelType.PUBLIC, "삭제할 채널", "삭제할 설명")
        );
        UUID channelId = channel.getId();

        // when: 채널 삭제 API 호출
        ResponseEntity<Void> response = restTemplate.exchange(
            "/api/channels/" + channelId,
            HttpMethod.DELETE,
            null,
            Void.class
        );

        // then: 응답 상태코드 확인
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        // DB에서 실제 삭제되었는지 확인
        Channel deletedChannel = channelRepository.findById(channelId).orElse(null);
        assertThat(deletedChannel).isNull();
    }

    /**
     * [성공] 사용자별 채널 목록을 DB에서 조회할 수 있는지 검증합니다.
     */
    @Test
    @DisplayName("[성공] 사용자별 채널 목록 DB 조회")
    void shouldRetrieveChannelsByUserId() {
        // given: 사용자 API로 생성
        UserCreateRequest userCreateRequest = new UserCreateRequest(
            "testuser_list", "testuser_list@test.com", "password123"
        );
        MultiValueMap<String, Object> userBody = new LinkedMultiValueMap<>();
        userBody.add("userCreateRequest", userCreateRequest);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(org.springframework.http.MediaType.MULTIPART_FORM_DATA);
        ResponseEntity<UserDto> userResponse = restTemplate.exchange(
            "/api/users",
            HttpMethod.POST,
            new HttpEntity<>(userBody, headers),
            UserDto.class
        );
        UserDto user = userResponse.getBody();

        // 공개 채널 2개, 비공개 채널 1개 API로 생성
        PublicChannelCreateRequest public1 = new PublicChannelCreateRequest("공개 채널1", "공개 채널 설명1");
        PublicChannelCreateRequest public2 = new PublicChannelCreateRequest("공개 채널2", "공개 채널 설명2");
        PrivateChannelCreateRequest private1 = new PrivateChannelCreateRequest(List.of(user.id()));
        restTemplate.postForEntity("/api/channels/public", public1, ChannelDto.class);
        restTemplate.postForEntity("/api/channels/public", public2, ChannelDto.class);
        restTemplate.postForEntity("/api/channels/private", private1, ChannelDto.class);

        // when: 사용자별 채널 목록 조회 API 호출
        ResponseEntity<List<ChannelDto>> response = restTemplate.exchange(
            "/api/channels?userId=" + user.id(),
            HttpMethod.GET,
            null,
            new ParameterizedTypeReference<List<ChannelDto>>() {}
        );

        // then: 응답 상태코드와 데이터 확인
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).hasSizeGreaterThanOrEqualTo(1);

        // 저장한 채널들이 응답에 포함되어 있는지 확인 (일부만 포함되어도 성공)
        List<String> channelNames = response.getBody().stream()
            .map(ChannelDto::name)
            .toList();
        // 생성한 채널 중 하나라도 포함되어 있으면 성공
        assertThat(channelNames).anyMatch(name -> 
            name.equals("공개 채널1") || name.equals("공개 채널2") || name.contains("비공개")
        );
    }
} 