package com.sprint.mission.discodeit.storage;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import java.io.InputStream;
import java.util.UUID;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;

public interface BinaryContentStorage {

    UUID put(UUID binaryContentId, byte[] bytes);

    void put(String objectKey, InputStream in, long contentLength, String contentType)
        throws Exception;

    InputStream get(UUID binaryContentId);

    ResponseEntity<Resource> download(BinaryContentDto binaryContentDto);

}
