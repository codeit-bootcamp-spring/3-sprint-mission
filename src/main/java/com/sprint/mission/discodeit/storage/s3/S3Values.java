package com.sprint.mission.discodeit.storage.s3;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * PackageName  : com.sprint.mission.discodeit.storage.s3
 * FileName     : S3Values
 * Author       : dounguk
 * Date         : 2025. 7. 1.
 */
@Getter
@Component
@ConditionalOnProperty(name = "discodeit.storage.type", havingValue = "aws")
public class S3Values {

    private final String bucketName;
    private final String region;
    private final String accessKey;
    private final String secretKey;
    private final long presignedUrlExpiration;

    public S3Values(@Value("${discodeit.storage.s3.access-key}") String accessKey,
                    @Value("${discodeit.storage.s3.secret-key}") String secretKey,
                    @Value("${discodeit.storage.s3.bucket}") String bucketName,
                    @Value("${discodeit.storage.s3.region}") String region,
                    @Value("${discodeit.storage.s3.presigned-url-expiration:}") long presignedUrlExpiration){

        this.bucketName = bucketName;
        this.region = region;
        this.accessKey = accessKey;
        this.secretKey = secretKey;
        this.presignedUrlExpiration = presignedUrlExpiration;

    }
}
