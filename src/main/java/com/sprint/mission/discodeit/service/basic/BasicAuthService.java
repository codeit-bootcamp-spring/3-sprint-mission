package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BasicAuthService implements AuthService {
    private final UserRepository userRepository;
    private final UserStatusRepository userStatusRepository; //이후에 login 로직에서 사용

    @Override
    public User authenticate(String name, String password) {
        User user = userRepository.loadByName(name);
        if (user == null) {
            throw new IllegalArgumentException("[Auth] 유효하지 않은 사용자입니다. (" + name + ")");
        }

        if (user.getPassword().equals(password)) {
            System.out.println(user);
            return user;
        } else {
            throw new IllegalArgumentException("[Auth] 패스워드가 일치하지 않습니다. (" + name + ")");
        }
    }
}
