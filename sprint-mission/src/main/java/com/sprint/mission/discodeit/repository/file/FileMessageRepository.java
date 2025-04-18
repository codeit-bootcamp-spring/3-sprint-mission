package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class FileMessageRepository implements MessageRepository {

    private static final String fileName = "files/message.ser";
    private final Map<UUID, Message> messages;

    public FileMessageRepository()
    {
        this.messages = load();
    }

    @Override
    public void save(Message message)
    {
        try(ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(fileName)))
        {
            out.writeObject(messages);
        }
        catch(IOException e)
        {
            throw new RuntimeException("파일 저장 실패입니다 : " + e.getMessage(), e);
        }
    }

    @Override
    public Map<UUID, Message> load(){
        File file = new File(fileName);

        if(!file.exists())
        {
            return new HashMap<>();
        }

        try(ObjectInputStream in = new ObjectInputStream(new FileInputStream(file)))
        {
            return (Map<UUID, Message>) in.readObject();
        }
        catch (IOException | ClassNotFoundException e)
        {
            throw new RuntimeException("파일 로드 실패입니다 : " + e.getMessage(), e);
        }

    }

    @Override
    public void deleteMessage(UUID id)
    {
        messages.remove(id);
    }




}
