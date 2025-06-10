package com.sprint.mission.discodeit.storage;

import com.sprint.mission.discodeit.dto.serviceDto.BinaryContentDto;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.UUID;
import org.springframework.http.ResponseEntity;

public interface BinaryContentStorage {

    UUID put(UUID binaryContentId, byte[] bytes) throws IOException;

    InputStream get(UUID binaryContentId) throws IOException;

    ResponseEntity<?> download(BinaryContentDto binaryContentDto) throws MalformedURLException;

}
