package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.dto.CreateBinaryContentRequest;
import com.sprint.mission.discodeit.dto.CreateUserRequest;
import com.sprint.mission.discodeit.dto.FindUserRequest;
import com.sprint.mission.discodeit.dto.UpdateUserRequest;
import com.sprint.mission.discodeit.entitiy.BinaryContent;
import com.sprint.mission.discodeit.entitiy.User;
import com.sprint.mission.discodeit.entitiy.UserStatus;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserService;
import com.sun.jdi.request.DuplicateRequestException;

import java.io.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class FileUserService implements UserService {

    UserRepository userRepository;
    UserStatusRepository userStatusRepository;
    BinaryContentRepository binaryContentRepository;

    @Override
    public User create(CreateUserRequest request) {
        User user = new User(request.username(), request.password(), request.email(), null);
        //중복검사후 아닐경우 save
        try{
            if(!userRepository.duplicateCheck(user)){
                userRepository.save(user);
                userStatusRepository.save(new UserStatus(user.getId()));
                return user;
            }else
                throw new DuplicateRequestException();
        }catch (DuplicateRequestException e){
            e.printStackTrace();
        }

    }

    @Override
    public User create(CreateUserRequest userRequest, CreateBinaryContentRequest binaryContentRequest) {
        User user = create(userRequest);
        BinaryContent binaryContent = new BinaryContent(binaryContentRequest.contentType(), binaryContentRequest.content());
        binaryContentRepository.save(binaryContent);
        return user;
    }

    @Override
    public List<FindUserRequest> findAll() {
        List<User> userList = userRepository.read();
        List<FindUserRequest> findUserRequestList = new ArrayList<>();
        findUserRequestList = userList.stream()
                .map(user->new FindUserRequest(user.getId(),user.getProfileId(),user.getCreatedAt(),user.getUpdatedAt(),user.getUsername(),user.getEmail(),user.getFriends(),userStatusRepository.readById(user.getId()).get()))
                .toList();
        return findUserRequestList;
    }

    @Override
    public FindUserRequest find(UUID id) {
        Optional<User> user = userRepository.readById(id);
        try {
            if (user.isPresent()) {
                FindUserRequest findUserRequest = new FindUserRequest(user.get().getId(), user.get().getProfileId(), user.get().getCreatedAt(), user.get().getUpdatedAt(), user.get().getUsername(), user.get().getEmail(), user.get().getFriends(), userStatusRepository.readById(user.get().getId()).get());
                return findUserRequest;
            } else {
                throw new ClassNotFoundException();
            }
        }catch (ClassNotFoundException e){
            System.out.println("존재하지 않는 id 입니다.");;
        }
    }

    @Override
    public void update(UpdateUserRequest request) {
        User user = new User(request.username(), request.password(), request.email(), request.friends());
        userRepository.update(request.id(), user);
    }

    @Override
    public void update(UpdateUserRequest request,CreateBinaryContentRequest binaryContentRequest) {
        update(request);
        BinaryContent binaryContent = new BinaryContent(binaryContentRequest.contentType(), binaryContentRequest.content());
        if(binaryContentRepository.readById(request.id()).isEmpty()){
            binaryContentRepository.save(binaryContent);
        } else{
            binaryContentRepository.update(request.id(), binaryContent);
        }
    }

    @Override
    public void delete(User user) {
        userStatusRepository.delete(userStatusRepository.readById(user.getId()).get());
        binaryContentRepository.delete(binaryContentRepository.readById(user.getId()).get());
        userRepository.delete(user);
    }
}
