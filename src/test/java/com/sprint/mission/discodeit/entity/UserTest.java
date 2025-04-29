package com.sprint.mission.discodeit.entity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
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
      // given
      String email = "test@example.com";
      String name = "테스트유저";
      String password = "password123";

      // when
      User user = User.create(email, name, password);

      // then
      assertAll(
          "사용자 기본 정보 검증",
          () -> assertThat(user.getId()).as("ID는 UUID여야 함").isNotNull(),
          () -> assertThat(user.getEmail()).as("이메일이 일치해야 함").isEqualTo(email),
          () -> assertThat(user.getName()).as("이름이 일치해야 함").isEqualTo(name),
          () -> assertThat(user.getPassword()).as("비밀번호가 일치해야 함").isEqualTo(password),
          () -> assertThat(user.getCreatedAt()).as("생성 시간이 설정되어야 함").isNotNull(),
          () -> assertThat(user.getUpdatedAt()).as("수정 시간이 설정되어야 함").isNotNull(),
          () -> assertThat(user.getUpdatedAt()).as("생성시 수정시간은 생성시간과 동일해야 함")
              .isEqualTo(user.getCreatedAt()),
          () -> assertThat(user.getChannels()).as("채널 목록은 비어있어야 함").isEmpty(),
          () -> assertThat(user.getProfileImageId()).as("프로필 이미지 ID는 NULL이어야 함").isNull()
      );
    }

    @Test
    @DisplayName("프로필 이미지를 포함하여 사용자를 생성할 수 있다")
    void createWithProfileImage() {
      // given
      String email = "test@example.com";
      String name = "테스트유저";
      String password = "password123";
      User user = UserFixture.createCustomUser(new UserCreateRequest(email, name, password, null));
      BinaryContent profileImage = BinaryContentFixture.createValidProfileImage(user.getId());
      UUID profileImageId = profileImage.getId();

      // when
      user.updateProfileImageId(profileImageId);

      // then
      assertAll(
          "프로필 이미지 ID를 포함한 사용자 생성 검증",
          () -> assertThat(user.getEmail()).as("이메일이 일치해야 함").isEqualTo(email),
          () -> assertThat(user.getName()).as("이름이 일치해야 함").isEqualTo(name),
          () -> assertThat(user.getPassword()).as("비밀번호가 일치해야 함").isEqualTo(password),
          () -> assertThat(user.getProfileImageId()).as("프로필 이미지 ID가 일치해야 함")
              .isEqualTo(profileImageId)
      );
    }
  }

  @Nested
  @DisplayName("사용자 정보 수정")
  class Update {

    @Test
    @DisplayName("이름 수정시 이름과 수정시간이 업데이트 되어야 한다")
    void updateName() {
      // given
      User user = User.create("test@example.com", "원래이름", "password123");
      Instant originalUpdatedAt = user.getUpdatedAt();
      String newName = "새이름";

      // when
      user.updateName(newName);

      // then
      assertAll(
          "이름 수정 검증",
          () -> assertThat(user.getName()).as("이름이 수정되어야 함").isEqualTo(newName),
          () -> assertThat(user.getUpdatedAt()).as("수정시간이 갱신되어야 함")
              .isAfterOrEqualTo(originalUpdatedAt)
      );
    }

    @Test
    @DisplayName("비밀번호 수정시 비밀번호와 수정시간이 업데이트 되어야 한다")
    void updatePassword() {
      // given
      User user = User.create("test@example.com", "테스트유저", "oldpass");
      Instant originalUpdatedAt = user.getUpdatedAt();
      String newPassword = "newpass123";

      // when
      user.updatePassword(newPassword);

      // then
      assertAll(
          "비밀번호 수정 검증",
          () -> assertThat(user.getPassword()).as("비밀번호가 수정되어야 함").isEqualTo(newPassword),
          () -> assertThat(user.getUpdatedAt()).as("수정시간이 갱신되어야 함")
              .isAfterOrEqualTo(originalUpdatedAt)
      );
    }

    @Test
    @DisplayName("프로필 이미지 ID 수정시 ID와 수정시간이 업데이트 되어야 한다")
    void updateProfileImageId() {
      // given
      User user = User.create("test@example.com", "테스트유저", "password123");
      Instant originalUpdatedAt = user.getUpdatedAt();
      UUID newProfileImageId = UUID.randomUUID();

      // when
      user.updateProfileImageId(newProfileImageId);

      // then
      assertAll(
          "프로필 이미지 ID 수정 검증",
          () -> assertThat(user.getProfileImageId()).as("프로필 이미지 ID가 수정되어야 함")
              .isEqualTo(newProfileImageId),
          () -> assertThat(user.getUpdatedAt()).as("수정시간이 갱신되어야 함")
              .isAfterOrEqualTo(originalUpdatedAt)
      );
    }
  }

  @Nested
  @DisplayName("채널 관리")
  class ChannelManagement {

    @Test
    @DisplayName("채널 추가시 채널 목록과 수정시간이 업데이트 되어야 한다")
    void addChannel() {
      // given
      User user = User.create("test@example.com", "테스트유저", "password123");
      Instant originalUpdatedAt = user.getUpdatedAt();
      Channel channel = ChannelFixture.createValidChannel(); // 테스트용 채널 객체

      // when
      user.addChannel(channel);

      // then
      assertAll(
          "채널 추가 검증",
          () -> assertThat(user.getChannels()).as("채널이 추가되어야 함").contains(channel),
          () -> assertThat(user.getChannels()).as("채널 목록 크기는 1이어야 함").hasSize(1),
          () -> assertThat(user.getUpdatedAt()).as("수정시간이 갱신되어야 함")
              .isAfterOrEqualTo(originalUpdatedAt)
      );
    }

    @Test
    @DisplayName("이미 존재하는 채널 추가시 중복 추가되지 않아야 한다")
    void addDuplicateChannel() {
      // given
      User user = User.create("test@example.com", "테스트유저", "password123");
      Channel channel = ChannelFixture.createValidChannel(); // 테스트용 채널 객체
      user.addChannel(channel);
      int originalSize = user.getChannels().size();
      Instant updatedAtAfterFirstAdd = user.getUpdatedAt();

      // when
      user.addChannel(channel);

      // then
      assertAll(
          "중복 채널 추가 검증",
          () -> assertThat(user.getChannels().size()).as("채널 목록 크기는 변경되지 않아야 함")
              .isEqualTo(originalSize),
          () -> assertThat(user.getUpdatedAt()).as("중복 추가 시 수정시간이 갱신되지 않아야 함")
              .isEqualTo(updatedAtAfterFirstAdd)
      );
    }

    @Test
    @DisplayName("채널 목록 조회시 원본 목록이 아닌 복사본을 반환해야 한다")
    void getChannelsReturnsCopy() {
      // given
      User user = User.create("test@example.com", "테스트유저", "password123");
      Channel channel1 = ChannelFixture.createValidChannel(); // 테스트용 채널 객체
      user.addChannel(channel1);

      // when
      List<Channel> returnedChannels = user.getChannels();
      Channel channel2 = ChannelFixture.createValidChannel(); // 테스트용 채널 객체
      returnedChannels.add(channel2);

      // then
      assertThat(user.getChannels()).as("반환된 목록 수정이 원본에 영향을 주지 않아야 함")
          .doesNotContain(channel2)
          .hasSize(1);
    }
  }
}