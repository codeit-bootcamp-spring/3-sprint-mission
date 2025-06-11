package com.sprint.mission.discodeit.storage;

import com.sprint.mission.discodeit.dto.binaryContent.JpaBinaryContentResponse;
import org.springframework.http.ResponseEntity;

import java.io.InputStream;
import java.util.UUID;

/**
 * PackageName  : com.sprint.mission.discodeit.storage
 * FileName     : BinaryContentStorage
 * Author       : dounguk
 * Date         : 2025. 5. 30.
 */
public interface BinaryContentStorage {

    UUID put(UUID BinaryContentId, byte[] bytes);

    InputStream get(UUID BinaryContentId);

    ResponseEntity<?> download(JpaBinaryContentResponse response);

}
