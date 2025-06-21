package com.sprint.mission.discodeit.dto.mapper.mapstruct;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.dto.mapper.mapstruct.config.CommonMapperConfig;
import com.sprint.mission.discodeit.entity.Message;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Message Entity ↔ MessageDto 매핑
 * 
 * 복잡한 매핑:
 * - channelId: Channel.id로 매핑
 * - author: UserMapper 사용하여 전체 UserDto로 매핑
 * - attachments: MessageAttachment → BinaryContent → BinaryContentDto 변환
 */
@Mapper(config = CommonMapperConfig.class, uses = { UserMapper.class, BinaryContentMapper.class })
public interface MessageMapper {

  Logger log = LoggerFactory.getLogger(MessageMapper.class);

  /**
   * Message Entity → MessageDto 변환
   * 
   * - channelId: Channel의 ID로 매핑
   * - author: UserMapper가 자동으로 전체 UserDto로 변환
   * - attachments: 안전한 첨부파일 매핑 메서드 사용
   */
  @Mapping(target = "channelId", source = "channel.id")
  @Mapping(target = "attachments", expression = "java(safeMapAttachments(message))")
  MessageDto toDto(Message message);

  /**
   * Message Entity 리스트 → MessageDto 리스트 변환
   */
  List<MessageDto> toDtoList(List<Message> messages);

  /**
   * 안전한 첨부파일 매핑
   * MessageAttachment → BinaryContent → BinaryContentDto 변환
   * 예외 발생 시 빈 리스트 반환
   */
  default List<BinaryContentDto> safeMapAttachments(Message message) {
    try {
      if (message.getMessageAttachments() != null) {
        return message.getMessageAttachments().stream()
            .map(messageAttachment -> {
              var attachment = messageAttachment.getAttachment();
              return new BinaryContentDto(
                  attachment.getId(),
                  attachment.getFileName(),
                  attachment.getSize(),
                  attachment.getContentType());
            })
            .collect(Collectors.toList());
      }
    } catch (Exception e) {
      log.warn("첨부파일 매핑 실패 - 메시지 ID: {}, 오류: {}", message.getId(), e.getMessage());
    }
    return Collections.emptyList();
  }
}