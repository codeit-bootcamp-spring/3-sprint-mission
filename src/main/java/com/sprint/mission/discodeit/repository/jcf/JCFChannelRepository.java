package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entitiy.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class JCFChannelRepository implements ChannelRepository {

    private static final String FILE_PATH = "src/main/java/com/sprint/mission/discodeit/service/file/data/channels.ser";

    @Override
    public void save(Channel channel) {

        //InputStream으로 파일 검색 후 List에 넣음.
        List<Channel> channels = new ArrayList<>();
        try (ObjectInputStream reader= new ObjectInputStream(new BufferedInputStream(new FileInputStream(FILE_PATH)))){
            while(true){
                try {
                    channels.add((Channel) reader.readObject());
                }catch (EOFException e){
                    break;
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        //List에 생성할 채널 추가
        channels.add(channel);

        //List에 파일쓰기
        try (ObjectOutputStream writer= new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(FILE_PATH)))){
            channels.forEach((c)->{
                try {
                    writer.writeObject(c);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void read() {
        //ObjectInputStream으로 파일검색
        try (ObjectInputStream reader= new ObjectInputStream(new BufferedInputStream(new FileInputStream(FILE_PATH)))){
            while(true){
                try {
                    Channel channel = (Channel) reader.readObject();
                    System.out.println(channel);
                }catch (EOFException e){
                    break;
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void readById(UUID id) {
        //ObjectInputStream으로 파일검색
        try (ObjectInputStream reader= new ObjectInputStream(new FileInputStream(FILE_PATH))){
            while(true){
                try {
                    Channel channel = (Channel) reader.readObject();
                    //id가 일치하는 채널 검색
                    if(channel.getId().equals(id)){
                        System.out.println(channel);
                    }
                }catch (EOFException e){
                    break;
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(UUID id, Channel channel) {
        //InputStream으로 파일 검색 후 List에 넣음.
        List<Channel> channels = new ArrayList<>();
        try (ObjectInputStream reader= new ObjectInputStream(new BufferedInputStream(new FileInputStream(FILE_PATH)))){
            while(true){
                try {
                    channels.add((Channel) reader.readObject());
                }catch (EOFException e){
                    break;
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        //id가 일치하는 채널을 찾아 수정
        channels.stream()
                .filter((c)->c.getId().equals(id))
                .forEach((c)->{c.updateChannelName(channel.getChannelName());
                                        c.updateUpdatedAt(System.currentTimeMillis());
                                        c.updateMembers(channel.getMembers());});

        //생성된 List를 파일에 출력
        try (ObjectOutputStream writer= new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(FILE_PATH)))){
            channels.forEach((c)->{
                try {
                    writer.writeObject(c);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(Channel channel) {
        //InputStream으로 파일 검색 후 List에 넣음.
        List<Channel> channels = new ArrayList<>();
        try (ObjectInputStream reader= new ObjectInputStream(new BufferedInputStream(new FileInputStream(FILE_PATH)))){
            while(true){
                try {
                    channels.add((Channel) reader.readObject());
                }catch (EOFException e){
                    break;
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        //Id가 일치하는 채널을 제외하고 새로운 List생성
        List<Channel> deleteChannels = channels.stream()
                .filter((c) -> !c.getId().equals(channel.getId()))
                .collect(Collectors.toList());

        //생성된 List를 파일에 출력
        try (ObjectOutputStream writer= new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(FILE_PATH)))){
            deleteChannels.forEach((c)->{
                try {
                    writer.writeObject(c);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
