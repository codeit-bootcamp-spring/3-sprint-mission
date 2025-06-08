package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.MessageDTO;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.mapper.MessageMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional
public class BasicMessageService implements MessageService {

  private final UserRepository userRepository;
  private final ChannelRepository channelRepository;
  private final MessageRepository messageRepository;
  private final BinaryContentRepository binaryContentRepository;
  private final MessageMapper messageMapper;

  @Override
  public Message create(MessageCreateRequest messageCreateRequest,
      List<BinaryContentCreateRequest> binaryContentCreateRequests) {
    User user = messageCreateRequest.author();
    Channel channel = messageCreateRequest.channel();

    if (!userRepository.existsById(user.getId())) {
      throw new NoSuchElementException("해당 사용자가 존재하지 않습니다.");
    }
    if (!channelRepository.existsById(channel.getId())) {
      throw new NoSuchElementException("해당 채널이 존재하지 않습니다.");
    }

    String text = messageCreateRequest.text();
    List<BinaryContent> attachments = binaryContentCreateRequests.stream()
        .map(contents -> {
          String fileName = contents.fileName();
          byte[] bytes = contents.bytes();
          String contentType = contents.contentType();

          BinaryContent binaryContent =
              BinaryContent.builder()
                  .fileName(fileName)
                  .size((long) bytes.length)
                  .contentType(contentType)
                  .bytes(bytes)
                  .build();

          binaryContentRepository.save(binaryContent);
          return binaryContent;

        })
        .toList();

    Message message =
        Message.builder()
            .currentChannel(channel)
            .currentUser(user)
            .content(text)
            .attachments(attachments)
            .build();

    return messageRepository.save(message);
  }

  @Override
  @Transactional(readOnly = true)
  public List<MessageDTO> findAllByChannelId(UUID channelId) {

    return messageRepository.findAllByChannelId(channelId)
        .stream()
            .map(messageMapper::toDTO)
            .toList();
  }

  @Override
  @Transactional(readOnly = true)
  public MessageDTO find(UUID id) {

    return messageRepository.findById(id)
            .map(messageMapper::toDTO)
        .orElseThrow(() -> new NoSuchElementException("해당 메시지가 존재하지 않습니다."));
  }

  ;

  @Override
  @Transactional(readOnly = true)
  public List<MessageDTO> findByContent(String text) {

    return messageRepository.findByContent(text)
        .stream()
            .map( messageMapper::toDTO)
        .toList();
  }

  @Override
  public Message update(UUID id, MessageUpdateRequest messageUpdateDTO) {
    String newText = messageUpdateDTO.newText();
    Message message = messageRepository.findById(id)
        .orElseThrow(() -> new NoSuchElementException("해당 메시지가 존재하지 않습니다."));
    message.update(newText);

    return message;
  }

  @Override
  public void delete(UUID id) {
    Message message = messageRepository.findById(id)
        .orElseThrow(() -> new NoSuchElementException("해당 메시지가 존재하지 않습니다."));
    message.getAttachments().stream()
        .map(BinaryContent::getId)
        .forEach(binaryContentRepository::deleteById);

    messageRepository.deleteById(id);

  }
}
