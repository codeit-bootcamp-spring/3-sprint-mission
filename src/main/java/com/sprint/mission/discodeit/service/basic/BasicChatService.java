package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.ChatService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.UUID;

public class BasicChatService implements ChatService {
    private final ChannelService channelService;
    private final MessageService messageService;
    private final UserService userService;

    public BasicChatService(ChannelService channelService, MessageService messageService, UserService userService) {
        this.channelService = channelService;
        this.messageService = messageService;
        this.userService = userService;
    }

    /**
     * 유저가 주어진 채널에 입장되도록 처리하는 메서드
     *
     * @param userId 입장할 유저ID
     * @param channelName 입장할 채널명
     * @return 입장 처리된 채널
     * @throws IllegalStateException 유저가 이미 해당 채널에 입장해있는 경우
     */
    @Override
    public Channel enterChannel(String userId, String channelName) {
        // 채널/유저 유효성 체크
        Channel ch = channelService.getChannelByChannelName(channelName);
        User targetUser = userService.getUserByUserId(userId);
        // 유저가 이미 해당 채널에 입장해있는 경우
        if (targetUser.getJoinChannelList().contains(ch)) {
            throw new IllegalStateException("이미 해당 채널에 입장해있습니다.");
        }
        //유저의 참여중인 채널리스트에 채널 추가, 채널의 유저리스트에 유저 추가
        targetUser.updateJoinChannelList(ch);
        ch.updateJoinUserList(targetUser);
        // 파일에 저장
        channelService.saveChannels();
        userService.saveUsers();
        return ch;
    }

    /**
     * 유저가 주어진 채널에서 퇴장하도록 처리하는 메서드
     *
     * @param user 퇴장할 유저
     * @param joinChannel 퇴장할 채널
     * @return 퇴장 처리된 채널
     * @throws IllegalStateException 유저가 해당 채널에 없는 경우
     * @throws IllegalArgumentException 유저가 탈퇴한 상태인 경우
     */
    @Override
    public Channel leaveChannel(User user, Channel joinChannel) {
        // 채널/유저 유효성 체크
        Channel ch = channelService.getChannelById(joinChannel.getId());
        User joinUser = userService.getUserById(user.getId());

        if (!ch.getJoinUserList().contains(joinUser)) {
            throw new IllegalStateException("채널에 해당 유저가 없으므로 퇴장이 불가합니다.");
        } else if (!joinUser.getIsActive()) {
            throw new IllegalArgumentException("탈퇴한 회원이므로 퇴장이 불가합니다.");
        }
        // 유저의 참여중인 채널리스트에서 채널 제거, 채널의 유저리스트에서 유저 제거
        joinUser.deleteJoinChannelList(joinChannel);
        ch.deleteJoinUserList(joinUser);
        // 파일에 저장
        channelService.saveChannels();
        userService.saveUsers();
        return ch;
    }

    /**
     * 메시지를 전송처리 하는 메서드
     *
     * @param sendChannel 전송하는 채널
     * @param sendUser 전송하는 유저
     * @param msgContent 전송하는 메시지 내용
     * @return 전송된 메시지
     */
    @Override
    public Message sendMessage(Channel sendChannel, User sendUser, String msgContent) {
        Message sendMessage = messageService.createMessage(sendChannel, sendUser, msgContent);
        // 채널의 메시지리스트에 메시지 추가
        sendChannel.updateMessageList(sendMessage);
        // 파일에 저장
        channelService.saveChannels();
        return sendMessage;
    }

    /**
     * 유저의 채널리스트로부터 채널을 삭제처리하는 메서드
     *
     * @param id 삭제처리할 채널 id
     * @return 삭제된 채널
     */
    @Override
    public Channel deleteChannelFromUsers(UUID id) {
        Channel deletedChannel = channelService.deleteChannel(id);
        // 유저의 채널리스트에서 채널 삭제
        for (User user : deletedChannel.getJoinUserList()) {
            user.deleteJoinChannelList(deletedChannel);
        }
        // 파일에 저장
        userService.saveUsers();
        return deletedChannel;
    }

    /**
     * 채널의 메시지리스트로부터 메시지를 삭제처리하는 메서드
     *
     * @param id 삭제처리할 메시지 id
     * @return 삭제된 메시지
     */
    @Override
    public void deleteMessageFromChannel(UUID id) {
        Message deleteMessage = messageService.deleteMessage(id);
        // 채널에 삭제된 메시지가 포함된 데이터 저장
        channelService.saveChannels();
    }
}
