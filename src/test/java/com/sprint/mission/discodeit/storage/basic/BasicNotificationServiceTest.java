package com.sprint.mission.discodeit.storage.basic;

import com.sprint.mission.discodeit.dto.request.NotificationDto;
import com.sprint.mission.discodeit.entity.Notification;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.mapper.NotificationMapper;
import com.sprint.mission.discodeit.repository.NotificationRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.basic.BasicNotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@org.mockito.junit.jupiter.MockitoSettings(strictness = org.mockito.quality.Strictness.LENIENT)
class BasicNotificationServiceTest {

  @Mock
  private UserRepository userRepository;

  @Mock
  private NotificationRepository notificationRepository;

  @Mock
  private NotificationMapper notificationMapper;
  
  @InjectMocks
  private BasicNotificationService notificationService;

  private UUID receiverId;
  private UUID notificationId;
  private Notification notification;
  private NotificationDto notificationDto;

  @BeforeEach
  void setUp() {
    receiverId = UUID.randomUUID();
    notificationId = UUID.randomUUID();
    User receiver = new User("receiver", "receiver@example.com", "pw", null);
    ReflectionTestUtils.setField(receiver, "id", receiverId);
    notification = new Notification(receiver, "Test Title", "Test Content");
    ReflectionTestUtils.setField(notification, "id", notificationId);
    
    notificationDto = new NotificationDto(
        notificationId,
        Instant.now(),
        receiverId,
        "Test Title",
        "Test Content"
    );
  }

  @Test
  @DisplayName("수신자별 알림 목록 조회 성공")
  void findAllByReceiverId_Success() {
    // given
    List<Notification> notifications = List.of(notification);
    given(notificationRepository.findByReceiverId(receiverId))
        .willReturn(notifications);
    given(notificationMapper.toDto(notification)).willReturn(notificationDto);

    // when
    List<NotificationDto> result = notificationService.findByUserId(receiverId);

    // then
    assertThat(result).hasSize(1);
    assertThat(result.get(0)).isEqualTo(notificationDto);
    verify(notificationRepository).findByReceiverId(receiverId);
    verify(notificationMapper).toDto(notification);
  }

  @Test
  @DisplayName("수신자별 알림 목록 조회 - 빈 목록")
  void findAllByReceiverId_EmptyList() {
    // given
    given(notificationRepository.findByReceiverId(receiverId))
        .willReturn(List.of());

    // when
    List<NotificationDto> result = notificationService.findByUserId(receiverId);

    // then
    assertThat(result).isEmpty();
    verify(notificationRepository).findByReceiverId(receiverId);
    verifyNoInteractions(notificationMapper);
  }

  @Test
  @DisplayName("알림 삭제 성공")
  void delete_Success() {
    // given
    given(notificationRepository.findById(notificationId))
        .willReturn(Optional.of(notification));

    // when
    notificationService.delete(notificationId, receiverId);

    // then
    verify(notificationRepository).findById(notificationId);
    verify(notificationRepository).delete(notification);
  }

  @Test
  @DisplayName("알림 삭제 실패 - 존재하지 않는 알림")
  void delete_Failure_NotificationNotFound() {
    // given
    given(notificationRepository.findById(notificationId))
        .willReturn(Optional.empty());

    // when & then
    assertThatThrownBy(() -> notificationService.delete(notificationId, receiverId))
        .isInstanceOf(com.sprint.mission.discodeit.exception.DiscodeitException.class);
    
    verify(notificationRepository).findById(notificationId);
    verify(notificationRepository, never()).delete(any());
  }

  
}