package com.sprint.mission.discodeit.exception;

import java.io.File;
import java.text.MessageFormat;

public class FileException extends BusinessException {

  public FileException(ErrorCode errorCode, String message) {
    super(errorCode, message);
  }

  public static FileException readError(File file, Throwable cause) {
    return new FileException(
        ErrorCode.FILE_READ_ERROR,
        MessageFormat.format("파일을 읽는 중 오류가 발생했습니다. [파일: {0}] - {1}", file.getPath(),
            cause.getMessage())
    );
  }

  public static FileException writeError(File file, Throwable cause) {
    return new FileException(
        ErrorCode.FILE_WRITE_ERROR,
        MessageFormat.format("파일을 쓰는 중 오류가 발생했습니다. [파일: {0}] - {1}", file.getPath(),
            cause.getMessage())
    );
  }

  public static FileException notFound(File file) {
    return new FileException(
        ErrorCode.FILE_NOT_FOUND,
        MessageFormat.format("파일을 찾을 수 없습니다. [파일: {0}]", file.getPath())
    );
  }

  public static FileException invalidPosition(File file, Long position) {
    return new FileException(
        ErrorCode.INVALID_POSITION,
        MessageFormat.format("잘못된 파일 위치입니다. [파일: {0}, 위치: {1}]", file.getPath(),
            position)
    );
  }

  public static FileException serializationError(File file, Throwable cause) {
    return new FileException(
        ErrorCode.SERIALIZATION_ERROR,
        MessageFormat.format("직렬화 오류가 발생했습니다. [파일: {0}] - {1}", file.getPath(),
            cause.getMessage())
    );
  }

  public static FileException deleteError(File file, Long position, Throwable cause) {
    return new FileException(
        ErrorCode.DELETE_ERROR,
        MessageFormat.format("오류로 인해 객체를 삭제할 수 없습니다. [파일: {0}, 위치: {1}] - {2}", file.getPath(),
            position, cause.getMessage())
    );
  }

  public static FileException optimizationError(File file, Throwable cause) {
    return new FileException(
        ErrorCode.OPTIMIZATION_ERROR,
        MessageFormat.format("파일을 최적화하는 중 오류가 발생했습니다. [파일: {0}] - {1}", file.getPath(),
            cause.getMessage())
    );
  }
}