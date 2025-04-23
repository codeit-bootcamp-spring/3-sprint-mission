package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entitiy.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicUserService implements UserService {

    UserRepository userRepository;

    @Override
    public void create(User user) {
        userRepository.save(user);
    }

    @Override
    public void readAll() {
        userRepository.read();
    }

    @Override
    public void readById(UUID id) {
        userRepository.readById(id);
    }

    @Override
    public void update(UUID id, User user) {
        userRepository.update(id, user);
    }

    @Override
    public void delete(User user) {
        userRepository.delete(user);
    }
}
