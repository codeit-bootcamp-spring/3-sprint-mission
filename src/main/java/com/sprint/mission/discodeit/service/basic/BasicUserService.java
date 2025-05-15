package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.CreateBinaryContentRequest;
import com.sprint.mission.discodeit.dto.CreateUserRequest;
import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.dto.UpdateUserRequest;
import com.sprint.mission.discodeit.entitiy.BinaryContent;
import com.sprint.mission.discodeit.entitiy.User;
import com.sprint.mission.discodeit.entitiy.UserStatus;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserService;
import com.sun.jdi.request.DuplicateRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BasicUserService implements UserService {

    private final UserRepository userRepository;
    private final UserStatusRepository userStatusRepository;
    private final BinaryContentRepository binaryContentRepository;


    @Override
    public User create(CreateUserRequest userRequest, Optional<CreateBinaryContentRequest> binaryContentRequest) {
        User user = new User(null,userRequest.username(), userRequest.password(), userRequest.email());
        //중복검사후 아닐경우 save
        try{
            if(!userRepository.duplicateCheck(user)) {
                if (binaryContentRequest.isPresent()) {
                    BinaryContent binaryContent = new BinaryContent(binaryContentRequest.get().filename(),binaryContentRequest.get().contentType(), binaryContentRequest.get().bytes());
                    binaryContentRepository.save(binaryContent);
                    user.setProfileId(binaryContent.getId());
                }
                userRepository.save(user);
                userStatusRepository.save(new UserStatus(user.getId()));
                return user;
            }else
                throw new DuplicateRequestException();
        }catch (DuplicateRequestException e) {
            System.out.println("중복된 name 또는 email 입니다.");
        }
        return null;
    }

    @Override
    public List<UserDto> findAll() {
        List<User> userList = userRepository.read();
        List<UserDto> findUserRespondList = new ArrayList<>();
        findUserRespondList = userList.stream()
                .map(user->new UserDto(user.getId(),user.getCreatedAt(),user.getUpdatedAt(),user.getUsername(),user.getEmail(),user.getProfileId(),userStatusRepository.readByUserId(user.getId()).get().IsOnline()))
                .collect(Collectors.toList());
        return findUserRespondList;
    }

    @Override
    public UserDto find(UUID id) {
        Optional<User> user = userRepository.readById(id);
        try {
            if (user.isPresent()) {
                UserDto findUserRespond = new UserDto(user.get().getId(),user.get().getCreatedAt(),user.get().getUpdatedAt(),user.get().getUsername(),user.get().getEmail(),user.get().getProfileId(),userStatusRepository.readByUserId(user.get().getId()).get().IsOnline());
                return findUserRespond;
            } else {
                throw new NoSuchElementException("존재하지 않는 id 입니다.");
            }
        }catch (NoSuchElementException e){
            System.out.println(e);
            return null;
        }

    }

    @Override
    public void update(UpdateUserRequest request,Optional<CreateBinaryContentRequest> binaryContentRequest) {
        User user = new User(null, request.username(), request.password(), request.email());
        user.setFriends(request.friends());
        if(binaryContentRequest.isPresent()) {
            BinaryContent binaryContent = new BinaryContent(binaryContentRequest.get().filename(), binaryContentRequest.get().contentType(), binaryContentRequest.get().bytes());
            if (binaryContentRepository.readById(userRepository.readById(request.id()).get().getProfileId()).isEmpty()) {
                binaryContentRepository.save(binaryContent);
            } else {
                binaryContentRepository.update(request.id(), binaryContent);
            }
        }
        userRepository.update(request.id(), user);
    }

    @Override
    public void delete(UUID userId) {
        Optional<User> user = userRepository.readById(userId);
        UUID profileId;
        try {
            if (user.isPresent()) {
                User user1 = user.get();
                profileId = user1.getProfileId();
                UserStatus userStatus = userStatusRepository.readByUserId(user1.getId()).get();
                userStatusRepository.delete(userStatus.getId());
                binaryContentRepository.delete(profileId);
                userRepository.delete(userId);
            } else {
                throw new NoSuchElementException("존재하지 않는 userId입니다.");
            }
        }
        catch (NoSuchElementException e){
            System.out.println(e);
        }

    }
}
