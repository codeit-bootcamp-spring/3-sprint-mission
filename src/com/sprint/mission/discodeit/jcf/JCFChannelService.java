package com.sprint.mission.discodeit.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;

import java.util.ArrayList;
import java.util.List;

public class JCFChannelService {
    // 초기 채널 입력
    private final List<Channel> channels = new ArrayList<>();
    public JCFChannelService(){
        channels.add(new Channel("ch01","default Channel","관리자",1744036548250L,1744036548250L));
    }
    public Channel addChannel(String channelName, String channelDesc, User user){
        long now = System.currentTimeMillis();
        Channel channel = new Channel(channelName,channelDesc,user.getName(),now,now);
        channels.add(channel);
        System.out.print("\n ▶ [새로운 채널 생성완료]");
        System.out.println(" ▶ 채널명 : " + channel.getChannelName() + "     ▶ 생성시간 : " + channel.getCreatedAt());
        return channel;
    };
    void UpdateChannel(String channelName, String channelDescription){};
    void deleteChannel(Channel channel){};

    public void printAllChannels(){
        System.out.println("<< 생성된 전체 채널 >>");
        System.out.println("채널명         | 개설자 | 채널설명(개설시간)");
        channels.forEach(chan -> System.out.println(chan.getChannelName()+"  |  "+chan.getChannelCreater()+"  |  "+chan.getChannelDescription()+"  ("+chan.getCreatedAt() + ")"));
        System.out.println("");
    };
    //List<Channel> getAllChannels(){};
    public Channel findChannelByName(String name){
        for (Channel channel : channels) {
            if (channel.getChannelName().equals(name)) {
                return channel; // 일치하는 name 발견시 user 리턴
            }
        }
        return null; // 없으면 null
    };
}
