package com.sprint.mission.discodeit.storage.s3;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class S3BinaryContentStorageTest {

    @Value("${discodeit.storage.s3.access-key}")
    String accessKey;
    @Value("${discodeit.storage.s3.secret-key}")
    String secretKey;
    @Value("${discodeit.storage.s3.bucket}")
    String bucket;
    @Value("${discodeit.storage.s3.region}")
    String region;

    @Test
    @DisplayName("S3 put 성공 테스트")
    void put_success() {
        S3BinaryContentStorage storage = new S3BinaryContentStorage(accessKey, secretKey, bucket, region);
        UUID id = UUID.randomUUID();
        byte[] data = "hello s3".getBytes();
        UUID result = storage.put(id, data);
        assertThat(result).isEqualTo(id);
    }

    @Test
    @DisplayName("S3 put 실패 테스트 - 잘못된 자격증명")
    void put_fail_invalid_credentials() {
        S3BinaryContentStorage storage = new S3BinaryContentStorage("invalid", "invalid", bucket, region);
        UUID id = UUID.randomUUID();
        byte[] data = "fail test".getBytes();
        assertThatThrownBy(() -> storage.put(id, data))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("S3 업로드 실패");
    }
}