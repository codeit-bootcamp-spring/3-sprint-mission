package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.common.Period;
import com.sprint.mission.discodeit.entity.dto.channel.ChannelCreatePrivateDto;
import com.sprint.mission.discodeit.entity.dto.channel.ChannelType;
import lombok.Getter;

import java.io.Serializable;
import java.util.*;

@Getter
public class Channel extends Period implements Serializable {
    private static final long serialVersionUID = 1L;

    private User channelAdmin;
    private String name;
    private String description;
    private Set<User> members;
    private List<Message> messages;
    private ChannelType type;

    public Channel() {}

    public Channel(User user, String name, String description) {
        super();
        this.channelAdmin = user;
        this.name = name;
        this.description = description;
        this.type = ChannelType.PUBLIC;
        this.members = new HashSet<>();
        this.messages = new ArrayList<>();
        user.getChannels().add(this);
    }

    public Channel(ChannelCreatePrivateDto channelCreatePrivateDto) {
        super();
        this.channelAdmin = channelCreatePrivateDto.getAdmin();
        this.members = new HashSet<>();
        this.messages = new ArrayList<>();
        this.type = ChannelType.PRIVATE;
    }

    public void addMember(User user) {
        if (this.members.add(user)) {
            user.getChannels().add(this);
        }
    }

    public void removeMember(User user) {
        this.members.remove(user);
    }

    public UUID getId() {
        return super.getId();
    }

    public void setChannelAdmin(User channelAdmin) {
        this.channelAdmin = channelAdmin;
        update();
    }

    public void setName(String name) {
        this.name = name;
        update();
    }

    public void setDescription(String description) {
        this.description = description;
        update();
    }

    @Override
    public String toString() {
        return "Channel{" +
                "channelAdmin=" + channelAdmin +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", members=" + members +
                ", messages=" + messages +
                ", type=" + type +
                "} " + super.toString();
    }
}
