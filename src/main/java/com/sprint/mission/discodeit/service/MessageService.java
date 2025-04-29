package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.MessageCreateResponse;
import com.sprint.mission.discodeit.dto.MessageUpdateRequest;

import java.util.List;
import java.util.UUID;

/*
 * 도메인 모델 별 CRUD(생성, 읽기, 모두 읽기, 수정, 삭제) 기능을 인터페이스로 선언
 * */

public interface MessageService {

    //TODO: 객체 생성 후 -> create 함수.  validation과 관계없이 객체 생성을 먼저 하는데 괜찮은가? -> 생성자에서 체크할것
    public MessageCreateResponse create(MessageCreateRequest createRequest);

    public MessageCreateResponse findById(UUID messageId);

    public List<MessageCreateResponse> findAllByChannelId(UUID channelId);

    public MessageCreateResponse update(MessageUpdateRequest updateRequest);

    public void delete(UUID messageId);

}
