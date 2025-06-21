package com.sprint.mission.discodeit.utils;

import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
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

  public static byte[] toByteArray(InputStream inputStream) throws IOException {
    ByteArrayOutputStream buffer = new ByteArrayOutputStream();
    byte[] data = new byte[1024]; // 임시버퍼생성, 한번 읽을때 1024 바이트만큼 읽음
    int nRead;
    while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
      buffer.write(data, 0, nRead);
    }
    return buffer.toByteArray();
  }

  public static InputStream toInputStream(byte[] bytes) {
    return new ByteArrayInputStream(bytes);
  }
}
