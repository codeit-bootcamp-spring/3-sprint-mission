package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.DefaultChannelFactory;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;

public class FileChannelRepository implements ChannelRepository {

    private final String FILEPATH = "channels.ser";
    private Map<Integer, Channel> channelMap;

    public FileChannelRepository() {
        channelMap = loadChannels();
        if (channelMap == null || channelMap.isEmpty()) {
            channelMap = new HashMap<>();
            for (Channel ch : DefaultChannelFactory.getChannel()) {
                channelMap.put(ch.getChannelNumber(), ch);
            }
            saveChannels();
        } else {
            int max = channelMap.keySet().stream().mapToInt(Integer::intValue).max().orElse(0);
            Channel.setCounter(max + 1);
        }
    }

    private Map<Integer, Channel> loadChannels() {
        File file = new File(FILEPATH);
        if (!file.exists()) {
            return null;
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (Map<Integer, Channel>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void saveChannels() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILEPATH))) {
            oos.writeObject(channelMap);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void saveChannel(Channel channel) {
        channelMap.put(channel.getChannelNumber(), channel);
        saveChannels();
    }

    @Override
    public Channel updateChannel(Channel channel) {
        if (channelMap.containsKey(channel.getChannelNumber())) {
            channelMap.put(channel.getChannelNumber(), channel);
            saveChannels();
            return channel;
        }
        return null;
    }

    @Override
    public void deleteChannel(int channelNumber) {
        channelMap.remove(channelNumber);
        saveChannels();
    }

    @Override
    public Channel findChannel(int channelNumber) {
        return channelMap.get(channelNumber);
    }

    @Override
    public Map<Integer, Channel> findAllChannel() {
        return channelMap;
    }
}