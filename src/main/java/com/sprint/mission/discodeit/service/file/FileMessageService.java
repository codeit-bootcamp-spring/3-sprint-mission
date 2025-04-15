package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Message;

import java.util.List;
import java.util.UUID;

/**
 * packageName    : com.sprint.mission.discodeit.service.file
 * fileName       : FileMessageService
 * author         : doungukkim
 * date           : 2025. 4. 14.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2025. 4. 14.        doungukkim       최초 생성
 */
public class FileMessageService {
    public FileChannelService fileChannelService;

    public FileMessageService(FileChannelService fileChannelService) {
        this.fileChannelService = fileChannelService;
    }
//    -------------------interface-------------------

//    UUID createMessage(UUID senderId, UUID channelId, String message);
//    List<Message> findAllMessages();
//    Message findMessageByMessageId(UUID messageId);
//    void updateMessage(UUID messageId, String newMessage);
//    void deleteMessageById(UUID messageId);
//    void deleteMessagesByChannelId(UUID channelId);

//    -------------------------------------------------

}
