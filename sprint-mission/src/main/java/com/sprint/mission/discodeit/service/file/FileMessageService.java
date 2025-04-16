package com.sprint.mission.discodeit.service.file;


import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class FileMessageService implements MessageService {
    private final Map<UUID, Message> messages;
    private final ChannelService channelService;
    private final String fileName = "files/message.ser";

    public FileMessageService(ChannelService channelService) {
        this.channelService = channelService;
        this.messages = loadFromFile();
    }

    private Map<UUID, Message> loadFromFile() {

        File file = new File(fileName);

        if(!file.exists())
        {
            return new HashMap<>();
        }

        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(file)))
        {
            return (Map<UUID, Message>) in.readObject();
        }
        catch (IOException | ClassNotFoundException e)
        {
            throw new RuntimeException("파일 로드 실패입니다 : " + e.getMessage(), e);
        }
    }

    private void saveToFile() {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(fileName))) {
            out.writeObject(messages);
        }
        catch (IOException e){
            throw new RuntimeException("파일 저장 실패입니다 : " + e.getMessage(), e);
        }
    }


    @Override
    public Message createMessage(String text, UUID channelID, UUID userID) {
        Message newMessage = new Message(text, channelID, userID);
        messages.put(newMessage.getId(), newMessage);
        channelService.addMessageToChannel(channelID, newMessage.getId());
        System.out.println("메시지 생성 : " + text);
        saveToFile();
        return newMessage;
    }

    @Override
    public Map<UUID, Message> readMessages() {
        return messages;
    }

    @Override
    public Optional<Message> readMessage(UUID id) {
        return Optional.ofNullable(messages.get(id));
    }

    @Override
    public Message updateMessage(UUID id, String text) {
        Message message = messages.get(id);
        if(message==null){
            throw new IllegalArgumentException("ID가" + id + "인 메시지를 찾을 수 없습니다.");
        }
        message.updateText(text);
        saveToFile();
        return message;
    }

    @Override
    public Message deleteMessage(UUID id) {
        saveToFile();
        return messages.remove(id);
    }

}
