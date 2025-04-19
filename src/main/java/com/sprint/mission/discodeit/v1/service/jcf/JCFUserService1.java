package com.sprint.mission.discodeit.v1.service.jcf;

import com.sprint.mission.discodeit.v1.entity.User1;
import com.sprint.mission.discodeit.v1.repository.jcf.JCFUserRepository1;
import com.sprint.mission.discodeit.v1.service.ChannelService1;
import com.sprint.mission.discodeit.v1.service.UserService1;

import java.util.List;
import java.util.UUID;

/**
 * packageName    : com.sprint.mission.discodeit.service.jcf
 * fileName       : JCFUserService
 * author         : doungukkim
 * date           : 2025. 4. 3.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2025. 4. 3.        doungukkim       최초 생성
 */

public class JCFUserService1 implements UserService1 {


    private final ChannelService1 channelService;
    private final JCFUserRepository1 jcfUserRepository = new JCFUserRepository1();

    public JCFUserService1(ChannelService1 channelService) {
        this.channelService = channelService;
    }


    // 등록
    @Override
    public UUID registerUser(String username) {
        return jcfUserRepository.saveUser(username);

    }

    // 단건 조회
    @Override
    public User1 findUserById(UUID userId) {
        return jcfUserRepository.findUserById(userId);
    }


    // 다건 조회
    @Override
    public List<User1> findAllUsers() {
        return jcfUserRepository.findAllUsers();
    }

    // 수정
    @Override
    public void updateUsername(UUID userId, String newName) {
        jcfUserRepository.updateUsernameByIdAndName(userId, newName);
    }

//    삭제
    @Override
    public void deleteUser(UUID userId) {
        jcfUserRepository.deleteUserById(userId);

    }

    @Override
    public void addChannelInUser(UUID userId, UUID channelId) {
        jcfUserRepository.addChannelInUserByIdAndChannelId(userId, channelId);

    }

    @Override
    public List<UUID> findChannelIdsInId(UUID userId) {

        List<UUID> channelIdsInId = jcfUserRepository.findChannelIdsInId(userId);

        if (channelIdsInId == null) {
            return null;
        }
        return channelIdsInId;


    }

    @Override
    public void removeChannelIdInUsers(UUID channelId) {
        List<UUID> usersIds = channelService.findChannelById(channelId).getUsersIds();
        for (UUID id : usersIds) {
            jcfUserRepository.deleteChannelIdInUser(channelId, id);
        }
    }
}
