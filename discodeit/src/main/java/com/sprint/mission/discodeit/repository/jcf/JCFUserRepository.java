//package com.sprint.mission.discodeit.repository.jcf;
//
//import com.sprint.mission.discodeit.entity.User;
//import com.sprint.mission.discodeit.repository.UserRepository;
//import org.springframework.stereotype.Repository;
//
//import java.util.*;
//
//
//
//@Repository
//public class JCFUserRepository implements UserRepository {
//    private final Map<UUID, User> userData;
//
//    public JCFUserRepository() {
//        this.userData = new HashMap<>();
//    }
//
//    @Override
//    public User save(User user) {
//        this.userData.put(user.getId(), user);
//        return user;
//    }
//
//    @Override
//    public Optional<User> findById(UUID id) {
//        return Optional.ofNullable(this.userData.get(id));
//    }
//
//    @Override
//    public Optional<User> findByUsername(String username) {
//        return this.userData.values().stream()
//                .filter(user -> user.getUsername().equals(username))
//                .findFirst();
//    }
//
//    @Override
//    public List<User> findAll() {
//        return this.userData.values().stream().toList();
//    }
//
//    @Override
//    public boolean existsById(UUID id) {
//        return this.userData.containsKey(id);
//    }
//
//    @Override
//    public void deleteById(UUID id) {
//        this.userData.remove(id);
//    }
//}
