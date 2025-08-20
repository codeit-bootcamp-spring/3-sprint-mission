package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;

@Entity
@Table(name = "channels")
@Getter
public class Channel extends BaseUpdatableEntity {

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private ChannelType type;

    @Column(name = "name", length = 100)
    private String name;

    @Column(name = "description", length = 500)
    private String description;

    @OneToMany(
        mappedBy = "channel",
        cascade = CascadeType.ALL,
        orphanRemoval = true
    )
    private List<Message> messages = new ArrayList<>();

    @OneToMany(
        mappedBy = "channel",
        cascade = CascadeType.ALL,
        orphanRemoval = true
    )
    private List<ReadStatus> readStatuses = new ArrayList<>();

    public Channel(ChannelType type, String name, String description) {
        this.type = type;
        this.name = name;
        this.description = description;
    }

    public Channel() { }

    public void update(String newName, String newDescription) {
        boolean anyValueUpdated = false;
        if (newName != null && !newName.equals(this.name)) {
            this.name = newName;
            anyValueUpdated = true;
        }
        if (newDescription != null && !newDescription.equals(this.description)) {
            this.description = newDescription;
            anyValueUpdated = true;
        }

        if (anyValueUpdated) {
            setUpdatedAt();
        }
    }

    @Override
    public String toString() {
        return "Channel{" +
            "type=" + type +
            ", name='" + name + '\'' +
            ", description='" + description + '\'' +
            ", readStatuses=" + readStatuses +
            '}';
    }
}
