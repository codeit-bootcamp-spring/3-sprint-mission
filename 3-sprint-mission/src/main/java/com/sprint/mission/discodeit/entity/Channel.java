package com.sprint.mission.discodeit.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Channel extends BaseEntity {

    private String channelName;
    private String channelDescription;
    private boolean isPrivate; //false면 public, true면 private로 설정
    private User creator;
    private int memberCount;
    private List<User> memberList;

    public Channel(String channelName, String channelDescription, User creator) {
        super();
        this.channelName = channelName;
        this.channelDescription = channelDescription;
        this.isPrivate = false; // 공개 여부를 설정하지 않을 시, default는 공개(false)로 설정
        this.creator = creator;
        this.memberCount = 1; //처음 생성하면 생성자 1명만 채널에 참여되어있으므로 1로 초기화
        this.memberList = new ArrayList<>();
        this.memberList.add(creator);

    }

    public Channel(String channelName, String channelDescription,
                   boolean isPrivate, User creator) {
        super();
        this.channelName = channelName;
        this.channelDescription = channelDescription;
        this.isPrivate = isPrivate;
        this.creator = creator;
        this.memberCount = 1; //처음 생성하면 생성자 1명만 채널에 참여되어있으므로 1로 초기화
        this.memberList = new ArrayList<>();
        this.memberList.add(creator);
    }

    public String getChannelName() {
        return channelName;
    }

    public String getChannelDescription() {
        return channelDescription;
    }

    public boolean getIsPrivate() {
        return isPrivate;
    }

    public User getCreator() {
        return creator;
    }

    public int getMemberCount() {
        return memberCount;
    }

    public void update(String channelName, String channelDescription,
                       boolean isPrivate) {
        this.channelName = channelName;
        this.channelDescription = channelDescription;
        this.isPrivate = isPrivate;
        setUpdatedAt();
    }

    @Override
    public String toString() {
        return "Channel{" +
                "channelName='" + channelName + '\'' +
                ", channelDescription='" + channelDescription + '\'' +
                ", isPrivate=" + isPrivate +
                ", creator=" + creator +
                ", memberCount=" + memberCount +
                ", memberList=" + memberList +
                '}';
    }
}
