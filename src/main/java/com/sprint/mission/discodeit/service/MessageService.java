package com.sprint.mission.discodeit.service;


import com.sprint.mission.discodeit.Dto.message.*;
import com.sprint.mission.discodeit.entity.Message;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

/**
 * packageName    : com.sprint.mission.discodeit.refactor.service
 * fileName       : MessageService
 * author         : doungukkim
 * date           : 2025. 4. 17.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2025. 4. 17.        doungukkim       최초 생성
 * 2025. 4. 28.        doungukkim       createMessage(DTO) 두개 완성, 테스트 필요
 */
public interface MessageService {

    List<FoundMessagesResponse> findAllByChannelId(UUID channelId);

    MessageAttachmentsCreateResponse createMessage(MessageCreateRequest MessageAttachmentRequest, List<MultipartFile> multipartFiles);

    UpdateMessageResponse updateMessage(UUID messageId, MessageUpdateRequest request);

    boolean deleteMessage(UUID messageId);


    // not required
    Message findMessageById(UUID messageId);

    // not required
    List<Message> findAllMessages();


}
