package com.sprint.mission.discodeit.common;

/**
 * 애플리케이션 전역에서 사용되는 상수들을 정의하는 클래스
 */
public final class Constants {

  private Constants() {
    // 유틸리티 클래스는 인스턴스화 방지
  }

  /**
   * 페이지네이션 관련 상수
   */
  public static final class Pagination {
    public static final int DEFAULT_PAGE_SIZE = 50;
    public static final String DEFAULT_SORT_FIELD = "createdAt";
    public static final String DEFAULT_SORT_DIRECTION = "desc";

    private Pagination() {
    }
  }

  /**
   * 데이터베이스 제약 조건 관련 상수
   */
  public static final class Database {
    public static final int USERNAME_MAX_LENGTH = 50;
    public static final int EMAIL_MAX_LENGTH = 100;
    public static final int PASSWORD_MAX_LENGTH = 60;
    public static final int CHANNEL_NAME_MAX_LENGTH = 100;
    public static final int CHANNEL_DESCRIPTION_MAX_LENGTH = 500;
    public static final int FILENAME_MAX_LENGTH = 255;
    public static final int CONTENT_TYPE_MAX_LENGTH = 100;

    private Database() {
    }
  }

  /**
   * 사용자 상태 관련 상수
   */
  public static final class UserStatus {
    public static final int ONLINE_THRESHOLD_MINUTES = 5;

    private UserStatus() {
    }
  }

  /**
   * 비공개 채널 관련 상수
   */
  public static final class PrivateChannel {
    public static final int MIN_PARTICIPANTS = 2;

    private PrivateChannel() {
    }
  }

  /**
   * 에러 메시지 상수
   */
  public static final class ErrorMessages {
    public static final String CHANNEL_NAME_REQUIRED = "채널 이름은 필수입니다.";
    public static final String CHANNEL_NAME_TOO_LONG = "채널 이름은 100자를 초과할 수 없습니다.";
    public static final String CHANNEL_DESCRIPTION_TOO_LONG = "채널 설명은 500자를 초과할 수 없습니다.";
    public static final String MESSAGE_CONTENT_REQUIRED = "메시지 내용은 필수입니다.";
    public static final String CHANNEL_ID_REQUIRED = "채널 ID는 필수입니다.";
    public static final String AUTHOR_ID_REQUIRED = "작성자 ID는 필수입니다.";
    public static final String PARTICIPANTS_REQUIRED = "참가자 목록은 비어있을 수 없습니다.";
    public static final String MIN_PARTICIPANTS_REQUIRED = "비공개 채널은 최소 2명의 참가자가 필요합니다.";

    private ErrorMessages() {
    }
  }
}