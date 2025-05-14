package com.sprint.mission.discodeit.service.basic;

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
    public Message create(CreateMessageRequest request) {
        if(request.attachments().isPresent()){
            List<UUID> attachmentsId = new ArrayList<>();
            request.attachments().get()
                    .forEach(attachment->{
                        BinaryContent binaryContent = binaryContentRepository.save(new BinaryContent(attachment.contentType(), attachment.content()));
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
    public void update(UpdateMessageRequest request) {
        Optional<Message> message = messageRepository.readById(request.id());
        try {
            if (message.isPresent()) {
                Message updateMessage = new Message(message.get().getChannelId(),message.get().getAuthorId(),message.get().getAttachmentIds(), request.text());
                messageRepository.update(request.id(),updateMessage);
            }
            else{
                    throw new NoSuchElementException("해당 id의 메세지는 존재하지 않습니다.");
            }

        }catch (NoSuchElementException e) {
            System.out.println(e);
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
