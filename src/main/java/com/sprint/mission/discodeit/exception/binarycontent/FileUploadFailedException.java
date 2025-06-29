package com.sprint.mission.discodeit.exception.binarycontent;

import com.sprint.mission.discodeit.exception.ErrorCode;

import java.util.Map;

/**
 * 파일 업로드 실패 시 발생하는 예외
 */
public class FileUploadFailedException extends BinaryContentException {

  public FileUploadFailedException() {
    super(ErrorCode.FILE_UPLOAD_FAILED);
  }

  public FileUploadFailedException(Map<String, Object> details) {
    super(ErrorCode.FILE_UPLOAD_FAILED, details);
  }

  public FileUploadFailedException(Throwable cause) {
    super(ErrorCode.FILE_UPLOAD_FAILED, cause);
  }

  public FileUploadFailedException(Map<String, Object> details, Throwable cause) {
    super(ErrorCode.FILE_UPLOAD_FAILED, details, cause);
  }

  // === 편의 정적 팩토리 메서드들 ===

  /**
   * 파일명으로 예외를 생성하는 정적 팩토리 메서드
   */
  public static FileUploadFailedException withFilename(String filename) {
    return new FileUploadFailedException(Map.of("filename", filename));
  }

  /**
   * 파일명과 원인으로 예외를 생성하는 정적 팩토리 메서드
   */
  public static FileUploadFailedException withFilenameAndCause(String filename, Throwable cause) {
    return new FileUploadFailedException(Map.of("filename", filename), cause);
  }

  /**
   * 원인과 함께 생성하는 정적 팩토리 메서드
   */
  public static FileUploadFailedException withCause(Throwable cause) {
    return new FileUploadFailedException(cause);
  }

  /**
   * 일반적인 생성 메서드
   */
  public static FileUploadFailedException of() {
    return new FileUploadFailedException();
  }

  /**
   * 상세 정보와 함께 생성하는 메서드
   */
  public static FileUploadFailedException of(Map<String, Object> details) {
    return new FileUploadFailedException(details);
  }
}