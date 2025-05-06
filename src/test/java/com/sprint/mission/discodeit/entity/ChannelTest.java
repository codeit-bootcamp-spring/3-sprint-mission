package com.sprint.mission.discodeit.entity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.entity.Channel.ChannelType;
import com.sprint.mission.discodeit.exception.ChannelException;
import com.sprint.mission.discodeit.fixture.ChannelFixture;
import com.sprint.mission.discodeit.fixture.UserFixture;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class ChannelTest {

  @Nested
  @DisplayName("채널 생성")
  class Create {

    @Test
    @DisplayName("채널이 생성되면 기본 정보가 올바르게 설정되어야 한다")
    void shouldCreateChannelWithDefaultInfo() {
      var creator = UserFixture.createValidUser();

      Channel channel = ChannelFixture.createCustomChannelWithParticipant(
          creator, ChannelFixture.DEFAULT_CHANNEL_NAME, ChannelFixture.DEFAULT_CHANNEL_DESCRIPTION);

      assertAll(
          () -> assertThat(channel.getId()).isNotNull(),
          () -> assertThat(channel.getName()).isEqualTo(ChannelFixture.DEFAULT_CHANNEL_NAME),
          () -> assertThat(channel.getCreatedAt()).isNotNull(),
          () -> assertThat(channel.getUpdatedAt()).isEqualTo(channel.getCreatedAt()),
          () -> assertThat(channel.getParticipants()).contains(creator.getId())
      );
    }

    @Test
    @DisplayName("DTO 파라미터 사용, 채널 타입을 구분해서 생성할 수 있다")
    void channelCreate_withDTOAndType() {
      var creator = UserFixture.createValidUser();
      PublicChannelCreateRequest publicDto = new PublicChannelCreateRequest(
          creator.getId(), "공개 채널", "공개 채널 설명");
      PrivateChannelCreateRequest privateDto = new PrivateChannelCreateRequest(
          creator.getId(), List.of());

      Channel publicChannel = ChannelFixture.createCustomPublicChannelWithParticipant(publicDto);
      Channel privateChannel = ChannelFixture.createCustomPrivateChannelWithParticipant(privateDto);

      assertAll(
          () -> assertThat(publicChannel.getType()).isEqualTo(ChannelType.PUBLIC),
          () -> assertThat(privateChannel.getType()).isEqualTo(ChannelType.PRIVATE),
          () -> assertThat(publicChannel.getParticipants()).contains(creator.getId()),
          () -> assertThat(privateChannel.getParticipants()).contains(creator.getId())
      );
    }
  }

  @Nested
  @DisplayName("채널 정보 조회")
  class Read {

    private Channel channel;
    private User creator;
    private User participant;

    @org.junit.jupiter.api.BeforeEach
    void setUp() {
      creator = UserFixture.createValidUser();
      channel = ChannelFixture.createCustomChannelWithParticipant(
          creator, ChannelFixture.DEFAULT_CHANNEL_NAME, ChannelFixture.DEFAULT_CHANNEL_DESCRIPTION);
      participant = UserFixture.createValidUser();
      channel.addParticipant(participant.getId());
    }

    @Test
    @DisplayName("참여자 목록 조회 시 불변성이 보장되어야 한다")
    void shouldReturnImmutableParticipantsList() {
      List<UUID> participants = channel.getParticipants();

      assertAll(
          () -> assertThatThrownBy(() -> participants.clear()).isInstanceOf(
              UnsupportedOperationException.class),
          () -> assertThat(channel.getParticipants()).hasSize(2),
          () -> assertThat(channel.getParticipants()).containsExactlyInAnyOrder(creator.getId(),
              participant.getId())
      );
    }
  }

  @Nested
  @DisplayName("채널 정보 수정")
  class Update {

    private Channel channel;
    private User creator;
    private Instant originalUpdatedAt;

    @org.junit.jupiter.api.BeforeEach
    void setUp() {
      creator = UserFixture.createValidUser();
      channel = ChannelFixture.createCustomChannelWithParticipant(
          creator, ChannelFixture.DEFAULT_CHANNEL_NAME, ChannelFixture.DEFAULT_CHANNEL_DESCRIPTION);
      originalUpdatedAt = channel.getUpdatedAt();
    }

    @Test
    @DisplayName("채널 이름 수정 시 이름과 수정 시간이 업데이트되어야 한다")
    void shouldUpdateNameAndTimestamp() {
      String newName = "변경된 채널명";
      channel.updateName(newName);

      assertAll(
          () -> assertThat(channel.getName()).isEqualTo(newName),
          () -> assertThat(channel.getUpdatedAt()).isAfterOrEqualTo(originalUpdatedAt)
      );
    }

    @ParameterizedTest
    @ValueSource(strings = {"감자", "왕감자", "고구마"})
    @DisplayName("채널 이름 수정 테스트 여러 데이터")
    void shouldUpdateNameAndTimestampParameterized(String newName) {
      channel.updateName(newName);

      assertAll(
          () -> assertThat(channel.getName()).isEqualTo(newName),
          () -> assertThat(channel.getUpdatedAt()).isAfterOrEqualTo(originalUpdatedAt)
      );
    }

    @Test
    @DisplayName("새로운 참여자 추가 시 참여자 목록이 올바르게 갱신되어야 한다")
    void shouldAddNewParticipant() {
      User newParticipant = UserFixture.createValidUser();
      channel.addParticipant(newParticipant.getId());

      assertAll(
          () -> assertThat(channel.getParticipants()).contains(newParticipant.getId()),
          () -> assertThat(channel.getParticipants()).hasSize(2),
          () -> assertThat(channel.getUpdatedAt()).isAfterOrEqualTo(originalUpdatedAt)
      );
    }

    @Test
    @DisplayName("중복된 참여자 추가 시 예외가 발생해야 한다")
    void shouldThrowExceptionForDuplicateParticipant() {
      assertAll(
          () -> assertThatThrownBy(() -> channel.addParticipant(creator.getId()))
              .isInstanceOf(ChannelException.class),
          () -> assertThat(channel.getUpdatedAt()).isEqualTo(originalUpdatedAt)
      );
    }

    @Test
    @DisplayName("참여자 제거 시 목록에서 삭제되어야 한다")
    void shouldRemoveParticipant() {
      User participant = UserFixture.createValidUser();
      channel.addParticipant(participant.getId());

      Instant beforeRemovalUpdatedAt = channel.getUpdatedAt();
      channel.removeParticipant(participant.getId());

      assertAll(
          () -> assertThat(channel.getParticipants()).doesNotContain(participant.getId()),
          () -> assertThat(channel.getParticipants()).hasSize(1),
          () -> assertThat(channel.getUpdatedAt()).isAfterOrEqualTo(beforeRemovalUpdatedAt)
      );
    }
  }
}
