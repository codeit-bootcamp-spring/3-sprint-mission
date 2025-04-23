package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ChannelService {
  // 채널 생성, 조회(단건 , 다건), 수정, 삭제 + 멤버 추가/ 삭제

  // 1. 채널 생성
  Channel createChannel(String channelName, User ownerUser);

  // 2. 단일 채널 조회
  Optional<Channel> getChannelById(UUID channelId);

  // 3. 전체 채널 조회
  List<Channel> getAllChannels();

  // 4. 채널 이름 수정
  void updateChannelName(UUID channelId, String channelName);

  // 채널 소유자 변경 ... 채널 소유자가 탈퇴한다면? 채널도 삭제된다. <- 이 전제로 1) 필요없는 메서드로 진행
  // 2) 채널 소유자가 떠나도 채널이 유지되어야 하는 경우에는 해당 메서드가 필요
  //void updateChannelOwner(UUID channelId, UUID ownerUserId);

  // 5. 채널 삭제(채널 소유자만 삭제 가능)
  void deleteChannel(UUID channelId);

  // 6. 멤버 추가
  void addMember(UUID channelId, UUID userId);

  // 7. 멤버 삭제(일반 유저가 직접 채널 나가기 or 채널소유자 권한의 멤버 강퇴)
  void removeMember(UUID channelId, UUID userId);

  // 8. 채널에 속한 멤버 조회
  List<User> getChannelMembers(UUID channelId);

  // 9. 유저가 만든 모든 채널 삭제
  void deleteChannelsCreatedByUser(UUID userId);

  // 10. 유저가 참여 중인 모든 채널에서 탈퇴
  void removeUserFromAllChannels(UUID userId);
}
