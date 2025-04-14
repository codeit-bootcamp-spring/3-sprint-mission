package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;

import java.util.List;
import java.util.UUID;

/**
 * packageName    : com.sprint.mission.discodeit.service.file
 * fileName       : FileChannelService
 * author         : doungukkim
 * date           : 2025. 4. 14.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2025. 4. 14.        doungukkim       최초 생성
 */
public class FileChannelService {
    private FileUserService fileUserService;
    private FileMessageService fileMessageService;

    public FileChannelService() {
    }

    public void setFileChannelService(FileMessageService fileMessageService, FileUserService fileUserService) {
        this.fileMessageService = fileMessageService;
        this.fileUserService = fileUserService;
    }

//    UUID createChannel(UUID userId);
//    Channel findChannelById(UUID channelId);
//    List<Channel> findChannelsByUserId(UUID userId);
//    List<Channel> findAllChannel();
//    void updateChannelName(UUID channelId, String title);
//    void deleteChannel(UUID channelId);
//    void addMessageInChannel(UUID channelId, Message message);
//    void deleteMessageInChannel(UUID messageId);
//    void addUserInChannel(UUID userId, UUID channelId);



}
