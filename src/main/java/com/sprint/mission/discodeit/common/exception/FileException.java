package com.sprint.mission.discodeit.common.exception;

import java.io.File;

public class FileException extends BusinessException {

  public static final String FILE_READ_ERROR = "F001";
  public static final String FILE_WRITE_ERROR = "F002";
  public static final String FILE_NOT_FOUND = "F003";
  public static final String INVALID_POSITION = "F004";
  public static final String SERIALIZATION_ERROR = "F005";
  public static final String DELETE_ERROR = "F006";
  public static final String OPTIMIZATION_ERROR = "F007";

  public FileException(String message, String errorCode) {
    super(message, errorCode);
  }

  public static FileException readError(File file, Throwable cause) {
    return new FileException(
        "파일을 읽는 중 오류가 발생했습니다. [파일: " + file.getPath() + "] - " + cause.getMessage(),
        FILE_READ_ERROR
    );
  }

  public static FileException writeError(File file, Throwable cause) {
    return new FileException(
        "파일을 쓰는 중 오류가 발생했습니다. [파일: " + file.getPath() + "] - " + cause.getMessage(),
        FILE_WRITE_ERROR
    );
  }

  public static FileException notFound(File file) {
    return new FileException(
        "파일을 찾을 수 없습니다. [파일: " + file.getPath() + "]",
        FILE_NOT_FOUND
    );
  }

  public static FileException invalidPosition(File file, Long position) {
    return new FileException(
        "잘못된 파일 위치입니다. [파일: " + file.getPath() + ", 위치: " + position + "]",
        INVALID_POSITION
    );
  }

  public static FileException serializationError(File file, Throwable cause) {
    return new FileException(
        "직렬화 오류가 발생했습니다. [파일: " + file.getPath() + "] - " + cause.getMessage(),
        SERIALIZATION_ERROR
    );
  }

  public static FileException deleteError(File file, Long position, Throwable cause) {
    return new FileException(
        "오류로 인해 객체를 삭제할 수 없습니다. [파일: " + file.getPath() + ", 위치: " + position + "] - "
            + cause.getMessage(),
        DELETE_ERROR
    );
  }

  public static FileException optimizationError(File file, Throwable cause) {
    return new FileException(
        "파일을 최적화하는 중 오류가 발생했습니다. [파일: " + file.getPath() + "] - " + cause.getMessage(),
        OPTIMIZATION_ERROR
    );
  }
}