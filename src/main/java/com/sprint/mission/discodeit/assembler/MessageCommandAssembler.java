package com.sprint.mission.discodeit.assembler;

import com.sprint.mission.discodeit.dto.data.BinaryContentData;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.mapper.MultipartFileMapper;
import com.sprint.mission.discodeit.service.command.CreateMessageCommand;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
@RequiredArgsConstructor
public class MessageCommandAssembler {

  private final MultipartFileMapper multipartFileMapper;

  public CreateMessageCommand toCreateCommand(MessageCreateRequest request,
      List<MultipartFile> attachments) {
    List<BinaryContentData> binaryContentDataList =
        multipartFileMapper.toBinaryContentDataList(attachments);

    return new CreateMessageCommand(
        request.content(),
        request.authorId(),
        request.channelId(),
        binaryContentDataList);
  }
}

