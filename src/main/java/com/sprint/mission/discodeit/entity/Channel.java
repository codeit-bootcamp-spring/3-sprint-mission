package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tbl_channel")
public class Channel extends BaseUpdatableEntity {
    @Enumerated(EnumType.STRING)
    private ChannelType type;

    private String name;
    private String description;

    public void update(String newName, String newDescription) {
        this.name = newName;
        this.description = newDescription;
    }
}
