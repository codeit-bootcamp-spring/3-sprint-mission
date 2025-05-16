package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class BasicMessageService implements MessageService {

    private static final Logger logger = Logger.getLogger(BasicMessageService.class.getName()); // 필드로 Logger 선언

    private static final String FILE_PATH = "message.ser";

    // 의존성
    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;
    private final MessageRepository messageRepository;
    private final BinaryContentRepository binaryContentRepository;

    //메시지 생성
    @Override
    public Message createMessage(MessageCreateRequest request, List<BinaryContentCreateRequest> binaryContentCreateRequests) {

        User user = userRepository.findUserById(request.senderId())
                .orElseThrow(() -> new NoSuchElementException("MessageService: 유저가 존재하지 않습니다."));

        Channel channel = channelRepository.findChannelUsingId(request.channelId())
                .orElseThrow(() -> new NoSuchElementException("MessageService: 채널이 존재하지 않습니다."));


        if(isParticipant(channel, request.senderId()) && isChannelLock(channel, request.password())){

            Message newMessage = new Message(request.channelId(), request.senderId(), request.messageContent());
            Message created = messageRepository.saveMessage(newMessage);

            if (created == null) {
                throw new IllegalStateException("MessageService: 메시지 저장 실패");
            }

            List<UUID> attachmentIds = binaryContentCreateRequests.stream()
                    .map(attachmentRequest -> {
                        String fileName = attachmentRequest.fileName();
                        String contentType = attachmentRequest.contentType();
                        byte[] bytes = attachmentRequest.data();

                        BinaryContent newBinaryContent = new BinaryContent(fileName, contentType, bytes);
                        BinaryContent binaryContent = binaryContentRepository.saveBinaryContent(newBinaryContent);

                        newMessage.getAttachedFileIds().add(binaryContent.getId());
                        messageRepository.saveMessage(newMessage);

                        return binaryContent.getId();
                    })
                    .toList();

            messageRepository.saveMessage(newMessage);
            return newMessage;
        }
        return null;
    }

    // 채널 메시지 조회 (findAll 수정 버전)
    @Override
    public List<Message> findallByChannelId(UUID channelId, UUID userId, String password) {

        User user = userRepository.findUserById(userId)
                .orElseThrow(() -> new NoSuchElementException("MessageService: 유저가 존재하지 않습니다."));

        Channel channel = channelRepository.findChannelUsingId(channelId)
                .orElseThrow(() -> new NoSuchElementException("MessageService: 채널이 존재하지 않습니다."));


        if (channel.getLock() == ChannelType.PRIVATE) {
            if (isCorrectPassword(channel, password)){
                if (isParticipant(channel, userId)){

                    logger.info("MessageService: 채널에 입장하셨습니다.");

                    return messageRepository.findMessageByChannel(channelId).stream()
                            .toList();
                }
                return null;
            }
            return null;
        }

        logger.info("MessageService: 채널에 입장하셨습니다.");

        return messageRepository.findMessageByChannel(channelId).stream()
                .toList();
    }

    // 메시지 아이디 이용한 조회
    @Override
    public Message getMessageById(UUID channelId, UUID userId, String password, UUID messageId) {

        User user = userRepository.findUserById(userId)
                .orElseThrow(() -> new NoSuchElementException("MessageService: 유저가 존재하지 않습니다."));

        Channel channel = channelRepository.findChannelUsingId(channelId)
                .orElseThrow(() -> new NoSuchElementException("MessageService: 채널이 존재하지 않습니다."));


        return messageRepository.findMessageById(messageId)
                .orElseThrow(() -> new NoSuchElementException("MessageService: 메시지가 존재하지 않습니다."));
    }

    // 발송자를 이용해서 조회
    @Override
    public List<Message> userMessage(UUID senderId, String password) {

        User user = userRepository.findUserById(senderId)
                .orElseThrow(() -> new NoSuchElementException("MessageService: 유저가 존재하지 않습니다."));

        List<Channel> foundChannel = channelRepository.findAllChannels().stream()
                .filter(channel -> channel.getJoiningUsers().contains(senderId))
                .toList();

        for(Channel channel : foundChannel){
            if (isChannelExist(channel) && isChannelLock(channel, password)) {
                logger.info("MessageService: 확인되었습니다.");
            }
        }

        return messageRepository.userMessage(senderId).stream()
            .toList();
    }

    // 메시지 수정
    @Override
    public boolean updateMessage(MessageUpdateRequest request) {

        User user = userRepository.findUserById(request.senderId())
                .orElseThrow(() -> new NoSuchElementException("MessageService: 유저가 존재하지 않습니다."));

        Channel channel = channelRepository.findChannelUsingId(request.channelId())
                .orElseThrow(() -> new NoSuchElementException("MessageService: 채널이 존재하지 않습니다."));

        Message message = messageRepository.findMessageById(request.messageId())
                .orElseThrow(() -> new NoSuchElementException("MessageService: 메시지가 존재하지 않습니다."));


        if (isSender(message, request.senderId())){
            if (channel.getLock() == ChannelType.PRIVATE) {
                if (isCorrectPassword(channel, request.password())) {
                    if (isParticipant(channel, request.senderId())) {

                        logger.info("MessageService: 채널에 입장하셨습니다.");
                        message.updateMessageContent(request.newMessageContent());
                        messageRepository.saveMessage(message);

                        logger.info("MessageService: 메시지가 수정되었습니다.");
                        return true;
                    }
                    return false;
                }
                return false;
            }
            logger.info("MessageService: 채널에 입장하셨습니다.");
            message.updateMessageContent(request.newMessageContent());
            messageRepository.saveMessage(message);

            logger.info("MessageService: 메시지가 수정되었습니다.");
            return true;
        }
        return false;
    }

    // 메시지 삭제
    @Override
    public void deletedMessage(UUID messageId, UUID senderId, String password) {

        User user = userRepository.findUserById(senderId)
                .orElseThrow(() -> new NoSuchElementException("MessageService: 유저가 존재하지 않습니다."));

        Message message = messageRepository.findMessageById(messageId)
                .orElseThrow(() -> new NoSuchElementException("MessageService: 메시지가 존재하지 않습니다."));


        if (isMessageExist(message)) {
            Channel channel = channelRepository.findChannelUsingId(message.getChannelId())
                    .orElseThrow(() -> new NoSuchElementException("MessageService: 체널이 존재하지 않습니다."));

            if (isUserExist(user) &&
                    isChannelLock(channel, password) &&
                    isParticipant(channel, senderId) &&
                    isSender(message, senderId)
            ) {

                logger.info("MessageService: 확인되었습니다.");

                // 연관 첨부파일 삭제
                message.getAttachedFileIds()
                        .forEach(binaryContentRepository::deleteById);

                messageRepository.deletedMessage(messageId);

            }
        }
    }


    /* 자주 쓰는 조건문 정리 */

    //유저 존재 검사
    private boolean isUserExist(User user){
        if (user == null){
            System.out.println("해당하는 유저가 존재하지 않습니다.");
            return false;
        }
        return true;
    }

    //채널 존재 검사
    private boolean isChannelExist(Channel channel){
        if (channel == null){
            System.out.println("해당하는 채널이 존재하지 않습니다.");
            return false;
        }
        return true;
    }

    //참여자 여부 검사
    private boolean isParticipant(Channel channel, UUID senderId){
        if(!channel.getJoiningUsers().contains(senderId)){
            System.out.println("해당 채널에 참여하고 있지 않습니다.");
            return false;
        }
        return true;
    }

    //채널 비밀번호 대조
    private boolean isChannelLock(Channel channel, String password){
        if (channel.getLock() == ChannelType.PRIVATE) {
            System.out.println("비공개 채널입니다. 비밀번호를 확인하고 있습니다..");
            if (!channel.getPassword().equals(password)) {
                System.out.println("비밀번호가 일치하지 않습니다.");
                return false;
            }
        }
        return true;
    }

    //채널 비밀번호 대조
    private boolean isCorrectPassword(Channel channel, String password){
        System.out.println("비공개 채널입니다. 비밀번호를 확인하고 있습니다..");
        if (!channel.getPassword().equals(password)) {
            System.out.println("비밀번호가 일치하지 않습니다.");
            return false;
        }
        return true;
    }

    //메시지 존재 검사
    private boolean isMessageExist(Message message){
        if(message == null){
            System.out.println("해당하는 메시지가 없습니다.");
            return false;
        }
        return true;
    }

    //메시지 발송자 대조
    private boolean isSender(Message message, UUID senderId){
        if (!message.getSenderId().equals(senderId)){
            System.out.println("타인이 보낸 메시지입니다.");
            return false;
        }
        return true;
    }

}

