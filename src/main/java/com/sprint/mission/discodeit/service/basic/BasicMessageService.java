package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class BasicMessageService implements MessageService {

  private final MessageRepository messageRepository;
  //
  private final ChannelRepository channelRepository;
  private final UserRepository userRepository;
  private final BinaryContentRepository binaryContentRepository;

  @Override
  public Message create(MessageCreateRequest request) {
    UUID channelId = request.channelId();
    UUID userId = request.authorId();
    String text = request.content();
    if (channelRepository.findById(channelId) == null) {
      throw new NoSuchElementException("생성 실패 : 잘못된 채널 ID입니다.");
    } else if (userRepository.findById(userId) == null) {
      throw new NoSuchElementException("생성 실패 : 잘못된 유저 ID입니다.");
    }
    Message message = new Message(
        text,
        channelId,
        userId
    );
    return messageRepository.save(message);
  }

  @Override
  public Message create(MessageCreateRequest request,
      List<BinaryContentCreateRequest> contentRequest) {
    Message message = this.create(request);
    if (contentRequest != null && contentRequest.get(0).content().length > 1) {
      List<UUID> attachmentsIds = contentRequest.stream()
          .map(attachmentsRequest -> {
            String filename = attachmentsRequest.fileName();
            String contentType = attachmentsRequest.contentType();
            byte[] contents = attachmentsRequest.content();

            BinaryContent binaryContent = new BinaryContent(filename, (long) contents.length,
                contentType, contents);
            BinaryContent createdBinaryContent = binaryContentRepository.save(binaryContent);
            return createdBinaryContent.getId();
          }).toList();
      message.setContentIds(attachmentsIds);
    }

    return messageRepository.save(message);
  }

  @Override
  public Message findById(UUID messageId) {
    return messageRepository.findById(messageId);
  }

  @Override
  public List<Message> findByChannel(UUID channelId) {
    List<Message> sortedList = messageRepository.findByChannel(channelId).stream()
        .sorted(Comparator.comparing(Message::getCreatedAt).reversed())
        .collect(Collectors.toList());
    return sortedList;
  }


  @Override
  public Message update(UUID messageId, MessageUpdateRequest request) {
    String text = request.newContent();
    Message message = findById(messageId);
    if (message == null) {
      throw new NoSuchElementException("수정 실패 : 존재하지 않는 메세지 Id입니다.");
    } else {
      message.update(text);
      messageRepository.save(message);
      return message;
    }
  }

  @Override
  public void delete(UUID messageId) {
    Message message = findById(messageId);
    if (!messageRepository.existsById(messageId)) {
      throw new NoSuchElementException("삭제 실패 : 존재하지 않는 메세지 Id입니다.");
    } else {
      if (!message.getContentIds().isEmpty()) {
        message.getContentIds().forEach(binaryContentRepository::deleteById);
      }
      messageRepository.delete(messageId);
    }
  }
}
