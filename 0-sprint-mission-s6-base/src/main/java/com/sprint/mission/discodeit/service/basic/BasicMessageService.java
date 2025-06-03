package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.MessageRequest;
import com.sprint.mission.discodeit.dto.response.MessageResponse;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.mapper.MessageMapper;
import com.sprint.mission.discodeit.mapper.PageResponseMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import jakarta.transaction.Transactional;
import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@Service
@Slf4j
public class BasicMessageService implements MessageService {

  private final MessageRepository messageRepository;
  private final MessageMapper messageMapper;
  private final ChannelRepository channelRepository;
  private final UserRepository userRepository;
  private final BinaryContentRepository binaryContentRepository;
  private final BinaryContentStorage binaryContentStorage;


  @Override
  @Transactional
  public MessageResponse create(MessageRequest.Create request,
      List<MultipartFile> messageFiles) {
    User user = userRepository.findById(request.authorId()).orElseThrow(
        () -> new NoSuchElementException(
            "Author with id " + request.authorId() + " does not exist"));
    Channel channel = channelRepository.findById(request.channelId()).orElseThrow(
        () -> new NoSuchElementException(
            "Channel with id " + request.channelId() + " does not exist")
    );

    if (!channelRepository.existsById(request.channelId())) {
      throw new NoSuchElementException(
          "Channel with id " + request.channelId() + " does not exist");
    }
    if (!userRepository.existsById(request.authorId())) {
      throw new NoSuchElementException("Author with id " + request.authorId() + " does not exist");
    }

    Message message = Message.createMessage(request.content(), channel, user);
    Optional.ofNullable(messageFiles).ifPresent(files ->
        files.forEach(file -> {
          BinaryContent binaryContent = binaryContentRepository.save(
              BinaryContent.createBinaryContent(
                  file.getOriginalFilename(),
                  file.getSize(),
                  file.getContentType()
              )
          );
          binaryContentStorage.put(binaryContent.getId(), convertToBytes(file));
          message.insertAttachments(binaryContent);
        })
    );
    messageRepository.save(message);
    log.info("메시지 생성 : {}", message);
    return messageMapper.entityToDto(message);
  }

  @Override
  public MessageResponse find(UUID id) {
    return messageRepository.findById(id)
        .map(messageMapper::entityToDto)
        .orElseThrow(
            () -> new NoSuchElementException("Message with id " + id + " not found"));
  }

  @Override
  public PageResponse<MessageResponse> findAllByChannelId(UUID channelId, Pageable pageable) {
    channelRepository.findById(channelId).orElseThrow(
        () -> new NoSuchElementException("Channel with id " + channelId + " not found")
    );

    PageRequest pageRequest = PageRequest.of(0, 50, Sort.by("createdAt").descending());
    Slice<Message> slice = messageRepository.findAllByChannelId(channelId, pageable);
    Slice<MessageResponse> messageResponseSlice = slice.map(messageMapper::entityToDto);

    return PageResponseMapper.fromSlice(messageResponseSlice);
  }

  @Override
  @Transactional
  public MessageResponse update(UUID id, MessageRequest.Update request) {
    String newContent = request.newContent();
    Message message = messageRepository.findById(id)
        .orElseThrow(
            () -> new NoSuchElementException("Message with id " + id + " not found"));
    message.updateContent(newContent);
    messageRepository.save(message);

    log.info("update message : {}", message);

    return messageMapper.entityToDto(message);
  }

  @Override
  @Transactional
  public void delete(UUID id) {
    Message message = messageRepository.findById(id)
        .orElseThrow(
            () -> new NoSuchElementException("Message with id " + id + " not found")
        );

    messageRepository.deleteById(id);
  }

  private byte[] convertToBytes(MultipartFile imageFile) {
    try {
      return imageFile.getBytes();
    } catch (IOException e) {
      throw new RuntimeException("Failed to convert file to byte array", e);
    }
  }
}
