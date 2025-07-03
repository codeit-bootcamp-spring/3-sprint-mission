package com.sprint.mission.discodeit.exception.binarycontent;

import com.sprint.mission.discodeit.exception.ErrorCode;

import java.util.Map;
import java.util.UUID;

/**
 * 파일 삭제 실패 시 발생하는 예외
 */
public class FileDeleteFailedException extends BinaryContentException {

  public FileDeleteFailedException() {
    super(ErrorCode.FILE_DELETE_FAILED);
  }

  public FileDeleteFailedException(Map<String, Object> details) {
    super(ErrorCode.FILE_DELETE_FAILED, details);
  }

  public FileDeleteFailedException(Throwable cause) {
    super(ErrorCode.FILE_DELETE_FAILED, cause);
  }

  public FileDeleteFailedException(Map<String, Object> details, Throwable cause) {
    super(ErrorCode.FILE_DELETE_FAILED, details, cause);
  }

  // === 편의 정적 팩토리 메서드들 ===

  /**
   * 바이너리 콘텐츠 ID로 예외를 생성하는 정적 팩토리 메서드
   */
  public static FileDeleteFailedException withBinaryContentId(UUID binaryContentId) {
    return new FileDeleteFailedException(Map.of("binaryContentId", binaryContentId));
  }

  /**
   * 파일명으로 예외를 생성하는 정적 팩토리 메서드
   */
  public static FileDeleteFailedException withFilename(String filename) {
    return new FileDeleteFailedException(Map.of("filename", filename));
  }

  /**
   * 바이너리 콘텐츠 ID와 원인으로 예외를 생성하는 정적 팩토리 메서드
   */
  public static FileDeleteFailedException withIdAndCause(UUID binaryContentId, Throwable cause) {
    return new FileDeleteFailedException(Map.of("binaryContentId", binaryContentId), cause);
  }

  /**
   * 원인과 함께 생성하는 정적 팩토리 메서드
   */
  public static FileDeleteFailedException withCause(Throwable cause) {
    return new FileDeleteFailedException(cause);
  }

  /**
   * 일반적인 생성 메서드
   */
  public static FileDeleteFailedException of() {
    return new FileDeleteFailedException();
  }

  /**
   * 상세 정보와 함께 생성하는 메서드
   */
  public static FileDeleteFailedException of(Map<String, Object> details) {
    return new FileDeleteFailedException(details);
  }
}