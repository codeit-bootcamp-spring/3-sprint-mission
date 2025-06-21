package com.sprint.mission.discodeit.dto.mapper.mapstruct;

import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.mapper.mapstruct.config.CommonMapperConfig;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Channel Entity ↔ ChannelDto 매핑
 * 
 * 복잡한 매핑:
 * - participants: 비공개 채널만 ReadStatus에서 User 정보 추출
 * - lastMessageAt: Message들 중 가장 최신 시간 계산
 */
@Mapper(config = CommonMapperConfig.class, uses = { UserMapper.class })
public interface ChannelMapper {

  Logger log = LoggerFactory.getLogger(ChannelMapper.class);

  /**
   * Channel Entity → ChannelDto 변환
   * 
   * - participants: 안전한 참가자 매핑 메서드 사용
   * - lastMessageAt: 안전한 마지막 메시지 시간 계산 메서드 사용
   */
  @Mapping(target = "participants", expression = "java(safeMapParticipants(channel))")
  @Mapping(target = "lastMessageAt", expression = "java(safeGetLastMessageAt(channel))")
  ChannelDto toDto(Channel channel);

  /**
   * Channel Entity 리스트 → ChannelDto 리스트 변환
   */
  List<ChannelDto> toDtoList(List<Channel> channels);

  /**
   * 안전한 참가자 매핑 (비공개 채널용)
   * 공개 채널은 빈 리스트, 비공개 채널은 ReadStatus에서 User 추출
   */
  default List<UserDto> safeMapParticipants(Channel channel) {
    if (channel.getType().isPublic()) {
      return Collections.emptyList();
    }

    try {
      if (channel.getReadStatuses() != null) {
        return channel.getReadStatuses().stream()
            .map(readStatus -> {
              var user = readStatus.getUser();
              return new UserDto(
                  user.getId(),
                  user.getCreatedAt(),
                  user.getUpdatedAt(),
                  user.getUsername(),
                  user.getEmail(),
                  user.getProfile() != null ? new com.sprint.mission.discodeit.dto.data.BinaryContentDto(
                      user.getProfile().getId(),
                      user.getProfile().getFileName(),
                      user.getProfile().getSize(),
                      user.getProfile().getContentType()) : null,
                  user.getUserStatus() != null ? user.getUserStatus().isOnline() : false);
            })
            .collect(Collectors.toList());
      }
    } catch (Exception e) {
      log.warn("참가자 매핑 실패 - 채널 ID: {}, 오류: {}", channel.getId(), e.getMessage());
    }
    return Collections.emptyList();
  }

  /**
   * 안전한 마지막 메시지 시간 조회
   * 채널의 메시지들 중 가장 최신 시간 반환, 없으면 채널 생성 시간
   */
  default Instant safeGetLastMessageAt(Channel channel) {
    try {
      if (channel.getMessages() != null && !channel.getMessages().isEmpty()) {
        return channel.getMessages().stream()
            .max(Comparator.comparing(Message::getCreatedAt))
            .map(Message::getCreatedAt)
            .orElse(channel.getCreatedAt());
      }
    } catch (Exception e) {
      log.warn("마지막 메시지 시간 조회 실패 - 채널 ID: {}, 오류: {}", channel.getId(), e.getMessage());
    }
    return channel.getCreatedAt();
  }
}