package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.dto.channel.ChannelResponseDto;
import com.sprint.mission.discodeit.dto.channel.PrivateChannelDto;
import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@DisplayName("ChannelService 통합 테스트")
@Transactional
public class ChannelServiceIntegrationTest {

    @Autowired
    private ChannelRepository channelRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ReadStatusRepository readStatusRepository;
    @Autowired
    private ChannelService channelService;

    private UUID userId1;
    private UUID userId2;
    private UUID userId3;

    @BeforeEach
    void setUp() {
        // given
        User user1 = User.builder()
                .username("test")
                .email("test@test.com")
                .password("pwd1234")
                .build();

        User user2 = User.builder()
                .username("test2")
                .email("test2@test.com")
                .password("pwd12345")
                .build();

        User user3 = User.builder()
                .username("test3")
                .email("test3@test.com")
                .password("pwd123456")
                .build();

        UserStatus userStatus1 = UserStatus.builder()
                .user(user1)
                .lastActiveAt(Instant.now())
                .build();

        UserStatus userStatus2 = UserStatus.builder()
                .user(user2)
                .lastActiveAt(Instant.now())
                .build();

        UserStatus userStatus3 = UserStatus.builder()
                .user(user3)
                .lastActiveAt(Instant.now())
                .build();

        user1.updateStatus(userStatus1);
        user2.updateStatus(userStatus2);
        user3.updateStatus(userStatus3);

        User savedUser1 = userRepository.save(user1);
        User savedUser2 = userRepository.save(user2);
        User savedUser3 = userRepository.save(user3);

        userId1 = savedUser1.getId();
        userId2 = savedUser2.getId();
        userId3 = savedUser3.getId();
    }

    @Test
    @DisplayName("Private 채널 등록 프로세스가 모든 계층에서 올바르게 동작해야 한다.")
    void completeCreatePrivateChannelIntegration() {

        // given
        PrivateChannelDto request = new PrivateChannelDto(List.of(userId1, userId2));

        // when
        ChannelResponseDto result = channelService.createPrivateChannel(request);

        UUID channelId = result.id();

        // then
        Optional<Channel> foundChannel = channelRepository.findById(channelId);
        assertTrue(foundChannel.isPresent());

        assertNotNull(result);
        assertEquals(ChannelType.PRIVATE, result.type());
        assertEquals(result.participants().get(0).id(), userId1);
        // 채널에 메시지가 없기때문에 lastMessageAt은 null
        assertNull(result.lastMessageAt());

        List<ReadStatus> foundReadStatus1 = readStatusRepository.findAllByUserId(userId1);
        assertEquals(1, foundReadStatus1.size());
    }

    @Test
    @DisplayName("사용자 ID로 채널 조회 프로세스가 모든 계층에서 올바르게 동작해야 한다.")
    void completeFindByUserIdIntegration() {

        // given
        Channel publicChannel = Channel.builder()
                .name("public")
                .description("test channel")
                .type(ChannelType.PUBLIC)
                .build();

        /*
            Private 채널 요청
         */
        PrivateChannelDto request1 = new PrivateChannelDto(List.of(userId1, userId2));
        PrivateChannelDto request2 = new PrivateChannelDto(List.of(userId2, userId3));

        Channel savedChannel1 = channelRepository.save(publicChannel);
        ChannelResponseDto privateChannel1 = channelService.createPrivateChannel(request1);
        ChannelResponseDto privateChannel2 = channelService.createPrivateChannel(request2);

        UUID channelId1 = savedChannel1.getId();
        UUID channelId2 = privateChannel1.id();
        UUID channelId3 = privateChannel2.id();

        // when
        List<ChannelResponseDto> channels = channelService.findAllByUserId(userId1);

        // then
        assertEquals(2, channels.size());
        List<UUID> resultIds = channels.stream().map(ChannelResponseDto::id).toList();

        assertTrue(resultIds.contains(channelId1));
        assertTrue(resultIds.contains(channelId2));
        assertFalse(resultIds.contains(channelId3));
    }
}
