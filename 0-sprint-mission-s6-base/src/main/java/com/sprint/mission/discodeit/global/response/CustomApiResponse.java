package com.sprint.mission.discodeit.global.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;


/* @Builder로 static 팩토리 메서드로만 생성 유도
   @JsonInclude를 NON_NULL로 두어 null인 필드를 JSON에서 제거하여 응답을 더 간결하게* */
@Getter
@Builder(access = AccessLevel.PRIVATE)
@JsonInclude(value = Include.NON_NULL)
public class CustomApiResponse<T> {

  @JsonIgnore
  private HttpStatus httpStatus;
  private boolean success;
  private String message;

  @Nullable
  private T data;


  public static <T> CustomApiResponse<T> success(T data) {
    return CustomApiResponse.<T>builder()
        .httpStatus(HttpStatus.OK)
        .success(true)
        .data(data)
        .build();
  }

  public static CustomApiResponse<Void> success(String message) {
    return CustomApiResponse.<Void>builder()
        .httpStatus(HttpStatus.CREATED)
        .success(true)
        .message(message)
        .build();
  }


  public static <T> CustomApiResponse<T> created(T data) {
    return CustomApiResponse.<T>builder()
        .httpStatus(HttpStatus.CREATED)
        .success(true)
        .data(data)
        .build();
  }

  public static <T> CustomApiResponse<T> failure(Exception e) {
    return CustomApiResponse.<T>builder()
        .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR)
        .success(false)
        .message(e.getMessage())
        .build();
  }
}
