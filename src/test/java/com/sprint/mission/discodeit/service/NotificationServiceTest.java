package com.sprint.mission.discodeit.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.sprint.mission.discodeit.entity.Notification;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.fixture.UserFixture;
import com.sprint.mission.discodeit.repository.NotificationRepository;
import com.sprint.mission.discodeit.testannotation.RepositoryTest;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@RepositoryTest
@Import(NotificationService.class)
class NotificationServiceTest {

  @Autowired
  NotificationService notificationService;
  @Autowired
  NotificationRepository notificationRepository;
  @Autowired
  TestEntityManager em;

  private User receiver;

  @BeforeEach
  void setUp() {
    receiver = UserFixture.createValidUser();
    em.persist(receiver);
  }

  @Test
  void 알림_생성_조회_삭제_기능이_정상적으로_동작해야_한다() {
    Notification notification = notificationService.create(receiver, "테스트", "내용");
    em.flush();
    em.clear();

    List<Notification> found = notificationRepository.findAllByReceiverId(receiver.getId());
    assertThat(found).hasSize(1);
    assertThat(found.get(0).getTitle()).isEqualTo("테스트");

    notificationService.delete(notification.getId(), receiver.getId());
    em.flush();
    em.clear();
    List<Notification> afterDelete = notificationRepository.findAllByReceiverId(receiver.getId());
    assertThat(afterDelete).isEmpty();
  }
}
