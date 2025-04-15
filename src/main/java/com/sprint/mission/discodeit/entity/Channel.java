package com.sprint.mission.discodeit.entity;

import java.time.Instant;
import java.util.*;
import java.util.UUID;

public class Channel extends Base {
    private String channelName;
    private String description;

    public Channel() {}

    public Channel(String channelName, String description) {
        super();
        //
        this.channelName = channelName;
        this.description = description;
    }


    // Gettter Method

    public String getChannelName() {
        return this.channelName;
    }

    public String getDescription() { return this.description; }


    // Update Method

    public void updateChannel(String newChannelName, String newDescription) {
        boolean anyValueUpdated = false;
        if (newChannelName != null && !newChannelName.equals(this.channelName)) {
            this.channelName = newChannelName;
            anyValueUpdated = true;
        }

        if (newDescription != null && newDescription.equals(this.description)) {
            this.description = newDescription;
            anyValueUpdated = true;
        }

        if (anyValueUpdated) {
            super.updateUpdatedAt(Instant.now().getEpochSecond());
        }
    }

    @Override
    public String toString() {
        return "Channel{" +
                "name='" + getChannelName() + '\'' +
                ", channelId='" + getId() + '\'' +
                ", createdAt='" + getCreatedAt() + '\'' +
                ", updatedAt='" + getUpdatedAt() + '\'' +
                '}';
    }
}