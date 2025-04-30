package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serializable;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.UUID;

public class BinaryContent implements Serializable {
    @Getter
    private static final Long serialVersionUID = 1L;
    @Getter
    private final UUID id;
    @Getter
    private final Instant createdAt;
    //
    @Getter
    private final byte[] byteArray;


    public BinaryContent(byte[] byteArray) {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        //
        this.byteArray = byteArray;
    }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                .withZone(ZoneId.systemDefault());

        String createdAtFormatted = formatter.format(createdAt);

        return "️ BinaryContent {\n" +
                "  id         = " + id + "\n" +
                "  createdAt  = " + createdAtFormatted + "\n" +
                "}";
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof BinaryContent))
            return false;
        BinaryContent other = (BinaryContent) o;

        return (this.id == null && other.id == null)
                || (this.id != null && this.id.equals(other.id));

    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.id);
    }

}
