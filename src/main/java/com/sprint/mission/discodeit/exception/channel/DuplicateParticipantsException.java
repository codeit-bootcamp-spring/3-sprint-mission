package com.sprint.mission.discodeit.exception.channel;

import com.sprint.mission.discodeit.exception.ErrorCode;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 채널 생성 시 중복된 참가자가 있을 때 발생하는 예외
 */
public class DuplicateParticipantsException extends ChannelException {

  public DuplicateParticipantsException() {
    super(ErrorCode.DUPLICATE_PARTICIPANTS);
  }

  public DuplicateParticipantsException(Map<String, Object> details) {
    super(ErrorCode.DUPLICATE_PARTICIPANTS, details);
  }

  public DuplicateParticipantsException(Throwable cause) {
    super(ErrorCode.DUPLICATE_PARTICIPANTS, cause);
  }

  public DuplicateParticipantsException(Map<String, Object> details, Throwable cause) {
    super(ErrorCode.DUPLICATE_PARTICIPANTS, details, cause);
  }

  // === 편의 정적 팩토리 메서드들 ===

  /**
   * 중복된 참가자 ID 목록과 함께 예외를 생성하는 정적 팩토리 메서드
   */
  public static DuplicateParticipantsException withParticipantIds(List<UUID> participantIds) {
    return new DuplicateParticipantsException(Map.of("participantIds", participantIds));
  }

  /**
   * 일반적인 생성 메서드
   */
  public static DuplicateParticipantsException of() {
    return new DuplicateParticipantsException();
  }

  /**
   * 상세 정보와 함께 생성하는 메서드
   */
  public static DuplicateParticipantsException of(Map<String, Object> details) {
    return new DuplicateParticipantsException(details);
  }
}