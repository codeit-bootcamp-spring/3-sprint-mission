package com.sprint.mission.discodeit.entity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class Message {
    // 필드 정의
    private final UUID messageId;
    private final long createdAt;
    private long updatedAt;
    private User fromUser;                        // 전송할 대상(작성자)
    private Channel toChannel;                  // 대상 채팅방
    private String messageContent;            // 메세지 내용
    private String messageType;             // 메세지 타입(이모지 / 텍스트 등)

    // 생성자
    public Message(String messageContent, String messageType, User fromUser, Channel toChannel, long updatedAt) {
        this.messageId = UUID.randomUUID();
        this.createdAt = System.currentTimeMillis();
        this.messageContent = messageContent;
        this.messageType = messageType;
        this.fromUser = fromUser;                                                   // 보낼 대상
        this.toChannel = toChannel;                                             // 장소
        this.updatedAt = this.createdAt;
    }

    // Getter

    public UUID getMessageId() {
        return messageId;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    public User getFromUser() {
        return fromUser;
    }

    public Channel getToChannel() {
        return toChannel;
    }

    public String getMessageContent() {
        return messageContent;
    }

    public String getMessageType() {
        return messageType;
    }

    // Setter


    public void setFromUser(User fromUser) {
        this.fromUser = fromUser;
    }

    public void setToChannel(Channel toChannel) {
        this.toChannel = toChannel;
    }

    public void setMessageContent(String messageContent) {
        this.messageContent = messageContent;
        this.updatedAt = System.currentTimeMillis();
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
        this.updatedAt = System.currentTimeMillis();
    }

    // toString()

    @Override
    public String toString() {
        return "Message{" +
                "messageId=" + messageId +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", fromUser=" + fromUser +
                ", toChannel=" + toChannel +
                ", messageContent='" + messageContent + '\'' +
                ", messageType='" + messageType + '\'' +
                '}';
    }


    // 타임스탬프( 생성 : Message Create TimeStamp )
    public String getFormattedCreatedAt() {
        Date dateC3 = new Date(this.createdAt);
        SimpleDateFormat mct = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return mct.format(dateC3);
    }

    // 수정 ( Message Update TimeStamp )
    public String getFormattedUpdatedAt() {
        Date dateU3 = new Date(this.updatedAt);
        SimpleDateFormat mut = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return mut.format(dateU3);
    }
}
