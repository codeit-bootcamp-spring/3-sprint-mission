package com.sprint.mission.discodeit.entity;

import java.time.Instant;
import java.util.*;
import java.io.Serial;
import java.io.Serializable;

public class User extends Base implements Serializable {
    private transient String RRN;

    //
    @Serial
    private static final long serialVersionUID = 1L; // private, final 필수x 권장사항
    private int age;
    private String name;
    private String email;


    public User(String RRN, String name, int age, String email) {
        super();

        this.RRN = RRN;
        this.age = age;
        this.name = name;
        this.email = email;
    }


    // Getter Method
    public String getRRN() { return RRN; }

    public int getAge() { return age; }

    public String getName() { return name; }

    public String getEmail() { return email; }


    // Update Method
    public void updateUser(String newName, String newEmail) {
        boolean anyValueUpdated = false;
        if (newName != null && !newName.equals(this.name)) {
            this.name = newName;
            anyValueUpdated = true;
        }

        if (newEmail != null && !newEmail.equals(this.email)) {
            this.email = newEmail;
            anyValueUpdated = true;
        }

        if (anyValueUpdated) {
            super.updateUpdatedAt(Instant.now().getEpochSecond());
        }
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + getId() +
                ", RRN='" + getRRN() + '\'' +
                ", name='" + getName() + '\'' +
                ", age=" + getAge() +
                ", email='" + getEmail() + '\'' +
                ", createdAt='" + getCreatedAt() + '\'' +
                ", updatedAt='" + getUpdatedAt() + '\'' +
                '}';
    }
}
