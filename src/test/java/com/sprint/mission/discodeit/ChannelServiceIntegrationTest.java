package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.repository.*;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@DisplayName("채널 서비스 통합 테스트")
@Transactional
public class ChannelServiceIntegrationTest {

    @Autowired private ChannelService channelService;
    @Autowired private UserRepository userRepository;
    @Autowired private ChannelRepository channelRepository;
    @Autowired private ReadStatusRepository readStatusRepository;
    @Autowired private UserStatusRepository userStatusRepository;
    @Autowired private BinaryContentRepository binaryContentRepository;
    @Autowired private BinaryContentStorage binaryContentStorage;
    @Autowired private MessageRepository messageRepository;

    @Test
    @DisplayName("PUBLIC 채널 생성 요청 시 모든 계층에서 올바르게 동작해야 한다")
    void createPublicChannelProcessIntegration() {
        // given
        PublicChannelCreateRequest publicChannelCreateRequest = new PublicChannelCreateRequest("test", "test");

        // when
        ChannelDto channelDto = channelService.create(publicChannelCreateRequest);

        // then
        Channel channel = channelRepository.findById(channelDto.id()).orElseThrow();
        assertThat(channelDto.name()).isEqualTo(channel.getName());
        assertThat(channelDto.id()).isEqualTo(channel.getId());
    }

    /*
    * PRIVATE 채널 생성 시 모든 계층에서 정상동작 하는지 검증한다
    * 채널 생성, 참가한 유저들의 ReadStatus 생성 까지의
    * 모든 비즈니스 플로우를 데이터베이스와 함께 검증한다
    * */
    @Test
    @DisplayName("PRIVATE 채널 생성 요청 시 모든 계층에서 올바르게 동작해야 한다")
    void createPrivateChannelProcessIntegration() {
        // given
        BinaryContent binaryContent = new BinaryContent("test", 100L, "png");
        binaryContentRepository.save(binaryContent);
        binaryContentRepository.flush();

        binaryContentStorage.put(binaryContent.getId(), "test File".getBytes());

        BinaryContent findBinaryContent = binaryContentRepository.findById(binaryContent.getId()).orElseThrow();

        User user = userRepository.save(new User("test", "test@gmail.com", "test1234", findBinaryContent));
        UserStatus userStatus = userStatusRepository.save(new UserStatus(user, Instant.now()));
        user.setUserStatus(userStatus);

        userRepository.flush();
        userStatusRepository.flush();

        List<UUID> userIds = Arrays.asList(user.getId());

        PrivateChannelCreateRequest privateChannelCreateRequest = new PrivateChannelCreateRequest(userIds);
        // when
        ChannelDto channelDto = channelService.create(privateChannelCreateRequest);

        // then
        Channel channel = channelRepository.findById(channelDto.id()).orElseThrow();
        assertThat(channelDto.type().name()).isEqualTo(channel.getType().name());
        assertThat(channelDto.participants().get(0).id()).isEqualTo(userIds.get(0));

        List<ReadStatus> foundReadStatuses = readStatusRepository.findAllByChannelId(channelDto.id());
        assertThat(foundReadStatuses.size()).isEqualTo(1);
        assertThat(foundReadStatuses.get(0).getUser().getId()).isEqualTo(user.getId());
    }

    @Test
    @DisplayName("PUBLIC 채널 수정 요청 시 모든 계층에서 올바르게 동작해야 한다")
    void updatePublicChannelProcessIntegration() {
        // given
        ChannelDto channelDto = channelService.create(new PublicChannelCreateRequest("test", "test"));

        PublicChannelUpdateRequest publicChannelUpdateRequest = new PublicChannelUpdateRequest("수정", "수정");
        // when
        ChannelDto updateChannel = channelService.update(channelDto.id(), publicChannelUpdateRequest);

        // then
        Channel foundChannel = channelRepository.findById(channelDto.id()).orElseThrow();

        assertThat(updateChannel.name()).isEqualTo(foundChannel.getName());
        assertThat(updateChannel.id()).isEqualTo(foundChannel.getId());
    }

    /*
    * 채널 삭제 시 모든 메세지를 삭제해아 한다
    * */
    @Test
    @DisplayName("채널 삭제 요청 시 모든 계층에서 올바르게 동작해야 한다")
    void deleteChannelProcessIntegration() {
        // given
        BinaryContent binaryContent = new BinaryContent("test", 100L, "png");
        binaryContentRepository.save(binaryContent);
        binaryContentRepository.flush();

        binaryContentStorage.put(binaryContent.getId(), "test File".getBytes());

        BinaryContent findBinaryContent = binaryContentRepository.findById(binaryContent.getId()).orElseThrow();

        User user = userRepository.save(new User("test", "test@gmail.com", "test1234", findBinaryContent));
        UserStatus userStatus = userStatusRepository.save(new UserStatus(user, Instant.now()));
        user.setUserStatus(userStatus);

        userRepository.flush();
        userStatusRepository.flush();

        Channel channel = channelRepository.save(new Channel(ChannelType.PUBLIC, "test", "test"));

        messageRepository.save(new Message("test1", channel, user));
        messageRepository.save(new Message("test2", channel, user));
        messageRepository.save(new Message("test3", channel, user));
        // when
        channelService.delete(channel.getId());

        // then
        assertThat(channelRepository.findById(channel.getId())).isEmpty();
        assertThat(messageRepository.findAllByChannelId(channel.getId())).isEmpty();
    }
}
