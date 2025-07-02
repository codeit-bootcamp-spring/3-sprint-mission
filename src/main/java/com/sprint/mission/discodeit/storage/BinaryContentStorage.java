package com.sprint.mission.discodeit.storage;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import java.io.InputStream;
import java.util.UUID;
import org.springframework.http.ResponseEntity;

public interface BinaryContentStorage {

    UUID put(UUID uuid, byte[] content, String contentType);

    InputStream get(UUID binaryContentId);

    ResponseEntity<?> download(BinaryContentDto metaData);
}
