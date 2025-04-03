package com.sprint.mission.discodeit.entity;

public class Channel extends BaseEntity {

    private String channelName;
    private String channelDescription;
    private boolean isPrivate; //false면 public, true면 private로 설정
    private final String channelCreatorId;
    private int memberCount;

    public Channel(String channelName, String channelDescription, String channelCreatorId) {
        super();
        this.channelName = channelName;
        this.channelDescription = channelDescription;
        this.isPrivate = false; // 공개 여부를 설정하지 않을 시, default는 공개(false)로 설정
        this.channelCreatorId = channelCreatorId;
        this.memberCount = 1; //처음 생성하면 생성자 1명만 채널에 참여되어있으므로 1로 초기화
    }

    public Channel(String channelName, String channelDescription,
                   boolean isPrivate, String channelCreatorId) {
        super();
        this.channelName = channelName;
        this.channelDescription = channelDescription;
        this.isPrivate = isPrivate;
        this.channelCreatorId = channelCreatorId;
        this.memberCount = 1; //처음 생성하면 생성자 1명만 채널에 참여되어있으므로 1로 초기화
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

    public String getChannelCreatorId() {
        return channelCreatorId;
    }

    public int getMemberCount() {
        return memberCount;
    }

    public void update(String channelName, String channelDescription,
                       boolean isPrivate, int memberCount) {
        this.channelName = channelName;
        this.channelDescription = channelDescription;
        this.isPrivate = isPrivate;
        this.memberCount = memberCount;
        setUpdatedAt();
    }
}
