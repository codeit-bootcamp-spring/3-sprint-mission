package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.service.ChannelService;

public class BasicChannelService implements ChannelService {
    private final ChannelRepository channelRepo;
    private Channel currentChannel;

    public BasicChannelService(ChannelRepository channelRepo) {
        this.channelRepo = channelRepo;
    }

    @Override
    public void outputAllChannelInfo() {
        channelRepo.findAllChannel().values().forEach(System.out::println);
    }

    @Override
    public void outputOneChannelInfo(Channel channel) {
        Channel c = channelRepo.findChannel(channel.getChannelNumber());
        if (channel == null) {
            System.out.println("해당 채널은 존재하지 않습니다.");
        } else {
            System.out.println(c);
        }
    }

    @Override
    public void updateChannelName(Channel currentChannel, String newName) {
        Channel channel = channelRepo.findChannel(currentChannel.getChannelNumber());
        if (channel != null) {
            channel.setChannelName(newName);
            channelRepo.updateChannel(channel);
        } else {
            System.out.println("해당 채널을 찾을 수 없습니다.");
        }
    }

    @Override
    public void deleteChannelName(Channel currentChannel) {
        channelRepo.deleteChannel(currentChannel.getChannelNumber());
    }

    @Override
    public void createNewChannel(String channelName) {
        Channel channel = new Channel(channelName);
        channelRepo.saveChannel(channel);
    }

    @Override
    public Channel changeChannel(int channelNumber) {
        currentChannel = channelRepo.findChannel(channelNumber);
        return currentChannel;
    }

    @Override
    public void selectChannel(int channelNumber) {
        currentChannel = channelRepo.findChannel(channelNumber);
        if (currentChannel == null) {
            System.out.println("유효하지 않은 채널 번호입니다.");
        }
    }

    @Override
    public Channel getCurrentChannel() {
        return currentChannel;
    }
}