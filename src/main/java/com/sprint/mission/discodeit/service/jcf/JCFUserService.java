package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

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

public class JCFUserService implements UserService {

    private final List<User> data;
    private final ChannelService channelService;

    public JCFUserService(ChannelService channelService) {
        this.data = new ArrayList<>();
        this.channelService = channelService;
    }

    // 등록
    @Override
    public UUID registerUser(String username) {
        User newUser = new User(username);
        data.add(newUser);
        return newUser.getId();
    }

    // 단건 조회
//  try-catch 에 대한 이해가 조금더 필요하신거 같습니다.
//  findUserById 메서드에 존재 안하는 userID를 전달하여 보시고, 의도하신 System.out.println("찾는 유저 없음"); 가 실행 되는지 확인해보시길 권장 드립니다.
//  추가적으로 null을 리턴하는건 해당 메서드를 사용하는 다른 로직에서 NPE가 발생할 리스크가 있습니다.
//  Optional을 활용 하시면 Null 체크를 강제할 수 있습니다.

    @Override
    public User findUserById(UUID userId) {
        return data.stream().filter(user -> user.getId().equals(userId)).findAny().orElse(null);
    }


    // 다건 조회
    @Override
    public List<User> findAllUsers() {
        return data;
    }

    // 수정
    @Override
    public void updateUsername(UUID userId, String newName) {

        for (User user : data) {
            if (user.getId().equals(userId)) {
                user.setUsername(newName);
                user.setUpdatedAt(System.currentTimeMillis());
            }
        }
    }

//    삭제
    @Override
    public void deleteUser(UUID userId) {
        for (int i = 0; i < data.size(); i++) {
            if (data.get(i).getId().equals(userId)) {
                data.remove(i);
                break;
            }
        }
    }

    @Override
    public void addChannelInUser(UUID userId, UUID channelId) {
        for (User user : data) {
            if (user.getId().equals(userId)) {
                if(user.getChannelIds()!=null){
                    List<UUID> channelIds = user.getChannelIds();
                    // 새 메세지 추가
                    channelIds.add(channelId);
                    user.setChannelIds(channelIds);
                    user.setUpdatedAt(System.currentTimeMillis());
                }
            }
        }
    }

    @Override
    public List<UUID> findChannelIdsById(UUID userId) {
        for (User user : data) {
            if (user.getId().equals(userId)) {
                List<UUID> channelIds = user.getChannelIds();
                return channelIds;
            }
        }
        return new ArrayList<>();
    }

}
