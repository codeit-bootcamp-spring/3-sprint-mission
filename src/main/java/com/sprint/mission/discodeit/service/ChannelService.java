package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;
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

     // userid로 만들 수 있게 수정 -> 이걸 수정하면 파라미터로 타이틀을 따로 받아야함
    UUID createChannel(List<User> channelUsers);

    List<Channel> findChannelsById(UUID id);

    List<Channel> findAllChannel();

    void updateChannelName(UUID id, String title);

    void deleteChannel(UUID id);



//    [ ] 등록
//    [ ] 조회(단건, 다건)
//    [ ] 수정
//    [ ] 수정된 데이터 조회
//    [ ] 삭제
//    [ ] 조회를 통해 삭제되었는지 확인
}
