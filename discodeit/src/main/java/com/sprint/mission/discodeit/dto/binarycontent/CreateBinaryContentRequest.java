package com.sprint.mission.discodeit.dto.binarycontent;

import com.sprint.mission.discodeit.entity.BinaryContentType;

public record CreateBinaryContentRequest(String fileName, BinaryContentType type, byte[] bytes) {
}
