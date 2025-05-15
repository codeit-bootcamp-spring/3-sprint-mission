package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.CreateBinaryContentRequest;
import com.sprint.mission.discodeit.dto.CreateMessageRequest;
import com.sprint.mission.discodeit.dto.UpdateMessageRequest;
import com.sprint.mission.discodeit.entitiy.BinaryContent;
import com.sprint.mission.discodeit.entitiy.Message;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class BasicMessageService implements MessageService {

    private final MessageRepository messageRepository;
    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;
    private final BinaryContentRepository binaryContentRepository;

    @Override
    public Message create(CreateMessageRequest request, Optional<List<CreateBinaryContentRequest>> attachments) {
        if(attachments.isPresent()){
            List<UUID> attachmentsId = new ArrayList<>();
            attachments.get()
                    .forEach(attachment->{
                        BinaryContent binaryContent = binaryContentRepository.save(new BinaryContent(attachment.filename(),attachment.contentType(), attachment.bytes()));
                        attachmentsId.add(binaryContent.getId());
                    });
            return messageRepository.save(new Message(request.channelId(), request.authorId(), attachmentsId, request.text()));
        }else{
            return messageRepository.save(new Message(request.channelId(), request.authorId(), null, request.text()));
        }

    }

    @Override
    public List<Message> findAllByChannelId(UUID channelId) {
        List<Message> read = messageRepository.read();
        List<Message> messageList = read.stream()
                .filter(message -> message.getChannelId().equals(channelId))
                .toList();
        return messageList;
    }

    @Override
    public void update(UpdateMessageRequest request, Optional<List<CreateBinaryContentRequest>> createBinaryContentRequest) {
        Optional<Message> optionalMessage = messageRepository.readById(request.id());

        try {
            // 메시지 존재 여부 확인
            optionalMessage.ifPresentOrElse(existingMessage -> {
                List<UUID> attachmentIds = new ArrayList<>();

                // 기존 메시지에 첨부파일이 있을 경우 삭제
                if (existingMessage.getAttachmentIds() != null) {
                    attachmentIds.addAll(existingMessage.getAttachmentIds());
                    attachmentIds.forEach(binaryContentRepository::delete);
                    existingMessage.setAttachmentIds(null);
                }

                // 업데이트할 이미지가 있을 경우
                if (createBinaryContentRequest.isPresent()) {
                    // 새 이미지를 저장하고 첨부파일 ID 리스트 업데이트
                    createBinaryContentRequest.get().forEach(attachment -> {
                        BinaryContent binaryContent = binaryContentRepository.save(
                                new BinaryContent(attachment.filename(), attachment.contentType(), attachment.bytes())
                        );
                        attachmentIds.add(binaryContent.getId());
                    });

                    // 첨부파일 갱신된 메시지 저장
                    Message updatedMessage = new Message(
                            existingMessage.getChannelId(),
                            existingMessage.getAuthorId(),
                            attachmentIds,
                            request.text()
                    );
                    messageRepository.update(request.id(), updatedMessage);

                } else {
                    // 새 이미지가 없을 경우 텍스트만 업데이트
                    Message updatedMessage = new Message(
                            existingMessage.getChannelId(),
                            existingMessage.getAuthorId(),
                            existingMessage.getAttachmentIds(),
                            request.text()
                    );
                    messageRepository.update(request.id(), updatedMessage);
                }
            }, () -> {
                throw new NoSuchElementException("해당 ID의 메시지는 존재하지 않습니다.");
            });

        } catch (NoSuchElementException e) {
            System.out.println(e.getMessage());
        }

    }

    @Override
    public void delete(UUID messageId) {
        Optional<Message> message = messageRepository.readById(messageId);
        try {
            if (message.isPresent()) {
                if(message.get().getAttachmentIds()==null){
                    messageRepository.delete(messageId);
                }
                else {
                    List<UUID> attachmentIds = message.get().getAttachmentIds();
                    attachmentIds.forEach(attachmentId -> {
                        Optional<BinaryContent> binaryContent = binaryContentRepository.readById(attachmentId);
                        if (binaryContent.isPresent()) {
                            binaryContentRepository.delete(binaryContent.get().getId());
                        }
                    });
                    messageRepository.delete(messageId);
                }
            }
            else {
                throw new NoSuchElementException("해당 id의 메세지는 존재하지 않습니다.");
            }
        }catch (NoSuchElementException e){
            System.out.println(e);
        }

    }
}
