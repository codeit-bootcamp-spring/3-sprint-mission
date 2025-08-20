package com.sprint.mission.discodeit.storage.s3;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "discodeit.storage.s3")
public record AwsProperties(
    String accessKey,
    String secretKey,
    String region,
    String bucket,
    Long presignedUrlExpiration
) { }