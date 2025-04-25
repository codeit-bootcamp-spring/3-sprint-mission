package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serializable;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class User implements Serializable {
    @Getter
    private static final Long serialVersionUID = 1L;
    //
    @Getter
    private final UUID id;
    @Getter
    private final Instant createdAt;
    @Getter
    private Instant updatedAt;
    //
    @Getter
    private String name;
    @Getter
    private String email;
    @Getter
    private String password;
    //
    @Getter
    private UUID profileId; // BinaryContent의 id

    public User(String name, String email, String password) {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
        //
        this.name = name;
        this.email = email;
        this.password = password;
    }

    public void update(String name, String email, String password, UUID profileId) {
        boolean anyValueUpdated = false;
        if (name != null && !name.equals(this.name)) {
            this.name = name;
            anyValueUpdated = true;
        }
        if (email != null && !email.equals(this.email)) {
            this.email = email;
            anyValueUpdated = true;
        }
        if (password != null && !password.equals(this.password)) {
            this.password = password;
            anyValueUpdated = true;
        }
        if (profileId != null && !profileId.equals(this.profileId)) {
            this.profileId = profileId;
            anyValueUpdated = true;
        }

        if (anyValueUpdated) {
            this.updatedAt = Instant.now();

        }
    }


    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                .withZone(ZoneId.systemDefault());

        String createdAtFormatted = formatter.format(createdAt);
        String updatedAtFormatted = formatter.format(updatedAt);

        return "🙋‍♂️ User {\n" +
                "  id         = " + id + "\n" +
                "  createdAt  = " + createdAtFormatted + "\n" +
                "  updatedAt  = " + updatedAtFormatted + "\n" +
                "  name       = '" + name + "'\n" +
                "}";
    }

    // REF : https://www.baeldung.com/java-equals-hashcode-contracts
    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof User))
            return false;
        User other = (User) o;
        boolean idEquals = (this.id == null && other.id == null)
                || (this.id != null && this.id.equals(other.id));
        boolean nameEquals = (this.name == null && other.name == null)
                || (this.name != null && this.name.equals(other.name));

        return idEquals && nameEquals;
    }

    @Override
    public int hashCode() {
        int result = 17;
        if (this.id != null) {
            result = 31 * result + this.id.hashCode();
        }
        if (this.name != null) {
            result = 31 * result + this.name.hashCode();
        }
        return result;
    }
}
