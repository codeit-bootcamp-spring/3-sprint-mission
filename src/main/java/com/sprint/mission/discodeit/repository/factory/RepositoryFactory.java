package com.sprint.mission.discodeit.repository.factory;

import com.sprint.mission.discodeit.repository.file.FileChannelRepository;
import com.sprint.mission.discodeit.repository.file.FileMessageRepository;
import com.sprint.mission.discodeit.repository.file.FileUserRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFChannelRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFMessageRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFUserRepository;

public class RepositoryFactory {

  public static RepositoryBundle createJCFRepositories() {
    return new RepositoryBundle(
        new JCFUserRepository(),
        new JCFChannelRepository(),
        new JCFMessageRepository()
    );
  }

  public static RepositoryBundle createFileRepositories() {
    return new RepositoryBundle(
        FileUserRepository.createDefault(),
        FileChannelRepository.createDefault(),
        FileMessageRepository.createDefault()
    );
  }
}
