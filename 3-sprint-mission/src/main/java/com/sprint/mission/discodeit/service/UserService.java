package com.sprint.mission.discodeit.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.sprint.mission.discodeit.entity.User;
import org.springframework.stereotype.Service;

public interface UserService {

    User create(String username, String email, String password, String name) throws IOException;
    User find(UUID id);
    User findByUsername(String username);
    List<User> findByName(String name);
    User findByEmail(String email);
    List<User> findAll();
    void updateName(UUID id, String name) throws IOException, IllegalAccessException;
    void updatePassword(UUID id, String password) throws IOException, IllegalAccessException;
    void updateEmail(UUID id, String email) throws IOException, IllegalAccessException;
    void updateProfile(UUID id, UUID profileId) throws IOException, IllegalAccessException;
    void delete(UUID id) throws IOException, IllegalAccessException;
}