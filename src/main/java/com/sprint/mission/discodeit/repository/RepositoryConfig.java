package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.repository.file.FileBinaryContentRepository;
import com.sprint.mission.discodeit.repository.file.FileReadStatusRepository;
import com.sprint.mission.discodeit.repository.file.FileUserStatusRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFBinaryContentRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFReadStatusRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFUserStatusRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RepositoryConfig {

    @Value("${discodeit.repository.type")
    private String repositoryType;

    @Value("${discodeit.repository.file-directory")
    private String fileDirectory;

    @Bean
    public UserStatusRepository userStatusRepository() {
        if ("file".equalsIgnoreCase(repositoryType)) {
            return new FileUserStatusRepository(fileDirectory);
        } else {
            return new JCFUserStatusRepository();
        }
    }
    @Bean
    public ReadStatusRepository readStatusRepository() {
        if ("file".equalsIgnoreCase(repositoryType)) {
            return new FileReadStatusRepository(fileDirectory);
        } else {
            return new JCFReadStatusRepository();
        }
    }
    @Bean
    public BinaryContentRepository binaryContentRepository() {
        if ("file".equalsIgnoreCase(repositoryType)) {
            return new FileBinaryContentRepository(fileDirectory);
        } else {
            return new JCFBinaryContentRepository();
        }
    }
}
