package com.sprint.mission.discodeit.service.basic;

import java.io.IOException;
import java.util.UUID;
import javax.transaction.Transactional;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.StorageService;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.dto.MessageDto;
import com.sprint.mission.discodeit.service.request.MessageCreateRequest;
import com.sprint.mission.discodeit.service.request.MessageUpdateRequest;

@Service
public class BasicMessageService implements MessageService {

  private final MessageRepository messageRepository;
  private final ChannelRepository channelRepository;
  private final UserRepository userRepository;
  private final StorageService binaryContentStorage;
  private final BinaryContentRepository binaryContentRepository;

  public BasicMessageService(MessageRepository messageRepository,
                             ChannelRepository channelRepository,
                             UserRepository userRepository,
                             StorageService binaryContentStorage,
                             BinaryContentRepository binaryContentRepository) {
    this.messageRepository = messageRepository;
    this.channelRepository = channelRepository;
    this.userRepository = userRepository;
    this.binaryContentStorage = binaryContentStorage;
    this.binaryContentRepository = binaryContentRepository;
  }

  @Transactional
  @Override
  public MessageDto create(MessageCreateRequest req, MultipartFile[] attachments) throws IOException {
    Channel channel = channelRepository.findById(req.channelId())
        .orElseThrow(() -> new NotFoundException("Channel", req.channelId().toString()));
    User author = userRepository.findById(req.authorId())
        .orElseThrow(() -> new NotFoundException("Author", req.authorId().toString()));

    Message m = Message.builder()
        .content(req.content())
        .channel(channel)
        .author(author)
        .build();

    if (attachments != null) {
      for (MultipartFile file : attachments) {
        if (file != null && !file.isEmpty()) {
          BinaryContent bc = BinaryContent.builder()
              .id(UUID.randomUUID())
              .fileName(file.getOriginalFilename())
              .size(file.getSize())
              .contentType(file.getContentType())
              .bytes(file.getBytes())
              .build();
          binaryContentStorage.put(bc.getId(), file.getBytes());
          binaryContentRepository.save(bc);
          m.addAttachment(bc);
        }
      }
    }

    messageRepository.save(m);
    return MessageDto.from(m);
  }

  @Transactional
  @Override
  public MessageDto update(UUID messageId, MessageUpdateRequest req) {
    Message m = messageRepository.findById(messageId)
        .orElseThrow(() -> new NotFoundException("Message", messageId.toString()));
    m.updateContent(req.newContent());
    messageRepository.save(m);
    return MessageDto.from(m);
  }

  @Transactional
  @Override
  public void delete(UUID messageId) {
    messageRepository.deleteById(messageId);
  }
}