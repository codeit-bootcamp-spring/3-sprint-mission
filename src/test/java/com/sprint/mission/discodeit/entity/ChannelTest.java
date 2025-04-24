package com.sprint.mission.discodeit.entity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.sprint.mission.discodeit.exception.ChannelException;
import com.sprint.mission.discodeit.fixture.ChannelFixture;
import com.sprint.mission.discodeit.fixture.UserFixture;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class ChannelTest {

  @Nested
  @DisplayName("채널 생성")
  class Create {

    @Test
    @DisplayName("채널이 생성되면 기본 정보가 올바르게 설정되어야 한다")
    void shouldCreateChannelWithDefaultInfo() {
      // when
      Channel channel = ChannelFixture.createDefaultChannel();

      // then
      assertAll(
          "채널 기본 정보 검증",
          () -> assertThat(channel.getId()).as("채널 ID는 null이 아니어야 함").isNotNull(),
          () -> assertThat(channel.getName())
              .as("채널 이름이 올바르게 설정되어야 함")
              .isEqualTo(ChannelFixture.DEFAULT_CHANNEL_NAME),
          () -> assertThat(channel.getCreatedAt())
              .as("생성 시간이 올바르게 설정되어야 함")
              .isPositive(),
          () -> assertThat(channel.getUpdatedAt())
              .as("최초 수정 시간은 생성 시간과 동일해야 함")
              .isEqualTo(channel.getCreatedAt())
      );
    }

    @Test
    @DisplayName("채널 생성 시 생성자는 자동으로 참여자로 등록되어야 한다")
    void shouldAddCreatorAsParticipant() {
      // given
      User creator = UserFixture.createDefaultUser();

      // when
      Channel channel = ChannelFixture.createCustomChannel(creator, "테스트 채널");

      // then
      assertAll(
          "채널 생성자 정보 검증",
          () -> assertThat(channel.getCreator())
              .as("생성자가 올바르게 설정되어야 함")
              .isEqualTo(creator),
          () -> assertThat(channel.getParticipants())
              .as("참여자 목록에 생성자가 포함되어야 함")
              .contains(creator),
          () -> assertThat(channel.getParticipants())
              .as("최초 참여자는 생성자 1명이어야 함")
              .hasSize(1)
      );
    }
  }

  @Nested
  @DisplayName("채널 정보 조회")
  class Read {

    @Test
    @DisplayName("참여자 목록 조회 시 불변성이 보장되어야 한다")
    void shouldReturnImmutableParticipantsList() {
      // given
      Channel channel = ChannelFixture.createDefaultChannel();
      User participant = UserFixture.createDefaultUser();
      channel.addParticipant(participant);

      // when
      List<User> participants = channel.getParticipants();
      participants.clear(); // 반환된 리스트 수정 시도

      // then
      assertAll(
          "참여자 목록 불변성 검증",
          () -> assertThat(channel.getParticipants())
              .as("원본 참여자 목록이 변경되지 않아야 함")
              .hasSize(2),
          () -> assertThat(channel.getParticipants())
              .as("모든 참여자가 유지되어야 함")
              .containsExactlyInAnyOrder(channel.getCreator(), participant)
      );
    }
  }

  @Nested
  @DisplayName("채널 정보 수정")
  class Update {

    @Test
    @DisplayName("채널 정보 수정 시 수정 정보와 시간이 업데이트되어야 한다")
    void shouldUpdateNameAndTimestamp() throws InterruptedException {
      // given
      Channel channel = ChannelFixture.createDefaultChannel();
      String newName = "변경된 채널명";
      long originalUpdatedAt = channel.getUpdatedAt();

      Thread.sleep(1);

      channel.updateName(newName);

      // when & then
      assertAll(
          "채널 정보 수정 검증",
          () -> assertThat(channel.getName())
              .as("새로운 이름으로 변경되어야 함")
              .isEqualTo(newName),
          () ->
              assertThat(channel.getUpdatedAt())
                  .as("수정 시간이 갱신되어야 함")
                  .isGreaterThan(originalUpdatedAt)
      );
    }

    @Test
    @DisplayName("새로운 참여자 추가 시 참여자 목록이 올바르게 갱신되어야 한다")
    void shouldAddNewParticipant() {
      // given
      Channel channel = ChannelFixture.createDefaultChannel();
      User newParticipant = UserFixture.createDefaultUser();
      long originalUpdatedAt = channel.getUpdatedAt();

      // when
      channel.addParticipant(newParticipant);

      // then
      assertAll(
          "참여자 추가 검증",
          () -> assertThat(channel.getParticipants())
              .as("참여자 목록에 새로운 참여자가 포함되어야 함")
              .contains(newParticipant),
          () -> assertThat(channel.getParticipants())
              .as("참여자 수가 증가해야 함")
              .hasSize(2),
          () -> assertThat(channel.getUpdatedAt())
              .as("수정 시간이 갱신되어야 함")
              .isGreaterThan(originalUpdatedAt)
      );
    }

    @Test
    @DisplayName("중복된 참여자 추가 시 예외가 발생해야 한다")
    void shouldThrowExceptionForDuplicateParticipant() {
      // given
      Channel channel = ChannelFixture.createDefaultChannel();
      User existingParticipant = channel.getCreator();
      int originalSize = channel.getParticipants().size();
      long originalUpdatedAt = channel.getUpdatedAt();

      // when & then
      assertAll(
          "중복 참여자 추가 시도 검증",
          () -> assertThatThrownBy(() -> channel.addParticipant(existingParticipant))
              .as("중복 참여자 추가 시 예외가 발생해야 함")
              .isInstanceOf(ChannelException.class),
          () -> assertThat(channel.getParticipants())
              .as("참여자 수가 변경되지 않아야 함")
              .hasSize(originalSize),
          () -> assertThat(channel.getUpdatedAt())
              .as("수정 시간이 변경되지 않아야 함")
              .isEqualTo(originalUpdatedAt)
      );
    }

    @Test
    @DisplayName("참여자 제거 시 목록에서 삭제되어야 한다")
    void shouldRemoveParticipant() throws InterruptedException {
      // given
      Channel channel = ChannelFixture.createDefaultChannel();
      User participant = UserFixture.createDefaultUser();
      channel.addParticipant(participant);
      long originalUpdatedAt = channel.getUpdatedAt();

      Thread.sleep(1); // 1밀리초 대기

      // when
      channel.removeParticipant(participant.getId());

      // then
      assertAll(
          "참여자 제거 검증",
          () -> assertThat(channel.getParticipants())
              .as("참여자 목록에서 제거되어야 함")
              .doesNotContain(participant),
          () -> assertThat(channel.getParticipants())
              .as("참여자 수가 감소해야 함")
              .hasSize(1),
          () -> assertThat(channel.getUpdatedAt())
              .as("수정 시간이 갱신되어야 함")
              .isGreaterThan(originalUpdatedAt)
      );
    }
  }
}