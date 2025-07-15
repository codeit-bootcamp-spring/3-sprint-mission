package com.sprint.mission.discodeit.service;


import com.sprint.mission.discodeit.dto.message.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.message.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.message.response.PageResponse;
import com.sprint.mission.discodeit.dto.message.response.MessageResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
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

    MessageResponse createMessage(MessageCreateRequest MessageAttachmentRequest, List<MultipartFile> multipartFiles);

    MessageResponse updateMessage(UUID messageId, MessageUpdateRequest request);

    void deleteMessage(UUID messageId);

    PageResponse findAllByChannelIdAndCursor(UUID channelId, Instant cursor, Pageable pageable);
}
