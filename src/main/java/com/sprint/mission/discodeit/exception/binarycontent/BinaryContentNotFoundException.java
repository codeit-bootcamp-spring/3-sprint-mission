package com.sprint.mission.discodeit.exception.binarycontent;

import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.Map;

/**
 * 바이너리컨텐츠가 존재하지 않을 때 발생하는 예외입니다.
 */
public class BinaryContentNotFoundException extends BinaryContentException {
    public BinaryContentNotFoundException(String message) {
        super(message, ErrorCode.INVALID_ARGUMENT);
    }
    public BinaryContentNotFoundException(String message, Map<String, Object> details) {
        super(message, ErrorCode.INVALID_ARGUMENT, details);
    }
} 