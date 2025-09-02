package com.sprint.mission.discodeit.entity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.sprint.mission.discodeit.fixture.UserFixture;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class NotificationTest {

  @Nested
  class Create {

    @Test
    void 알림_생성_시_필드가_정상적으로_설정되어야_한다() {
      User receiver = UserFixture.createValidUser();
      String title = "테스트 제목";
      String content = "테스트 내용";
      Notification notification = Notification.create(receiver, title, content);

      assertAll(
          () -> assertThat(notification.getReceiver()).isEqualTo(receiver),
          () -> assertThat(notification.getTitle()).isEqualTo(title),
          () -> assertThat(notification.getContent()).isEqualTo(content)
      );
    }
  }
}
