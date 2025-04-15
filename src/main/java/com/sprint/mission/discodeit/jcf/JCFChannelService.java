package com.sprint.mission.discodeit.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;

import java.util.ArrayList;
import java.util.List;

public class JCFChannelService implements ChannelService {
    // 초기 채널 입력
    private final List<Channel> channels = new ArrayList<>();
    public JCFChannelService(){
        channels.add(new Channel("ch01","default Channel","관리자",1744036548250L,1744036548250L));
    }

    // 채널 생성 메서드
    @Override
    public Channel addChannel(String channelName, String channelDesc, String createrName) {
        long now = System.currentTimeMillis();
        Channel channel = new Channel(channelName,channelDesc,createrName,now,now);
        channels.add(channel);
        System.out.print("\n ▶ [새로운 채널 생성완료]");
        System.out.println(" ▶ 채널명 : " + channel.getChannelName() + "     ▶ 생성시간 : " + channel.getCreatedAt());
        return channel;
    };
    // 채널 수정 메서드
    @Override
    public void UpdateChannel(Channel channel, String channelName, String channelDescription){
        long now = System.currentTimeMillis();
        channel.setChannelName(channelName);
        channel.setChannelDescription(channelDescription);
        channel.setUpdatedAt(now);
        System.out.print("\n ▶ [채널 정보 수정 완료]");
        System.out.println(channel);
    };
    // 채널 삭제 메서드
    @Override
    public void deleteChannel(Channel channel){
        channels.remove(channel);
    };
    // 전체채널목록 출력 메서드
    @Override
    public void printAllChannels(){
        System.out.println("<< 생성된 전체 채널 >>");
        System.out.println("채널명         | 개설자 | 채널설명(개설시간)");
        channels.forEach(chan -> System.out.println(chan.getChannelName()+"  |  "+chan.getChannelCreater()+"  |  "+chan.getChannelDescription()+"  ("+chan.getCreatedAt() + ")"));
        System.out.println("");
    }


    @Override // 이름으로 채널 검색 메서드. 채널객체를 return
    public Channel findChannelByName(String name){
        for (Channel channel : channels) {
            if (channel.getChannelName().equals(name)) {
                return channel; // 일치하는 name 발견시 channel 객체를 리턴
            }
        }
        return null; // 없으면 null
    }
}
