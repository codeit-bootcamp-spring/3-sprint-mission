package com.sprint.mission.discodeit.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;

import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
@WithMockUser(roles = "CHANNEL_MANAGER")
@DisplayName("ChannelService 통합 테스트")
public class ChannelServiceIntegrationTest {

    @Autowired private ChannelService channelService;
    @Autowired private ChannelRepository channelRepository;

    @Test
    @DisplayName("모든 계층의 공개 채널 생성 프로세스")
    void createPublicChannel() {
        // given
        PublicChannelCreateRequest publicChannelCreateRequest = new PublicChannelCreateRequest("public", "public channel");

        // when
        ChannelDto channelDto = channelService.create(publicChannelCreateRequest);

        // then
        Channel channel = channelRepository.findById(channelDto.id()).orElseThrow();
        assertThat(channelDto.name()).isEqualTo(channel.getName());
        assertThat(channelDto.id()).isEqualTo(channel.getId());
    }

    @Test
    @DisplayName("모든 계층의 공개 채널 수정 프로세스")
    void updatePublicChannelIntegration() {
        // given
        PublicChannelCreateRequest publicChannelCreateRequest = new PublicChannelCreateRequest("public", "public channel");
        ChannelDto channelDto = channelService.create(publicChannelCreateRequest);

        PublicChannelUpdateRequest publicChannelUpdateRequest = new PublicChannelUpdateRequest("publicChannel", "public-channel");

        // when
        ChannelDto updatedChannelDto = channelService.update(channelDto.id(), publicChannelUpdateRequest);

        Channel channel = channelRepository.findById(updatedChannelDto.id())
            .orElseThrow(() -> new AssertionError("채널이 존재하지 않습니다: " + updatedChannelDto.id()));
        assertThat(channel.getName()).isEqualTo("publicChannel");
        assertThat(channel.getDescription()).isEqualTo("public-channel");
    }

    @Test
    @DisplayName("모든 계층의 채널 삭제 프로세스")
    void deleteChannelIntegration() {
        // given
        PublicChannelCreateRequest publicChannelCreateRequest = new PublicChannelCreateRequest("public", "public channel");
        ChannelDto channelDto = channelService.create(publicChannelCreateRequest);
        UUID channelId = channelDto.id();

        // when
        channelService.delete(channelId);

        // then
        assertFalse(channelRepository.findById(channelId).isPresent(), "채널이 삭제되지 않았습니다: " + channelId);
    }
}