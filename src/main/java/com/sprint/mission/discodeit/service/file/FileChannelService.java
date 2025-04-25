package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.DefaultChannelFactory;
import com.sprint.mission.discodeit.service.ChannelService;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FileChannelService implements ChannelService {

    private Map<Integer, Channel> channelMap;
    private Channel currentChannel;
    private final static String FILEPATH = "channels.ser";

    public FileChannelService() {
        channelMap = loadChannels();

        if (channelMap == null || channelMap.isEmpty()) {
            channelMap = new HashMap<>();
            List<Channel> defaultChannels = DefaultChannelFactory.getChannel();
            for (Channel channel : defaultChannels) {
                channelMap.put(channel.getChannelNumber(), channel);
            }
            saveChannels();
        } else {
            int max = 0;
            for (Channel channel : channelMap.values()) {
                if (channel.getChannelNumber() > max) {
                    max = channel.getChannelNumber();
                }
            }
            Channel.setCounter(max + 1);
        }
    }

    @SuppressWarnings("unchecked")
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
    public void outputAllChannelInfo() {
        for (Channel channel : channelMap.values()) {
            System.out.println(channel);
        }
    }

    @Override
    public void outputOneChannelInfo(Channel channel) {
        if (channel == null) {
            System.out.println("해당 채널은 존재하지 않습니다.");
        } else {
            System.out.println(channel);
        }
    }

    @Override
    public void updateChannelName(Channel currentChannel, String newName) {
        Channel channel = channelMap.get(currentChannel.getChannelNumber());
        if (channel != null) {
            channel.updateChannel(newName);
            saveChannels();
        } else {
            System.out.println("해당 채널을 찾을 수 없습니다.");
        }
    }

    @Override
    public void deleteChannelName(Channel currentChannel) {
        Integer findChannelKey = null;
        for (Map.Entry<Integer, Channel> entry : channelMap.entrySet()) {
            if (entry.getValue().getChannelNumber() == currentChannel.getChannelNumber()) {
                findChannelKey = entry.getKey();
                break;
            }
        }
        if (findChannelKey != null) {
            channelMap.remove(findChannelKey);
            saveChannels();
        }
    }

    @Override
    public void createNewChannel(String channelName) {
        Channel channel = new Channel(channelName);
        channelMap.put(channel.getChannelNumber(), channel);
        saveChannels();
    }

    @Override
    public Channel changeChannel(int channelNumber) {
        Channel newChannel = channelMap.get(channelNumber);
        if (newChannel != null) {
            currentChannel = newChannel;
        }
        return currentChannel;
    }

    @Override
    public void selectChannel(int channelNumber) {
        Channel channel = channelMap.get(channelNumber);
        if (channel != null) {
            currentChannel = channel;
        } else {
            System.out.println("유효하지 않은 채널 번호입니다.");
        }
    }

    @Override
    public Channel getCurrentChannel() {
        return currentChannel;
    }
}