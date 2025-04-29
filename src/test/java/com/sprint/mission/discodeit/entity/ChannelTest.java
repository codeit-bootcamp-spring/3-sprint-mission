package com.sprint.mission.discodeit.entity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.sprint.mission.discodeit.common.exception.ChannelException;
import com.sprint.mission.discodeit.fixture.ChannelFixture;
import com.sprint.mission.discodeit.fixture.UserFixture;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class ChannelTest {

  // 테스트에서 사용할 고정된 시간
  private static final Instant FIXED_TIME = Instant.parse("2023-01-01T10:00:00Z");
  private static final Clock FIXED_CLOCK = Clock.fixed(FIXED_TIME, ZoneId.systemDefault());

  @Nested
  @DisplayName("채널 생성")
  class Create {

    @Test
    @DisplayName("채널이 생성되면 기본 정보가 올바르게 설정되어야 한다")
    void shouldCreateChannelWithDefaultInfo() {
      // when
      Channel channel = ChannelFixture.createValidChannel();

      // then
      assertAll(
          "채널 기본 정보 검증",
          () -> assertThat(channel.getId()).as("채널 ID는 null이 아니어야 함").isNotNull(),
          () -> assertThat(channel.getName())
              .as("채널 이름이 올바르게 설정되어야 함")
              .isEqualTo(ChannelFixture.DEFAULT_CHANNEL_NAME),
          () -> assertThat(channel.getCreatedAt())
              .as("생성 시간이 올바르게 설정되어야 함")
              .isNotNull(),
          () -> assertThat(channel.getUpdatedAt())
              .as("최초 수정 시간은 생성 시간과 동일해야 함")
              .isEqualTo(channel.getCreatedAt())
      );
    }

    @Test
    @DisplayName("채널 생성 시 생성자는 자동으로 참여자로 등록되어야 한다")
    void shouldAddCreatorAsParticipant() {
      // given
      User creator = UserFixture.createValidUser();

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

    private Channel channel;
    private User participant;

    @BeforeEach
    void setUp() {
      // 각 테스트마다 새로운 인스턴스 생성하여 독립성 보장
      channel = ChannelFixture.createValidChannel();
      participant = UserFixture.createValidUser();
      channel.addParticipant(participant);
    }

    @Test
    @DisplayName("참여자 목록 조회 시 불변성이 보장되어야 한다")
    void shouldReturnImmutableParticipantsList() {
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

    private Channel channel;
    private Instant originalUpdatedAt;

    @BeforeEach
    void setUp() {
      // 각 테스트마다 새로운 인스턴스 생성하여 독립성 보장
      channel = ChannelFixture.createValidChannel();
      originalUpdatedAt = channel.getUpdatedAt();

      // 시간 차이를 보장하기 위한 대기
      try {
        Thread.sleep(5);
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      }
    }

    @Test
    @DisplayName("채널 이름 수정 시 이름과 수정 시간이 업데이트되어야 한다")
    void shouldUpdateNameAndTimestamp() {
      // given
      String newName = "변경된 채널명";

      // when
      channel.updateName(newName);

      // then
      assertAll(
          "이름 변경과 수정 시간 업데이트 검증",
          () -> assertThat(channel.getName())
              .as("새로운 이름으로 변경되어야 함")
              .isEqualTo(newName),
          () -> assertThat(channel.getUpdatedAt())
              .as("수정 시간이 갱신되어야 함")
              .isAfter(originalUpdatedAt)
      );
    }

    @ParameterizedTest
    @ValueSource(strings = {"감자", "왕감자", "고구마"})
    @DisplayName("채널 이름 수정 테스트 여러 데이터")
    void shouldUpdateNameAndTimestampParameterized(String newName) {
      // when
      channel.updateName(newName);

      // then
      assertAll(
          () -> assertThat(channel.getName()).isEqualTo(newName),
          () -> assertThat(channel.getUpdatedAt()).isAfter(originalUpdatedAt)
      );
    }

    @Test
    @DisplayName("새로운 참여자 추가 시 참여자 목록이 올바르게 갱신되어야 한다")
    void shouldAddNewParticipant() {
      // given
      User newParticipant = UserFixture.createValidUser();

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
              .isAfter(originalUpdatedAt)
      );
    }

    @Test
    @DisplayName("중복된 참여자 추가 시 예외가 발생해야 한다")
    void shouldThrowExceptionForDuplicateParticipant() {
      // given
      User existingParticipant = channel.getCreator();
      int originalSize = channel.getParticipants().size();

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
    void shouldRemoveParticipant() {
      // given
      User participant = UserFixture.createValidUser();
      channel.addParticipant(participant);

      // 수정 시간 업데이트 이후 시간 차이를 보장하기 위한 대기
      try {
        Thread.sleep(5);
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      }

      Instant beforeRemovalUpdatedAt = channel.getUpdatedAt();

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
              .isAfter(beforeRemovalUpdatedAt)
      );
    }
  }
}