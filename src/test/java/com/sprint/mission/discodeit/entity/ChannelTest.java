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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class ChannelTest {

  @Nested
  class Create {

    @Test
    void 채널이_생성되면_기본_정보가_올바르게_설정되어야_한다() {
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
    void DTO_파라미터_사용과_채널_타입을_구분해서_생성할_수_있다() {
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
  class Read {

    private Channel channel;
    private User creator;
    private User participant;

    @BeforeEach
    void setUp() {
      creator = UserFixture.createValidUser();
      channel = ChannelFixture.createCustomChannelWithParticipant(
          creator, ChannelFixture.DEFAULT_CHANNEL_NAME, ChannelFixture.DEFAULT_CHANNEL_DESCRIPTION);
      participant = UserFixture.createValidUser();
      channel.addParticipant(participant.getId());
    }

    @Test
    void 참여자_목록_조회_시_불변성이_보장되어야_한다() {
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
  class Update {

    private Channel channel;
    private User creator;
    private Instant originalUpdatedAt;

    @BeforeEach
    void setUp() {
      creator = UserFixture.createValidUser();
      channel = ChannelFixture.createCustomChannelWithParticipant(
          creator, ChannelFixture.DEFAULT_CHANNEL_NAME, ChannelFixture.DEFAULT_CHANNEL_DESCRIPTION);
      originalUpdatedAt = channel.getUpdatedAt();
    }

    @Test
    void 채널_이름_수정_시_이름과_수정_시간이_업데이트되어야_한다() {
      String newName = "변경된 채널명";
      channel.updateName(newName);

      assertAll(
          () -> assertThat(channel.getName()).isEqualTo(newName),
          () -> assertThat(channel.getUpdatedAt()).isAfterOrEqualTo(originalUpdatedAt)
      );
    }

    @ParameterizedTest
    @ValueSource(strings = {"감자", "왕감자", "고구마"})
    void 채널_이름_수정_테스트_여러_데이터(String newName) {
      channel.updateName(newName);

      assertAll(
          () -> assertThat(channel.getName()).isEqualTo(newName),
          () -> assertThat(channel.getUpdatedAt()).isAfterOrEqualTo(originalUpdatedAt)
      );
    }

    @Test
    void 새로운_참여자_추가_시_참여자_목록이_올바르게_갱신되어야_한다() {
      User newParticipant = UserFixture.createValidUser();
      channel.addParticipant(newParticipant.getId());

      assertAll(
          () -> assertThat(channel.getParticipants()).contains(newParticipant.getId()),
          () -> assertThat(channel.getParticipants()).hasSize(2),
          () -> assertThat(channel.getUpdatedAt()).isAfterOrEqualTo(originalUpdatedAt)
      );
    }

    @Test
    void 중복된_참여자_추가_시_예외가_발생해야_한다() {
      assertAll(
          () -> assertThatThrownBy(() -> channel.addParticipant(creator.getId()))
              .isInstanceOf(ChannelException.class),
          () -> assertThat(channel.getUpdatedAt()).isEqualTo(originalUpdatedAt)
      );
    }

    @Test
    void 참여자_제거_시_목록에서_삭제되어야_한다() {
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
