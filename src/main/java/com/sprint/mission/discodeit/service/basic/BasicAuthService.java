package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.Dto.authService.LoginRequest;
import com.sprint.mission.discodeit.Dto.authService.LoginResponse;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.jpa.JpaUserRepository;
import com.sprint.mission.discodeit.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * packageName    : com.sprint.mission.discodeit.service.basic fileName       : BasicAuthService
 * author         : doungukkim date           : 2025. 4. 25. description    :
 * =========================================================== DATE              AUTHOR NOTE
 * ----------------------------------------------------------- 2025. 4. 25.        doungukkim 최초 생성
 */

@Primary
@RequiredArgsConstructor
@Service("basicAuthService")
public class BasicAuthService implements AuthService {

    private final JpaUserRepository userRepository;
//    private final UserRepository userRepository;


    public LoginResponse login(LoginRequest request) {
//
//        String username = request.username();
//        String password = request.password();
//
//        List<User> users = userRepository.findAllUsers();
//        List<User> selectedUser = users.stream().filter(user -> user.getUsername().equals(username)).toList();
//
//        if (selectedUser.isEmpty()) {
//            throw new RuntimeException("User with username " + username + " not found");
////            return ResponseEntity.status(404).body("User with username " + username + " not found");
//        }
//
//        if ((selectedUser.size() == 1) && (selectedUser.get(0).getPassword().equals(password))) {
//
//            LoginResponse loginResponse = new LoginResponse(
//                    selectedUser.get(0).getId(),
//                    selectedUser.get(0).getCreatedAt(),
//                    selectedUser.get(0).getUpdatedAt(),
//                    selectedUser.get(0).getUsername(),
//                    selectedUser.get(0).getEmail(),
//                    selectedUser.get(0).getPassword(),
//                    selectedUser.get(0).getProfileId()
//            );
//            return loginResponse;
//        }
//        if (selectedUser.size() >= 2) {
//            throw new IllegalArgumentException("USERNAME IS NOT UNIQUE I AM IN TROUBLE!!!");
//        }
//        throw new IllegalArgumentException("wrong password");
        return null;
    }

}
