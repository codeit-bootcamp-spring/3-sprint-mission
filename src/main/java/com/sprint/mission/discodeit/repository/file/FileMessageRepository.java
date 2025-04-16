package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;

public class FileMessageRepository implements MessageRepository {

    private final ChannelRepository channelRepository;

    public FileMessageRepository(ChannelRepository channelRepository) {
        this.channelRepository = channelRepository;
    }

}
