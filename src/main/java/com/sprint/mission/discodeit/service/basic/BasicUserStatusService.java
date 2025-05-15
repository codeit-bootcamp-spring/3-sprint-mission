package com.sprint.mission.discodeit.service.basic;


import com.sprint.mission.discodeit.dto.CreateUserStatusRequest;
import com.sprint.mission.discodeit.dto.UpdateUserStatusRequest;
import com.sprint.mission.discodeit.entitiy.User;
import com.sprint.mission.discodeit.entitiy.UserStatus;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserStatusService;
import com.sun.jdi.request.DuplicateRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicUserStatusService implements UserStatusService {

    private final UserStatusRepository userStatusRepository;
    private final UserRepository userRepository;

    @Override
    public UserStatus create(CreateUserStatusRequest request){
        Optional<User> user = userRepository.readById(request.userId());
        try {
            if (user.isEmpty()) {
                throw new NoSuchElementException("해당id의 User는 존재하지 않습니다.");
            } else {
                List<UserStatus> read = userStatusRepository.read();
                boolean duplicatedUserStatus = read.stream().anyMatch(userStatus -> userStatus.getUserId().equals(request.userId()));
                if(duplicatedUserStatus){
                    throw new DuplicateRequestException("중복된 userStatus입니다.");
                }
                else{
                    return userStatusRepository.save(new UserStatus(request.userId()));
                }
            }
        }catch (NoSuchElementException | DuplicateRequestException e){
            System.out.println(e);
            return null;
        }
    }

    @Override
    public UserStatus find(UUID userStatusId){
        Optional<UserStatus> userStatus = userStatusRepository.readById(userStatusId);
        try {
            if (userStatus.isPresent()) {
                return userStatus.get();
            } else {
                throw new NoSuchElementException("해당 userStatusId는 존재하지 않습니다");
            }
        }catch (NoSuchElementException e){
            System.out.println(e);
            return null;
        }
    }

    @Override
    public UserStatus findByUserId(UUID userId) {
        return userStatusRepository.readByUserId(userId).orElseThrow(NoSuchElementException::new);
    }

    @Override
    public List<UserStatus> findAll(){
        return userStatusRepository.read();
    }

    @Override
    public void update(UpdateUserStatusRequest request) {
        Optional<UserStatus> userStatus = userStatusRepository.readById(request.id());
        try {
            if (userStatus.isPresent()) {
                userStatusRepository.update(request.id(), new UserStatus(request.userId()));
            } else {
                throw new NoSuchElementException("해당하는 id의 userStatus는 존재하지 않습니다.");
            }
        } catch (NoSuchElementException e) {
            System.out.println(e);
        }
    }

    @Override
    public void updateByUserId(UpdateUserStatusRequest request){
        Optional<User> user = userRepository.readById(request.userId());
        try {
            if (user.isPresent()) {
                Optional<UserStatus> userStatus = userStatusRepository.readByUserId(request.userId());
                if(userStatus.isPresent()){
                    userStatusRepository.update(userStatus.get().getId(), new UserStatus(request.userId()));
                }
                else throw new NoSuchElementException("해당하는 user의 UserStatus가 존재하지 않습니다.");
            } else {
                throw new NoSuchElementException("해당하는 id의 userStatus는 존재하지 않습니다.");
            }
        } catch (NoSuchElementException e) {
            System.out.println(e);
        }
    }

    @Override
    public void delete(UUID userStatusId){
        userStatusRepository.delete(userStatusId);
    }
}

