package com.sprint.mission.discodeit.entity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

// 채널
public class Channel{
    // 필드 정의
    private final UUID channelId;
    private final long createdAt;
    private long updatedAt;
    private String channelName;                          // 채널명
    private String channelType;                         // 채널 유형
    private String category;                           // 분류
    private final List<User> members;                // 채널에 속한 사용자


    // 생성자
    public Channel(String channelName, String channelType, String category, long updatedAt) {
        this.channelId = UUID.randomUUID();
        this.createdAt = System.currentTimeMillis();
        this.channelName = channelName;
        this.channelType = channelType;
        this.category = category;
        this.updatedAt = this.createdAt;
        this.members = new ArrayList<>();
    }

    // Getter

    public UUID getChannelId() {
        return channelId;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    public String getChannelName() {
        return channelName;
    }

    public String getChannelType() {
        return channelType;
    }

    public String getCategory() {
        return category;
    }

    public List<User> getMembers() {
        return members;
    }


    // Setter

    public void setChannelName(String channelName) {
        this.channelName = channelName;
        this.updatedAt = System.currentTimeMillis();
    }

    public void setChannelType(String channelType) {
        this.channelType = channelType;
        this.updatedAt = System.currentTimeMillis();
    }

    public void setCategory(String category) {
        this.category = category;
        this.updatedAt = System.currentTimeMillis();
    }

    // toString()


    @Override
    public String toString() {
        return "Channel{" +
                "channelId=" + channelId +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", channelName='" + channelName + '\'' +
                ", channelType='" + channelType + '\'' +
                ", category='" + category + '\'' +
                ", members=" + members +
                '}';
    }


    // 타임스탬프( 생성 : Channel Create TimeStamp )
    public String getFormattedCreatedAt() {
        Date dateC2 = new Date(this.createdAt);
        SimpleDateFormat cct = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return cct.format(dateC2);
    }

    // 수정 ( Channel Update TimeStamp )
    public String getFormattedUpdatedAt() {
        Date dateU2 = new Date(this.updatedAt);
        SimpleDateFormat cut = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return cut.format(dateU2);
    }


    // 인원 추가 메서드
    public void addMember(User user) {
        if (!members.contains(user)) {
            members.add(user);
        }
    }

    // 인원 제거 메서드
    public void removeMember(User user) {
        if (members.contains(user)) {
            members.remove(user);
        }
    }
}
