package com.sprint.mission.discodeit.entity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.sprint.mission.discodeit.fixture.BinaryContentFixture;
import com.sprint.mission.discodeit.fixture.ChannelFixture;
import com.sprint.mission.discodeit.fixture.UserFixture;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class UserTest {

  @Nested
  class Create {

    @Test
    void 기본_정보로_사용자_생성시_필수_필드가_올바르게_설정되어야_한다() {
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
    void 프로필_이미지를_포함하여_사용자를_생성할_수_있다() {
      User user = UserFixture.createValidUser();
      var profileImage = BinaryContentFixture.createValidProfileImage(user.getId());

      user.updateProfileImageId(profileImage.getId());

      assertAll(
          () -> assertThat(user.getProfileImageId()).isEqualTo(profileImage.getId())
      );
    }
  }

  @Nested
  class Update {

    @Test
    void 이름_수정시_이름과_수정시간이_업데이트_되어야_한다() {
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
    void 비밀번호_수정_시_비밀번호와_수정시간이_업데이트_되어야_한다() {
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
    void 프로필_이미지_ID_수정_시_ID와_수정시간이_업데이트_되어야_한다() {
      User user = UserFixture.createValidUser();
      Instant originalUpdatedAt = user.getUpdatedAt();
      UUID newProfileImageId = UUID.randomUUID();

      user.updateProfileImageId(newProfileImageId);

      assertAll(
          () -> assertThat(user.getProfileImageId()).isEqualTo(newProfileImageId),
          () -> assertThat(user.getUpdatedAt()).isAfterOrEqualTo(originalUpdatedAt)
      );
    }

    @Test
    void 채널_추가시_채널_목록과_수정시간이_업데이트_되어야_한다() {
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
    void 이미_존재하는_채널_추가시_중복_추가되지_않아야_한다() {
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
  }

  @Nested
  class Read {

    @Test
    void 채널_목록_조회시_복사본을_반환해야_한다() {
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
