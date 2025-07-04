package com.sprint.mission.discodeit.config;

import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import com.sprint.mission.discodeit.storage.s3.S3BinaryContentStorage;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StorageConfig {

    @Bean
    @ConditionalOnProperty(name = "discodeit.storage.type", havingValue = "s3")
    public BinaryContentStorage s3BinaryContentStorage(StorageProperties props) {
        StorageProperties.S3 s3 = props.s3();
        return new S3BinaryContentStorage(
            s3.accessKey(),
            s3.secretKey(),
            s3.region(),
            s3.bucket(),
            s3.presignedUrlExpiration()
        );
    }

}