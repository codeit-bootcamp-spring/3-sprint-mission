package com.sprint.mission.discodeit.exception.binarycontent;

import com.sprint.mission.discodeit.exception.ErrorCode;

import java.util.Map;
import java.util.UUID;

/**
 * 바이너리 콘텐츠를 찾을 수 없을 때 발생하는 예외
 */
public class BinaryContentNotFoundException extends BinaryContentException {

  public BinaryContentNotFoundException() {
    super(ErrorCode.BINARY_CONTENT_NOT_FOUND);
  }

  public BinaryContentNotFoundException(Map<String, Object> details) {
    super(ErrorCode.BINARY_CONTENT_NOT_FOUND, details);
  }

  public BinaryContentNotFoundException(Throwable cause) {
    super(ErrorCode.BINARY_CONTENT_NOT_FOUND, cause);
  }

  public BinaryContentNotFoundException(Map<String, Object> details, Throwable cause) {
    super(ErrorCode.BINARY_CONTENT_NOT_FOUND, details, cause);
  }

  // === 편의 정적 팩토리 메서드들 ===

  /**
   * 바이너리 콘텐츠 ID로 예외를 생성하는 정적 팩토리 메서드
   */
  public static BinaryContentNotFoundException withBinaryContentId(UUID binaryContentId) {
    return new BinaryContentNotFoundException(Map.of("binaryContentId", binaryContentId));
  }

  /**
   * 파일명으로 예외를 생성하는 정적 팩토리 메서드
   */
  public static BinaryContentNotFoundException withFilename(String filename) {
    return new BinaryContentNotFoundException(Map.of("filename", filename));
  }

  /**
   * 파일 경로로 예외를 생성하는 정적 팩토리 메서드
   */
  public static BinaryContentNotFoundException withFilePath(String filePath) {
    return new BinaryContentNotFoundException(Map.of("filePath", filePath));
  }

  /**
   * 바이너리 콘텐츠 ID와 파일명으로 예외를 생성하는 정적 팩토리 메서드
   */
  public static BinaryContentNotFoundException withIdAndFilename(UUID binaryContentId, String filename) {
    return new BinaryContentNotFoundException(Map.of(
        "binaryContentId", binaryContentId,
        "filename", filename));
  }

  /**
   * 일반적인 생성 메서드
   */
  public static BinaryContentNotFoundException of() {
    return new BinaryContentNotFoundException();
  }

  /**
   * 상세 정보와 함께 생성하는 메서드
   */
  public static BinaryContentNotFoundException of(Map<String, Object> details) {
    return new BinaryContentNotFoundException(details);
  }
}