package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;


/*  비즈니스 로직은 동일하게 작성
    저장 로직 -
    FileService는 직렬화/역직렬화를 통해야한다.
    그렇기에 -saveToFile()에서 현재 데이터를 직렬화하여 .ser 확장자로 로컬에 저장하여 데이터 입력,변경,삭제시 saveToFile()을 통해 변경
            -loadFile()은 로컬 경로의 파일을 읽어 역직렬화를 하여 Map<UUID, (class)> 형태로 리턴, 이를 프로그램 종료 시에도 저장을 통해ㅔ,
            재시작 시에도 사용가능!
 */
public class FileChannelService implements ChannelService {
    private final Map<UUID, Channel> channels;
    private static final String fileName = "files/channel.ser";


    public FileChannelService(){
        this.channels = loadFromFile();
    }


    // 저장로직

    private Map<UUID, Channel> loadFromFile() {

        File file = new File(fileName);

        if(!file.exists()){
            return new HashMap<>();
        }

        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(file)))
        {
            return (Map<UUID, Channel>)  in.readObject();
        }
        catch (IOException | ClassNotFoundException e)
        {
            throw new RuntimeException("파일 로드 실패입니다 : " + e.getMessage(), e);
        }
    }

    private void saveToFile() {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(fileName))){
            out.writeObject(channels);
        }
        catch (IOException e){
            throw new RuntimeException("파일 저장 실패입니다 : " + e.getMessage(), e);
        }
    }


    @Override
    public Channel createChannel(String channelName) {
        Channel newChannel = new Channel(channelName);
        channels.put(newChannel.getId(), newChannel);
        System.out.println(newChannel.getChannelName() + "채널이 추가되었습니다.");
        saveToFile();
        return newChannel;
    }

    @Override
    public Map<UUID, Channel>  readChannels() {
        return channels;
    }

    @Override
    public Optional<Channel> readChannel(UUID id) {
        return Optional.ofNullable(channels.get(id));
    }

    @Override
    public Channel addMessageToChannel(UUID channelId, UUID messageId) {
        Channel channel = channels.get(channelId);
        channel.addMessageToChannel(messageId);
        saveToFile();
        return channel;
    }

    @Override
    public Channel addUserToChannel(UUID channelId, UUID userId){
        Channel channel = channels.get(channelId);
        channel.addUserToChannel(userId);
        saveToFile();
        return channel;
    }

    @Override
    public Channel updateChannel(UUID id, String channelName) {
        Channel channel = channels.get(id);
        if(channel==null){
            throw new IllegalArgumentException("ID가" + id + "인 채널을 찾을 수 없습니다.");
        }
        channel.updateChannelName(channelName);
        saveToFile();
        return channel;
    }

    @Override
    public Channel deleteChannel(UUID id) {
        saveToFile();
        return channels.remove(id);
    }
}
