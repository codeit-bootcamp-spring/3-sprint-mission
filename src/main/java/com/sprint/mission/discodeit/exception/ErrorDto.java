package com.sprint.mission.discodeit.exception;

import com.sprint.mission.discodeit.exception.errorcode.ErrorCode;
import lombok.Builder;
import lombok.Data;
import org.springframework.http.ResponseEntity;

@Builder
@Data
public class ErrorDto {

    private int status;
    private String code;
    private String message;

    public static ResponseEntity<ErrorDto> toResponseEntity(ErrorCode e) {
        return ResponseEntity.status(e.getStatus().value())
            .body( /* ErrorDto 객체 생성 */
                ErrorDto.builder()
                    .status(e.getStatus().value())
                    .code(e.getCode())
                    .message(e.getMsg())
                    .build()
            );
    }
}
