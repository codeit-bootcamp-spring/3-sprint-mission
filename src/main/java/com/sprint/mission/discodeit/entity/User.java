package com.sprint.mission.discodeit.entity;


public class User extends Base {
    private int age;
    private String name;
    private String email;

    public User(String name, int age, String email) {
        super();
        this.age = age;
        this.name = name;
        this.email = email;
    }


    // Getter Method

    public int getAge() { return age; }

    public String getName() { return name; }

    public String getEmail() { return email; }


    // Update Method

    public void updateAge(int age) {
        this.age = age;
        super.updateUpdatedAt();
    }

    public void updateName(String name) {
        this.name = name;
        super.updateUpdatedAt();
    }

    public void updateEmail(String email) {
        this.email = email;
        super.updateUpdatedAt();
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + getId() +
                ", name='" + getName() + '\'' +
                ", age=" + getAge() +
                ", email='" + getEmail() + '\'' +
                ", createdAt='" + getCreatedAt() + '\'' +
                ", updatedAt='" + getUpdatedAt() + '\'' +
                '}';
    }
}
