package com.sprint.mission.discodeit.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
@AllArgsConstructor
public class CodeMessageResponseDto<T> {
    private String code;
    private String message;
    private T data;

    public static <T> CodeMessageResponseDto<T> of(String code, String message, T data) {
        return new CodeMessageResponseDto<>(code, message, data);
    }

    public static <T> CodeMessageResponseDto<T> success(T data) {
        return new CodeMessageResponseDto<>(ResponseCode.SUCCESS, ResponseMessage.SUCCESS, data);
    }

    public static CodeMessageResponseDto<?> error(String code, String message) {
        return new CodeMessageResponseDto<>(code, message, null);
    }

}
