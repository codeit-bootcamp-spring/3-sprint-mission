package com.sprint.mission.discodeit.testutil;

import java.util.UUID;

/**
 * 테스트용 상수 정의
 */
public final class TestConstants {

  private TestConstants() {
    // 유틸리티 클래스
  }

  // === 테스트 데이터 상수 ===
  public static final String TEST_USERNAME = "testuser";
  public static final String TEST_EMAIL = "test@example.com";
  public static final String TEST_PASSWORD = "password123";

  public static final String DUPLICATE_USERNAME = "duplicateuser";
  public static final String DUPLICATE_EMAIL = "duplicate@example.com";

  public static final String NEW_USERNAME = "newuser";
  public static final String NEW_EMAIL = "new@example.com";
  public static final String NEW_PASSWORD = "newpassword123";

  // === 채널 관련 상수 ===
  public static final String TEST_CHANNEL_NAME = "Test Channel";
  public static final String TEST_CHANNEL_DESCRIPTION = "Test Description";

  public static final String UPDATED_CHANNEL_NAME = "Updated Channel";
  public static final String UPDATED_CHANNEL_DESCRIPTION = "Updated Description";

  // === 메시지 관련 상수 ===
  public static final String TEST_MESSAGE_CONTENT = "Test message content";
  public static final String UPDATED_MESSAGE_CONTENT = "Updated message content";

  // === 파일 관련 상수 ===
  public static final String TEST_FILE_NAME = "test-file.jpg";
  public static final String TEST_CONTENT_TYPE = "image/jpeg";
  public static final long TEST_FILE_SIZE = 1024L;
  public static final String TEST_FILE_PATH = "/path/to/file";

  // === 존재하지 않는 ID(실패 케이스용) ===
  public static final UUID NON_EXISTENT_USER_ID = UUID.fromString("999e4567-e89b-12d3-a456-426614174999");
  public static final UUID NON_EXISTENT_CHANNEL_ID = UUID.fromString("999e4567-e89b-12d3-a456-426614174998");
  public static final UUID NON_EXISTENT_MESSAGE_ID = UUID.fromString("999e4567-e89b-12d3-a456-426614174997");
}