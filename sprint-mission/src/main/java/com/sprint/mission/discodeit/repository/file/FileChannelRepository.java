package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;

import javax.imageio.IIOException;
import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class FileChannelRepository implements ChannelRepository {

    private static final String fileName = "files/channel.ser";
    private final Map<UUID, Channel> channels;

    public FileChannelRepository() {
        this.channels = load();
    }

    @Override
    public void save(Channel channel){
        try(ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(fileName))){
          out.writeObject(channels);
        }
        catch(IOException e)
        {
         throw new RuntimeException("파일 저장 실패입니다 : " + e.getMessage(), e);
        }
    }

    @Override
    public Map<UUID, Channel> load() {
        File file = new File(fileName);

        if(!file.exists())
        {
            return new HashMap<>();
        }

        try(ObjectInputStream in = new ObjectInputStream(new FileInputStream(file)))
        {
            return (Map<UUID, Channel>) in.readObject();
        }
        catch(IOException | ClassNotFoundException e)
        {
            throw new RuntimeException("파일 로드 실패입니다 : " + e.getMessage(), e);
        }

    }

    @Override
    public void deleteChannel(UUID id)
    {
        channels.remove(id);
    }



}
