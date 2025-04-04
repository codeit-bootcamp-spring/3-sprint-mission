package com.sprint.mission.discodeit.service.jcf;


import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;

import java.util.*;
import java.util.stream.Collectors;

/**
 * packageName    : com.sprint.mission.discodeit.service.jcf
 * fileName       : JCFChannelService
 * author         : doungukkim
 * date           : 2025. 4. 3.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2025. 4. 3.        doungukkim       최초 생성
 */
public class JCFChannelService implements ChannelService {
    private final List<Channel> data;
    private MessageService messageService;

    public JCFChannelService() {
        this.data = new ArrayList<>();
    }

    private void setMessageService(MessageService messageService) {
        this.messageService = messageService;
    }

    @Override
    public UUID createChannel(List<User> channelUsers) {
        Channel channel = new Channel(channelUsers);
        data.add(channel);
        return channel.getId();
    }

    @Override
    public List<Channel> findChannelsById(UUID channelId) {
        return data.stream().filter(channel -> channel.getId().equals(channelId)).collect(Collectors.toList());
    }

    @Override
    public List<Channel> findAllChannel() {
        return data;
    }

    @Override
    public void updateChannelName(UUID id, String title) {

        for (Channel channel : data) {
            if (channel.getId().equals(id)) {
                channel.setTitle(title);
                channel.setUpdatedAt(System.currentTimeMillis());
            }
        }
    }

    @Override
    public void deleteChannel(UUID channelId) {
        for (int i = 0; i < data.size(); i++) {
            if (data.get(i).getId().equals(channelId)) {
                List<Message> messages = data.get(i).getMessages();
                for (Message message : messages) {
                    //업데이트 필요
                }
                data.remove(i);
                break;
            }
        }
    }


//    public void deleteUserFromChannel(UUID id) {
//        //모든 방에서 특정 유저를 삭제 해야함
//        List<UUID> emptyChannel = new ArrayList<>();
//
////        System.out.println("삭제할 UUID: " + id);
////
////        for (Channel channel : data) {
////            for (User user : channel.getChannelUsers()) {
////                System.out.println("채널 " + channel.getId() + " 유저: " + user.getUsername() + ", UUID: " + user.getId());
////            }
////        }
//        // 모든 방 조회
//        for (Channel channel : data) {
//
//
//            List<User> channelUsers = channel.getChannelUsers();
//
//            // 그 안에 유저 있는지 확인
//            // 있으면 유저 삭제
//            List<User> modifiedUsers = channelUsers.stream().filter(u-> !u.getId().equals(id)).collect(Collectors.toList());
////            System.out.println("before: channel.getChannelUsers() = " + channel.getChannelUsers()
////                    .stream()
////                    .map(user -> user.getUsername())
////                    .collect(Collectors.joining(", ")));
//
//            // 방에 유저 있는지 확인
//            if (modifiedUsers.isEmpty()) {
//                // 없으면 emptyChannel에 추가
//                emptyChannel.add(channel.getId());
//            } else {
//                // 있으면 channel 업데이트
//                channel.setChannelUsers(modifiedUsers);
//                // 날짜 업데이트
//                channel.setUpdatedAt(System.currentTimeMillis());
//            }
//
////            System.out.println("after: channel.getChannelUsers() = " + channel.getChannelUsers()
////                    .stream()
////                    .map(user -> user.getUsername())
////                    .collect(Collectors.joining(", ")));
//        }
//
//        // user가 없는 방 삭제
//        for (int i = 0; i < emptyChannel.size(); i++) {
//            UUID channelId = emptyChannel.get(i);
//            deleteChannel(channelId);
//
//
//        }
////        System.out.println(emptyChannel);
//    }

}

