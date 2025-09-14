package com.sprint.mission.discodeit.config;

import jakarta.annotation.PostConstruct;
import java.util.logging.Logger;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(
    prefix = "discodeit.storage.s3",
    name = "enabled",
    havingValue = "true",
    matchIfMissing = false
)
@Getter
public class AwsServiceConfiguration {
    private static final Logger logger = Logger.getLogger(AwsServiceConfiguration.class.getName());


    private String url;
    private String username;
    private String password;
    private String region;
    private String bucketname;

    public AwsServiceConfiguration(@Value("${spring.datasource.url}") String url,
            @Value("${spring.datasource.username}") String username,
            @Value("${spring.datasource.password}") String password,
            @Value("${discodeit.storage.s3.region}") String region,
            @Value("${discodeit.storage.s3.bucket}") String bucketname
            ) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.region = region;
        this.bucketname = bucketname;
    }

    @PostConstruct
    public void printCompanyName() {
        logger.info("데이터베이스 URL = " + url);
        logger.info("데이터베이스 region = " + region);
    }
}
