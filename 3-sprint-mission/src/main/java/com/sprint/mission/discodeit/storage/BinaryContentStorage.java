package com.sprint.mission.discodeit.storage;

import com.sprint.mission.discodeit.dto.data.BinaryContentDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.UUID;

@Component
public interface BinaryContentStorage {

    UUID put(UUID id, byte[] bytes);
    InputStream get(UUID id);
    ResponseEntity<?> download(BinaryContentDTO dto);
}
