package com.sprint.mission.discodeit.exception.binaryContent;

import com.sprint.mission.discodeit.dto.response.ErrorCode;
import java.time.Instant;
import java.util.Map;

public class BinaryContentCreationException extends BinaryContentException {

  public BinaryContentCreationException(String fileName, String contentType) {
    super(
        Instant.now(),
        ErrorCode.BINARY_CONTENT_CREATION_FAILED,
        Map.of("fileName", fileName, "contentType", contentType)
    );
  }
}
