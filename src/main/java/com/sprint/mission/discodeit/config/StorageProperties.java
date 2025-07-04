package com.sprint.mission.discodeit.config;

import java.time.Duration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.ConstructorBinding;

@ConfigurationProperties(prefix = "discodeit.storage")
public class StorageProperties {

    private final String type;
    private final Local local;
    private final S3 s3;

    @ConstructorBinding
    public StorageProperties(String type, Local local, S3 s3) {
        this.type = type;
        this.local = local;
        this.s3 = s3;
    }

    public String type() { return type; }
    public Local local() { return local; }
    public S3 s3() { return s3; }

    public record Local(String rootPath) {}
    public record S3(
        String accessKey,
        String secretKey,
        String region,
        String bucket,
        Duration presignedUrlExpiration   // ISO-8601(PnDTnHnMnS) 또는 숫자(분) 자동 매핑
    ) {}
}