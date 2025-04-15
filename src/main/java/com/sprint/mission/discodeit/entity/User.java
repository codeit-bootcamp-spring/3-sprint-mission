package com.sprint.mission.discodeit.entity;

import java.io.Serializable;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class User implements Serializable {
    private static final long serialVersionUID = 1L;
    private UUID id;
    private long createdAt;
    private long updatedAt;

    private String name;
    private int age;


    public User(String name, int age) {
        this.name = name;
        this.age = age;
        // for fixed unique id
        this.id = UUID.nameUUIDFromBytes(name.concat(String.valueOf(age)).getBytes());
        this.createdAt = Instant.now().getEpochSecond();
        this.updatedAt = Instant.now().getEpochSecond();

    }

    public UUID getId() {
        return id;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    // 필드를 수정하는 update 함수를 정의하세요.
    public User update(String name) {
        this.updatedAt = Instant.now().getEpochSecond();

        //TODO : to check if values are different before update
        this.name = name;

        return this;
    }

    public User update(int age) {
        this.updatedAt = Instant.now().getEpochSecond();

        //TODO : to check if values are different before update
        this.age = age;

        return this;
    }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                .withZone(ZoneId.systemDefault());

        String createdAtFormatted = formatter.format(Instant.ofEpochSecond(createdAt));
        String updatedAtFormatted = formatter.format(Instant.ofEpochSecond(updatedAt));

        return "🙋‍♂️ User {\n" +
                "  id         = " + id + "\n" +
                "  createdAt  = " + createdAtFormatted + "\n" +
                "  updatedAt  = " + updatedAtFormatted + "\n" +
                "  name       = '" + name + "'\n" +
                "  age        = " + age + "\n" +
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
        boolean ageEquals = this.age == other.age;
        return idEquals && nameEquals && ageEquals;
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
        result = 31 * result + this.age;
        return result;
    }
}
