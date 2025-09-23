package com.sprint.mission.discodeit.storage.s3;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.NotificationRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.storage.S3BinaryContentStorage;
import io.github.cdimascio.dotenv.Dotenv;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class S3BinaryContentStorageTest {


    private static S3BinaryContentStorage storage;
    @Mock
    private BinaryContentRepository binaryContentRepository;
    @Mock
    private NotificationRepository notificationRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ApplicationEventPublisher applicationEventPublisher;

    @BeforeEach
    public void setup() {

        Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();

        String accessKey = Optional.ofNullable(System.getenv("AWS_ACCESS_KEY"))
                .orElse(dotenv.get("AWS_S3_ACCESS_KEY"));

        String secretKey = Optional.ofNullable(System.getenv("AWS_SECRET_KEY"))
                .orElse(dotenv.get("AWS_S3_SECRET_KEY"));

        String region = Optional.ofNullable(System.getenv("AWS_REGION"))
                .orElse(dotenv.get("AWS_S3_REGION"));

        String bucket = Optional.ofNullable(System.getenv("AWS_BUCKET"))
                .orElse(dotenv.get("AWS_S3_BUCKET"));
        int presignedUrlExpiration = 600;

        storage = new S3BinaryContentStorage(
                accessKey, secretKey, region, bucket, presignedUrlExpiration, binaryContentRepository, notificationRepository, userRepository, applicationEventPublisher
        );
    }

    @Test
    public void testUploadImageFile() throws Exception {
        // given
        String filePath = "src/test/resources/KakaoTalk_Photo_2025-06-16-10-27-10.jpeg";
        try (InputStream inputStream = new FileInputStream(filePath)) {

            MockMultipartFile mockFile = new MockMultipartFile(
                    "file",
                    "KakaoTalk_Photo_2025-06-16-10-27-10.jpeg",
                    "image/jpeg",
                    inputStream
            );

            UUID id = UUID.randomUUID();
            when(binaryContentRepository.findById(id)).thenReturn(Optional.of(new BinaryContent("file", 10L, "jpeg")));

            // when
            UUID result = storage.put(id, mockFile.getBytes());

            // then
            assertNotNull(result);
        }
    }

    @Test
    public void testGet() throws Exception {
        // given
        UUID testId = UUID.fromString("8a36f250-6e52-4ec6-9e3c-e47046d66028");

        // when & then
        try (InputStream inputStream = storage.get(testId)) {
            assertNotNull(inputStream);
            byte[] buffer = inputStream.readNBytes(100); // 일부만 읽기
            assertTrue(buffer.length > 0);
        }
    }

    @Test
    public void testDownload() throws Exception {
        // given
        BinaryContentDto dto = new BinaryContentDto(UUID.fromString("8a36f250-6e52-4ec6-9e3c-e47046d66028"), "file.jpg", null, "image/jpeg", null);

        // when
        ResponseEntity<?> response = storage.download(dto);

        // then
        assertEquals(302, response.getStatusCodeValue());
        assertNotNull(response.getHeaders().getLocation());
        assertTrue(response.getHeaders().getLocation().toString().contains("s3"),
                "Presigned URL이 S3 주소를 포함하지 않습니다.");


    }
}
