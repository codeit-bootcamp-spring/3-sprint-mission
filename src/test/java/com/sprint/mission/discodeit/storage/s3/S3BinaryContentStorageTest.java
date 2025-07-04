package com.sprint.mission.discodeit.storage.s3;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.storage.S3BinaryContentStorage;
import java.io.InputStream;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = {
    "spring.sql.init.mode=never",
    "discodeit.storage.type=s3"
})
class S3BinaryContentStorageTest {

    @Autowired
    private S3BinaryContentStorage storage;

    @Test
    void putAndGetTest() throws Exception {
        UUID id = UUID.randomUUID();
        byte[] data = "Hello S3!".getBytes();

        storage.put(id, data);

        try (InputStream in = storage.get(id)) {
            byte[] result = in.readAllBytes();
            assertArrayEquals(data, result);
        }
    }

    @Test
    void presignedUrlTest() {
        UUID id = UUID.randomUUID();
        byte[] data = "Hello S3!".getBytes();
        storage.put(id, data);

        BinaryContentDto dto = new BinaryContentDto(
            id,
            "test.txt",
            (long) data.length,
            "text/plain"
        );

        ResponseEntity<?> response = storage.download(dto);

        String url = response.getHeaders().getLocation().toString();
        assertNotNull(url);
        assertTrue(url.startsWith("http"));
        assertTrue(url.contains(id.toString()));

        System.out.println("Presigned URL: " + url);
    }

    @Test
    void deleteTest() throws Exception {
        UUID id = UUID.randomUUID();
        byte[] data = "Delete me".getBytes();

        storage.put(id, data);
        storage.delete(id);

        assertThrows(Exception.class, () -> {
            try (InputStream in = storage.get(id)) {
                in.read();
            }
        });
    }
}
