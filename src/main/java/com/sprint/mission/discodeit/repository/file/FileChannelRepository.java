package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileChannelRepository implements ChannelRepository {
    // 초기 채널 입력
    private final List<Channel> channels;
    private static final String CHANNEL_FILE_NAME = "src/files/channel.ser";

    public FileChannelRepository(){
        this.channels = fileLoadChannels();
        if(channels.isEmpty()){
            channels.add(new Channel("ch01","default Channel","관리자",1744036548250L,1744036548250L));
            fileSaveChannels();
        }
    }

    // 파일 로드 메서드
    @SuppressWarnings("unchecked")
    private List<Channel> fileLoadChannels(){
        File file = new File(CHANNEL_FILE_NAME);
        if (!file.exists() || file.length() == 0) {
            return new ArrayList<>();
        }
        try (FileInputStream fis = new FileInputStream(CHANNEL_FILE_NAME);
             ObjectInputStream ois = new ObjectInputStream(fis)){
            return (List<Channel>)ois.readObject();
        } catch (IOException | ClassNotFoundException e){
            return new ArrayList<>();
        }
    }
    // 파일 세이브 메서드
    public void fileSaveChannels(){
        try(FileOutputStream fos = new FileOutputStream(CHANNEL_FILE_NAME);
            ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(channels);
            System.out.println("파일생성 성공. ///channel.ser");
        } catch (IOException e){
            System.out.println("파일생성에실패하였습니다. ///channel.ser");
        }
    }

    public List<Channel> getChannelsList(){
        return channels;
    }

}
