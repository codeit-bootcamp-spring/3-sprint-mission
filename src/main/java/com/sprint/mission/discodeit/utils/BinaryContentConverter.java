package com.sprint.mission.discodeit.utils;

import com.sprint.mission.discodeit.dto.BinaryContentCreateRequest;
import java.io.IOException;
import java.util.Optional;
import org.springframework.web.multipart.MultipartFile;

public class BinaryContentConverter {

  /* MultipartFile 타입 -> BinaryContentCreateRequest 타입으로 변경 */
  public static Optional<BinaryContentCreateRequest> resolveProfileRequest(MultipartFile profile) {
    if (profile.isEmpty()) {
      return Optional.empty();
    } else {
      try {
        BinaryContentCreateRequest binaryContentCreateRequest = new BinaryContentCreateRequest(
            profile.getOriginalFilename(), profile.getContentType(), profile.getBytes());
        return Optional.of(binaryContentCreateRequest);
      } catch (IOException e) {
        throw new RuntimeException();
      }
    }
  }
}
