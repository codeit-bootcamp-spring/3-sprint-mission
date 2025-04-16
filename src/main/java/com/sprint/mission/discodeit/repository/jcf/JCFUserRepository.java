package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * packageName    : com.sprint.mission.discodeit.repository.jcf
 * fileName       : JCFUserRepository
 * author         : doungukkim
 * date           : 2025. 4. 15.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2025. 4. 15.        doungukkim       최초 생성
 */
public class JCFUserRepository implements UserRepository {

    private final List<User> data = new ArrayList<>();

//    -------------------interface-------------------

//    UUID saveUser(String username);
//    User findUserById(UUID userId);
//    List<User> findAllUsers();
//    void updateUsernameByIdAndName(UUID userId, String newName);
//    void deleteUserById(UUID userId);
//    void addChannelInUserByIdAndChannelId(UUID userId, UUID channelId);
//    List<UUID> findChannelIdsInId(UUID userId);
//    void deleteChannelIdInUser(UUID channelId, UUID userId);

//    -------------------------------------------------

    @Override
    public UUID saveUser(String username) {
        User user = new User(username);
        data.add(user);
        return user.getId();
    }
    @Override
    public User findUserById(UUID userId) {
        return data.stream().filter(user -> user.getId().equals(userId)).findAny().orElse(null);
    }

    @Override
    public List<User> findAllUsers() {
        return data;
    }


    @Override
    public void updateUsernameByIdAndName(UUID userId, String newName) {
        for (User user : data) {
            if (user.getId().equals(userId)) {
                user.setUsername(newName);
            }
        }
    }
    @Override
    public void deleteUserById(UUID userId) {
        for (int i = 0; i < data.size(); i++) {
            if (data.get(i).getId().equals(userId)) {
                data.remove(i);
                break;
            }
        }
    }
    @Override
    public void addChannelInUserByIdAndChannelId(UUID userId, UUID channelId) {
        for (User user : data) {
            if (user.getId().equals(userId)) {
                if(user.getChannelIds()!=null){
                    List<UUID> channelIds = user.getChannelIds();
                    // 새 메세지 추가
                    channelIds.add(channelId);
                    user.setChannelIds(channelIds);
                }
            }
        }

    }
    @Override
    public List<UUID> findChannelIdsInId(UUID userId) {
        for (User user : data) {
            if (user.getId().equals(userId)) {
                List<UUID> channelIds = user.getChannelIds();
                return channelIds;
            }
        }
        return null;
    }
    @Override
    public void deleteChannelIdInUser(UUID channelId, UUID userId) {
        for (User user : data) {
            if (user.getId().equals(userId)) {
                user.getChannelIds().removeIf(id -> id.equals(channelId));
            }
        }
    }

}
