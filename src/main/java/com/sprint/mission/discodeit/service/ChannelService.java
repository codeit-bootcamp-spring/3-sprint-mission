package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.jcf.JCFChannelService.ChannelNotFoundException;
import com.sprint.mission.discodeit.service.jcf.JCFChannelService.ParticipantAlreadyExistsException;
import com.sprint.mission.discodeit.service.jcf.JCFChannelService.ParticipantNotFoundException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ChannelService {

  /**
   * 새로운 채널을 생성한다
   *
   * @param creator 채널 생성자
   * @param name    채널명
   * @return 생성된 채널 객체
   */
  Channel createChannel(User creator, String name);

  /**
   * ID로 채널을 조회한다
   *
   * @param id 채널 ID
   * @return 조회된 채널 객체
   */
  Optional<Channel> getChannelById(UUID id);

  /**
   * 채널을 검색한다
   *
   * @param creatorId 생성자 ID (null인 경우 모든 생성자)
   * @param name      채널명 (null인 경우 모든 이름)
   * @return 검색된 채널 목록
   */
  List<Channel> searchChannels(UUID creatorId, String name);

  /**
   * 사용자가 참여 중인 모든 채널을 조회한다
   *
   * @param userId 사용자 ID
   * @return 사용자가 참여 중인 채널 목록
   */
  List<Channel> getUserChannels(UUID userId);

  /**
   * 채널명을 업데이트한다
   *
   * @param channelId 채널 ID
   * @param name      새로운 채널명
   * @return 업데이트된 채널 객체
   */
  Optional<Channel> updateChannelName(UUID channelId, String name);

  /**
   * 채널에 참여자를 추가한다
   *
   * @param channelId 채널 ID
   * @param user      추가할 사용자
   * @throws ChannelNotFoundException          존재하지 않는 채널
   * @throws ParticipantAlreadyExistsException 중복 참여 시도 오류
   */
  void addParticipant(UUID channelId, User user)
      throws ChannelNotFoundException, ParticipantAlreadyExistsException;

  /**
   * 채널에서 참여자를 제거한다
   *
   * @param channelId 채널 ID
   * @param userId    제거할 사용자 ID
   * @throws ChannelNotFoundException     존재하지 않는 채널
   * @throws ParticipantNotFoundException 존재하지 않는 참여자
   */
  void removeParticipant(UUID channelId, UUID userId)
      throws ChannelNotFoundException, ParticipantNotFoundException;

  /**
   * 채널을 삭제한다
   *
   * @param channelId 삭제할 채널 ID
   * @return 삭제된 채널 객체
   */
  Optional<Channel> deleteChannel(UUID channelId);
}