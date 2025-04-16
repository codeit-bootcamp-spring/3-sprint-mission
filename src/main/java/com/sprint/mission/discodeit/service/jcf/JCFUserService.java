package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.jcf.JCFUserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.UserService;

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

public class JCFUserService implements UserService{

//    private final List<User> data;
    private final ChannelService channelService;
    private final JCFUserRepository jcfUserRepository = new JCFUserRepository();

    public JCFUserService(ChannelService channelService) {
//        this.data = new ArrayList<>();
        this.channelService = channelService;
    }


    // 등록
    @Override
    public UUID registerUser(String username) {
        return jcfUserRepository.saveUser(username);

    }

    // 단건 조회
    @Override
    public User findUserById(UUID userId) {
        return jcfUserRepository.findUserById(userId);
    }


    // 다건 조회
    @Override
    public List<User> findAllUsers() {
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
//        for (int i = 0; i < data.size(); i++) {
//            if (data.get(i).getId().equals(userId)) {
//                data.remove(i);
//                break;
//            }
//        }
    }

    @Override
    public void addChannelInUser(UUID userId, UUID channelId) {
        jcfUserRepository.addChannelInUserByIdAndChannelId(userId, channelId);
//        for (User user : data) {
//            if (user.getId().equals(userId)) {
//                if(user.getChannelIds()!=null){
//                    List<UUID> channelIds = user.getChannelIds();
//                    // 새 메세지 추가
////                    jcfUserRepository.addChannelInUser(channelIds, channelId, user);
//                    channelIds.add(channelId);
//                    user.setChannelIds(channelIds);
//                }
//            }
//        }
    }

    @Override
    public List<UUID> findChannelIdsInId(UUID userId) {

        List<UUID> channelIdsInId = jcfUserRepository.findChannelIdsInId(userId);

        if (channelIdsInId == null) {
            return null;
        }
        return channelIdsInId;

//        for (User user : data) {
//            if (user.getId().equals(userId)) {
//                List<UUID> channelIds = user.getChannelIds();
//                return channelIds;
//            }
//        }

    }

    @Override
    public void removeChannelIdInUsers(UUID channelId) {
        List<UUID> usersIds = channelService.findChannelById(channelId).getUsersIds();
        for (UUID id : usersIds) {
            jcfUserRepository.deleteChannelIdInUser(channelId, id);
        }
    }
}
