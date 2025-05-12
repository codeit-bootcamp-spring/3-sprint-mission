package com.sprint.mission.discodeit.dto.binarycontent;

import com.sprint.mission.discodeit.entity.BinaryContentType;

public record AddBinaryContentRequest(String fileName,
                                      String type,
                                      byte[] bytes) {
}
