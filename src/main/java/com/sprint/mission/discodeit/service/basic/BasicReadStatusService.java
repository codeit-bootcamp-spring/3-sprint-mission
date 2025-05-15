package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.CreateReadStatusRequest;
import com.sprint.mission.discodeit.dto.UpdateReadStatusRequest;
import com.sprint.mission.discodeit.entitiy.Channel;
import com.sprint.mission.discodeit.entitiy.ReadStatus;
import com.sprint.mission.discodeit.entitiy.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ReadStatusService;
import com.sun.jdi.request.DuplicateRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicReadStatusService implements ReadStatusService {

    private final ReadStatusRepository readStatusRepository;
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;

    @Override
    public ReadStatus create(CreateReadStatusRequest request){
        Optional<User> user = userRepository.readById(request.userId());
        Optional<Channel> channel = channelRepository.readById(request.channelId());
        try {
            if (user.isEmpty() || channel.isEmpty()) {
                throw new NoSuchElementException("userId 또는 channelId가 일치하지 않습니다");
            } else {
                List<ReadStatus> readByUserId = readStatusRepository.readByUserId(user.get().getId());
                boolean duplicatedReadStatus = readByUserId.stream().anyMatch(readStatus -> readStatus.getChannelId().equals(channel.get().getId()));
                if(duplicatedReadStatus){
                    throw new DuplicateRequestException("중복된 readStatus입니다.");
                }
                else{
                    return readStatusRepository.save(new ReadStatus(request.userId(),request.channelId()));
                }
            }
        } catch (NoSuchElementException | DuplicateRequestException e) {
            System.out.println(e);
            return null;
        }
    }
    @Override
    public ReadStatus find(UUID id){
        Optional<ReadStatus> readStatus = readStatusRepository.readById(id);
        try {
            if (readStatus.isPresent()) {
                return readStatus.get();
            } else {
                throw new NoSuchElementException("해당하는 id의 ReadStatus는 존재하지 않습니다.");
            }
        }catch (NoSuchElementException e){
            System.out.println(e);
            return null;
        }
    }
    @Override
    public List<ReadStatus> findAllByUserId(UUID userId){
        return readStatusRepository.readByUserId(userId);
    }

    @Override
    public void update(UpdateReadStatusRequest request){
        Optional<ReadStatus> readStatus = readStatusRepository.readById(request.id());
        try {
            if (readStatus.isPresent()) {
                readStatusRepository.update(request.id(), new ReadStatus(request.userId(),request.channelId()));
            } else {
                throw new NoSuchElementException("해당하는 id의 ReadStatus는 존재하지 않습니다.");
            }
        }catch (NoSuchElementException e){
            System.out.println(e);
        }

    }
    @Override
    public void delete(UUID readStatusId){
        readStatusRepository.delete(readStatusId);
    }
}

