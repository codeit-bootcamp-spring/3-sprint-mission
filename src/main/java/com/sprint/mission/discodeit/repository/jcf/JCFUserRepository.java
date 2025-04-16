package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.User;

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
public class JCFUserRepository {

    private final List<User> data = new ArrayList<>();

    public void save(User user) {
        data.add(user);
    }

    public User findUserById(UUID userId) {
        return data.stream().filter(user -> user.getId().equals(userId)).findAny().orElse(null);
    }

    public List<User> findAllUsers() {
        return data;
    }

    public void updateUsername(UUID userId, String newName) {
        for (User user : data) {
            if (user.getId().equals(userId)) {
                user.setUsername(newName);
            }
        }
    }

    public void deleteUserByIndex(UUID userId) {
        for (int i = 0; i < data.size(); i++) {
            if (data.get(i).getId().equals(userId)) {
                data.remove(i);
                break;
            }
        }
    }

    public void addChannelInUser(UUID userId, UUID channelId) {
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

    public List<UUID> findChannelIdsInId(UUID userId) {
        for (User user : data) {
            if (user.getId().equals(userId)) {
                List<UUID> channelIds = user.getChannelIds();
                return channelIds;
            }
        }
        return null;
    }

    public void deleteChannelIdInUser(UUID channelId, UUID userId) {
        for (User user : data) {
            if (user.getId().equals(userId)) {
                user.getChannelIds().removeIf(id -> id.equals(channelId));
            }
        }
    }

}
