package com.sprint.mission.discodeit.exception.binarycontent;

import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.Map;

public class ResourceLoadFailedException extends BinaryContentException {

    public ResourceLoadFailedException(String resourcePath) {
        super(ErrorCode.RESOURCE_LOAD_FAILED, Map.of("로드에 실패한 경로: ", resourcePath));
    }

  public ResourceLoadFailedException(String resourcePath, Throwable cause) {
    super(ErrorCode.RESOURCE_LOAD_FAILED, Map.of("로드에 실패한 경로: ", resourcePath), cause);
  }
}
