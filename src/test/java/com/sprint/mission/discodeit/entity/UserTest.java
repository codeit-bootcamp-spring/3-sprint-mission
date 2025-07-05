package com.sprint.mission.discodeit.entity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.sprint.mission.discodeit.fixture.BinaryContentFixture;
import com.sprint.mission.discodeit.fixture.ChannelFixture;
import com.sprint.mission.discodeit.fixture.UserFixture;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("User 엔티티 테스트")
class UserTest {

  @Nested
  @DisplayName("사용자 생성")
  class Create {

    @Test
    @DisplayName("기본 정보로 사용자 생성시 필수 필드가 올바르게 설정되어야 한다")
    void createWithBasicInfo() {
      User user = UserFixture.createValidUser();

      assertAll(
          () -> assertThat(user.getId()).isNotNull(),
          () -> assertThat(user.getEmail()).isNotNull(),
          () -> assertThat(user.getName()).isNotNull(),
          () -> assertThat(user.getPassword()).isNotNull(),
          () -> assertThat(user.getCreatedAt()).isNotNull(),
          () -> assertThat(user.getUpdatedAt()).isNotNull(),
          () -> assertThat(user.getUpdatedAt()).isEqualTo(user.getCreatedAt()),
          () -> assertThat(user.getChannels()).isEmpty(),
          () -> assertThat(user.getProfileImageId()).isNull()
      );
    }

    @Test
    @DisplayName("프로필 이미지를 포함하여 사용자를 생성할 수 있다")
    void createWithProfileImage() {
      User user = UserFixture.createValidUser();
      var profileImage = BinaryContentFixture.createValidProfileImage(user.getId());

      user.updateProfileImageId(profileImage.getId());

      assertAll(
          () -> assertThat(user.getProfileImageId()).isEqualTo(profileImage.getId())
      );
    }
  }

  @Nested
  @DisplayName("사용자 정보 수정")
  class Update {

    @Test
    @DisplayName("이름 수정시 이름과 수정시간이 업데이트 되어야 한다")
    void updateName() {
      User user = UserFixture.createValidUser();
      Instant originalUpdatedAt = user.getUpdatedAt();
      String newName = "새이름";

      user.updateName(newName);

      assertAll(
          () -> assertThat(user.getName()).isEqualTo(newName),
          () -> assertThat(user.getUpdatedAt()).isAfterOrEqualTo(originalUpdatedAt)
      );
    }

    @Test
    @DisplayName("비밀번호 수정시 비밀번호와 수정시간이 업데이트 되어야 한다")
    void updatePassword() {
      User user = UserFixture.createValidUser();
      Instant originalUpdatedAt = user.getUpdatedAt();
      String newPassword = "newpass123";

      user.updatePassword(newPassword);

      assertAll(
          () -> assertThat(user.getPassword()).isEqualTo(newPassword),
          () -> assertThat(user.getUpdatedAt()).isAfterOrEqualTo(originalUpdatedAt)
      );
    }

    @Test
    @DisplayName("프로필 이미지 ID 수정시 ID와 수정시간이 업데이트 되어야 한다")
    void updateProfileImageId() {
      User user = UserFixture.createValidUser();
      Instant originalUpdatedAt = user.getUpdatedAt();
      UUID newProfileImageId = UUID.randomUUID();

      user.updateProfileImageId(newProfileImageId);

      assertAll(
          () -> assertThat(user.getProfileImageId()).isEqualTo(newProfileImageId),
          () -> assertThat(user.getUpdatedAt()).isAfterOrEqualTo(originalUpdatedAt)
      );
    }
  }

  @Nested
  @DisplayName("채널 관리")
  class ChannelManagement {

    @Test
    @DisplayName("채널 추가시 채널 목록과 수정시간이 업데이트 되어야 한다")
    void addChannel() {
      User user = UserFixture.createValidUser();
      var channel = ChannelFixture.createValidChannelWithParticipant();
      Instant originalUpdatedAt = user.getUpdatedAt();

      user.addChannel(channel);

      assertAll(
          () -> assertThat(user.getChannels()).contains(channel),
          () -> assertThat(user.getChannels()).hasSize(1),
          () -> assertThat(user.getUpdatedAt()).isAfterOrEqualTo(originalUpdatedAt)
      );
    }

    @Test
    @DisplayName("이미 존재하는 채널 추가시 중복 추가되지 않아야 한다")
    void addDuplicateChannel() {
      User user = UserFixture.createValidUser();
      var channel = ChannelFixture.createValidChannelWithParticipant();
      user.addChannel(channel);
      int originalSize = user.getChannels().size();
      Instant updatedAtAfterFirstAdd = user.getUpdatedAt();

      user.addChannel(channel);

      assertAll(
          () -> assertThat(user.getChannels().size()).isEqualTo(originalSize),
          () -> assertThat(user.getUpdatedAt()).isEqualTo(updatedAtAfterFirstAdd)
      );
    }

    @Test
    @DisplayName("채널 목록 조회시 복사본을 반환해야 한다")
    void getChannelsReturnsCopy() {
      User user = UserFixture.createValidUser();
      var channel = ChannelFixture.createValidChannelWithParticipant();
      user.addChannel(channel);

      List<Channel> returnedChannels = user.getChannels();
      var newChannel = ChannelFixture.createValidChannelWithParticipant();
      returnedChannels.add(newChannel);

      assertThat(user.getChannels()).doesNotContain(newChannel).hasSize(1);
    }
  }
}
