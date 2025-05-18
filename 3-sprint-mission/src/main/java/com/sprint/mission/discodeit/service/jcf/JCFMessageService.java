package com.sprint.mission.discodeit.service.jcf;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;

import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

public class JCFMessageService implements MessageService {
    private static BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
    private final List<Message> data;
    public JCFMessageService(){
        data = new ArrayList<>();
    }

    @Override
    public Message create(User currentUser, Channel currentChannel, String text) {
        Message message = null;

        if (currentUser.getIsLogin() && currentChannel != null) {
            message = new Message(currentUser.getId(), currentChannel.getId(), text);
            this.data.add(message);

        } else if (!currentUser.getIsLogin()) {
            System.out.println("먼저 로그인하십시오.");

        } else {
            System.out.println("잘못된 접근입니다.");

        }

        return message;
    }

    @Override
    public List<Message> readAll(User currentUser, Channel currentChannel) {
        List<Message> messages = null;

        if (currentUser.getIsLogin() && currentChannel != null) {
            messages = this.data.stream()
                    .filter(m -> m.getChannel().equals(currentChannel.getId()))
                    .collect(Collectors.toList());

        } else if (!currentUser.getIsLogin()) {
            System.out.println("먼저 로그인하십시오.");

        } else if (currentChannel == null) {

            System.out.println("잘못된 접근입니다.");
        }

        return messages;
    }

    @Override
    public Message find(User currentUser, Channel currentChannel, UUID id) {

        return readAll(currentUser, currentChannel).stream()
                .filter(m -> m.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("해당 메시지는 존재하지 않습니다."));
    }

    public List<Message> findByText(User currentUser, Channel currentChannel, String text) {

        return readAll(currentUser, currentChannel).stream()
                .filter(m -> m.getText().contains(text))
                .collect(Collectors.toList());
    }

    @Override
    public void update(User currentUser, Channel currentChannel, UUID id, String text) {
        Message message = find(currentUser, currentChannel, id);

        if (message.getSender().equals(currentUser.getId())) {
            message.updateText(id, text);
        } else {
            System.out.println("권한이 없습니다.");
        }
    }

    @Override
    public void delete(User currentUser, Channel currentChannel, UUID id) {
        Message message = find(currentUser, currentChannel, id);

        if (message.getSender().equals(currentChannel.getId())) {
            this.data.removeIf(m -> m.getId().equals(id));
        } else {
            System.out.println("권한이 없습니다.");
        }
    }
}
