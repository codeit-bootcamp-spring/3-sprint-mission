package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.Message;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MessageService {

  // 메시지 생성: 채널, 발신자, 내용 필요
  Message createMessage(MessageCreateRequest messageCreateRequest,
      List<BinaryContentCreateRequest> binaryContentCreateRequests);

  // 특정 채널에서 내가 보낸 메시지들 조회
  List<Message> getMessagesBySenderInChannel(UUID channelId, UUID senderId);

  // 특정 채널에서 내가 받은 메시지들 조회 (송신자 ≠ 나)
  List<Message> getMessagesByReceiverInChannel(UUID channelId, UUID receiverId);

  // 전체 채널에서 내가 보낸 메시지들
  List<Message> getAllSentMessages(UUID senderId);

  // 전체 채널에서 내가 받은 메시지들
  List<Message> getAllReceivedMessages(UUID receiverId);

  // 메시지 수정 (내가 보낸 메시지만 가능)
  Message updateMessage(UUID messageId, MessageUpdateRequest request);

  // 메시지 삭제 (내가 보낸 메시지만 가능)
  void deleteMessage(UUID messageId, UUID senderId);


  // 해당 채널 전체 메시지를 요청자가 조회
  //List<Message> getAllMessagesInChannel(UUID channelId, UUID requesterId);
  // 특정 Channel의 Message 목록을 조회
  List<Message> findAllByChannelId(UUID channelId);

  Optional<Message> getMessageById(UUID messageId);

  String formatMessage(Message message);
}
