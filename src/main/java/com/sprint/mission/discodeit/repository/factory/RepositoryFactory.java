package com.sprint.mission.discodeit.repository.factory;

import com.sprint.mission.discodeit.repository.file.FileBinaryContentRepository;
import com.sprint.mission.discodeit.repository.file.FileChannelRepository;
import com.sprint.mission.discodeit.repository.file.FileMessageRepository;
import com.sprint.mission.discodeit.repository.file.FileUserRepository;
import com.sprint.mission.discodeit.repository.file.FileUserStatusRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFBinaryContentRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFChannelRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFMessageRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFUserRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFUserStatusRepository;

public class RepositoryFactory {

  public static RepositoryBundle createJCFRepositories() {
    return new RepositoryBundle(
        new JCFUserRepository(),
        new JCFUserStatusRepository(),
        new JCFChannelRepository(),
        new JCFMessageRepository(),
        new JCFBinaryContentRepository()
    );
  }

  public static RepositoryBundle createFileRepositories() {
    return new RepositoryBundle(
        FileUserRepository.createDefault(),
        FileUserStatusRepository.createDefault(),
        FileChannelRepository.createDefault(),
        FileMessageRepository.createDefault(),
        FileBinaryContentRepository.createDefault()
    );
  }
}
