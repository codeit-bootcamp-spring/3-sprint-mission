package com.sprint.mission.discodeit.util;

import com.sprint.mission.discodeit.dto.BinaryContent.BinaryContentCreateRequest;
import java.io.IOException;
import java.util.Optional;
import org.springframework.web.multipart.MultipartFile;

public class BinaryContentUtil {

    private BinaryContentUtil() {
    }

    public static Optional<BinaryContentCreateRequest> resolveFile(MultipartFile file) {
        if (file.isEmpty()) {
            return Optional.empty();
        } else {
            try {
                BinaryContentCreateRequest binaryContentCreateRequest = new BinaryContentCreateRequest(
                        file.getOriginalFilename(),
                        file.getContentType(),
                        file.getBytes()
                );
                return Optional.of(binaryContentCreateRequest);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
