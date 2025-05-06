package com.sprint.mission.discodeit.exception;

import java.io.File;

public class FileException extends BusinessException {

  public static final String FILE_READ_ERROR = "F001";
  public static final String FILE_WRITE_ERROR = "F002";
  public static final String FILE_NOT_FOUND = "F003";
  public static final String INVALID_POSITION = "F004";
  public static final String SERIALIZATION_ERROR = "F005";
  public static final String DELETE_ERROR = "F006";
  public static final String OPTIMIZATION_ERROR = "F007";

  public FileException(String errorCode, String message) {
    super(errorCode, message);
  }

  public static FileException readError(File file, Throwable cause) {
    return new FileException(
        FILE_READ_ERROR,
        "파일을 읽는 중 오류가 발생했습니다. [파일: " + file.getPath() + "] - " + cause.getMessage()
    );
  }

  public static FileException writeError(File file, Throwable cause) {
    return new FileException(
        FILE_WRITE_ERROR,
        "파일을 쓰는 중 오류가 발생했습니다. [파일: " + file.getPath() + "] - " + cause.getMessage()
    );
  }

  public static FileException notFound(File file) {
    return new FileException(
        FILE_NOT_FOUND,
        "파일을 찾을 수 없습니다. [파일: " + file.getPath() + "]"
    );
  }

  public static FileException invalidPosition(File file, Long position) {
    return new FileException(
        INVALID_POSITION,
        "잘못된 파일 위치입니다. [파일: " + file.getPath() + ", 위치: " + position + "]"
    );
  }

  public static FileException serializationError(File file, Throwable cause) {
    return new FileException(
        SERIALIZATION_ERROR,
        "직렬화 오류가 발생했습니다. [파일: " + file.getPath() + "] - " + cause.getMessage()
    );
  }

  public static FileException deleteError(File file, Long position, Throwable cause) {
    return new FileException(
        DELETE_ERROR,
        "오류로 인해 객체를 삭제할 수 없습니다. [파일: " + file.getPath() + ", 위치: " + position + "] - "
            + cause.getMessage()
    );
  }

  public static FileException optimizationError(File file, Throwable cause) {
    return new FileException(
        OPTIMIZATION_ERROR,
        "파일을 최적화하는 중 오류가 발생했습니다. [파일: " + file.getPath() + "] - " + cause.getMessage()
    );
  }
}