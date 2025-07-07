package com.sprint.mission.discodeit.config;


import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "discodeit.storage.s3")
public class AWSS3Properties {

    private String accessKey;
    private String secretKey;
    private String bucket;
    private String region;

}
