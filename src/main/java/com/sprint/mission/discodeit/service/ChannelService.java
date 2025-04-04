package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

/**
 *packageName    : com.sprint.mission.discodeit.service
 * fileName       : ChannelService
 * author         : doungukkim
 * date           : 2025. 4. 3.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2025. 4. 3.        doungukkim       최초 생성
 */public interface ChannelService {


    UUID createChannel(List<User> channelUsers);

    List<Channel> findChannelsById(UUID channelId);

    List<Channel> findAllChannel();

    void updateChannelName(UUID channelId, String title);

    void deleteChannel(UUID channelId);



//    // channel에서 유저 삭제
//    // updatedAt()업데이트
//    void deleteUserFromChannel(UUID userId);



//    [ ] 등록
//    [ ] 조회(단건, 다건)
//    [ ] 수정
//    [ ] 수정된 데이터 조회
//    [ ] 삭제
//    [ ] 조회를 통해 삭제되었는지 확인
}
