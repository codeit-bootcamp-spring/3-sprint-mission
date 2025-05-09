package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.util.*;
import java.util.stream.Collectors;

@Getter
public class Channel extends BaseEntity {

    private String channelName;
    private String channelDescription;
    private boolean isLock; //false면 public, true면 private로 설정
    private User creator;
    private String password;
    private Set<User> memberList;
    private List<Message> messageList;

    public Channel(String channelName, String channelDescription,
                   boolean isLock, User creator) {
        super();
        this.channelName = channelName;
        this.channelDescription = channelDescription;
        this.isLock = false; // 공개 여부를 설정하지 않을 시, default는 공개(false)로 설정
        this.creator = creator;
        this.password = "";
        this.memberList = new LinkedHashSet<User>();
        this.memberList.add(creator);
        this.messageList = new ArrayList<Message>();

    }

    public Channel(String channelName, String channelDescription,
                   boolean isLock, User creator, String password) {
        super();
        this.channelName = channelName;
        this.channelDescription = channelDescription;
        this.isLock = isLock;
        this.creator = creator;
        this.password = password;
        this.memberList = new HashSet<User>();
        this.memberList.add(creator);
        this.messageList = new ArrayList<Message>();
    }

    public void update(String channelName, String channelDescription,
                       boolean isLock, String password) {
        this.channelName = channelName;
        this.channelDescription = channelDescription;
        this.isLock = isLock;
        this.password = password;
        setUpdatedAt();
    }

    @Override
    public String toString() {
        return "[채널] {" + "\"" +
                channelName + "\"" +
                ", " + isLock +
                ", 어드민: [" + creator.getUserName() +
                "], 멤버 수: " + memberList.size() +
                ", 채널 멤버: " + memberList.stream().map(u -> u.getUserName()).collect(Collectors.toList()) +
                '}' + '\n';
    }
}
