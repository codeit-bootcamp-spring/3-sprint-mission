package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

@Entity(name = "channel")
@Table(name = "tbl_channels")
@NoArgsConstructor
@Getter
@DynamicUpdate
public class Channel extends BaseUpdatableEntity {

    @Column(name = "type")
    private ChannelType type;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    public Channel(ChannelType type, String name, String description) {
        super.setId(UUID.randomUUID());
        super.setCreatedAt(Instant.now());
        //
        this.type = type;
        this.name = name;
        this.description = description;
    }

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
            super.setUpdatedAt(Instant.now());
        }
    }
}
