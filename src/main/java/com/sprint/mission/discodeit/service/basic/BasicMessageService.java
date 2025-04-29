package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.MessageCreateResponse;
import com.sprint.mission.discodeit.dto.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicMessageService implements MessageService {
    private final MessageRepository messageRepository;
    //

    @Override
    public MessageCreateResponse create(MessageCreateRequest createRequest) {
        //TODO : 유저가 해당 채널에 있는지 validation check 후 메세지 생성


        //TODO : 선택적으로 여러개의 첨부파일 같이 등록 가능
        //QUESTION. 메세지 create 하기 전에 createRequest에서 attachmentIds를 알수있나? 여기서 파일 추가할껀데?

        Message message = new Message(createRequest);

        //FIXME : MessageCreateResponse에는 Message 밖에없는데
        this.messageRepository.save(message);

        return new MessageCreateResponse(message);
    }

    @Override
    public MessageCreateResponse findById(UUID messageId) {
        Message message = this.messageRepository
                .findById(messageId)
                .orElseThrow(() -> new NoSuchElementException("Message with id " + messageId + " not found"));

        return new MessageCreateResponse(message);
    }

    @Override
    public List<MessageCreateResponse> findAllByChannelId(UUID channelId) {

        List<MessageCreateResponse> messages = this.messageRepository
                .findAll()
                .stream().filter((message) -> {
                    return message.getChannelId().equals(channelId);
                }).map(MessageCreateResponse::new).toList();


        return messages;
    }

    @Override
    public MessageCreateResponse update(MessageUpdateRequest updateRequest) {
        // 다른 메소드에서 this.find() 이제 사용 못함.
        Message message = this.messageRepository.findById(updateRequest.messageId())
                .orElseThrow(() -> new NoSuchElementException("Message with id " + updateRequest.messageId() + " not found"));

        message.update(updateRequest.newContent(), updateRequest.attachmentIds());

        //QUESTION: 이렇게 하면 파일레포일때 파일 업데이트가되나??? -> 안됨
        return new MessageCreateResponse(message);
    }

    @Override
    public void delete(UUID messageId) {
        Message message = this.messageRepository.findById(messageId).
                orElseThrow(() -> new NoSuchElementException("Message with messageId " + messageId + " not found"));

        //TODO : BinaryContentRepository에서 해당 객체 삭제,
//        if(!message.getAttachmentIds().isEmpty()){
//            for(UUID binaryContentId : message.getAttachmentIds()){
//                this.BinaryContentRepository.deleteById(binaryContentId);
//            }
//        }

        this.messageRepository.deleteById(messageId);
    }

}
