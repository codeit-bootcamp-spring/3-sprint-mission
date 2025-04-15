package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entitiy.Channel;
import com.sprint.mission.discodeit.entitiy.Message;
import com.sprint.mission.discodeit.entitiy.User;
import com.sprint.mission.discodeit.repository.MessageRepository;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Collectors;

public class JCFMessageRepository implements MessageRepository {

    private static final String FILE_PATH = "src/main/java/com/sprint/mission/discodeit/service/file/data/messages.ser";

        @Override
        public void save(Message message, User user, Channel channel) {
            //InputStream으로 파일 검색 후 List에 넣음.
            List<Message> messages = new ArrayList<>();
            try (ObjectInputStream reader= new ObjectInputStream(new BufferedInputStream(new FileInputStream(FILE_PATH)))){
                while(true){
                    try {
                        messages.add((Message) reader.readObject());
                    }catch (EOFException e){
                        break;
                    }
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
            try {
                //Channel에 해당하는 user가 있을경우 메세지를 List에 저장후 파일에 출력.
                if (channel.getMembers().contains(user)) {
                    messages.add(message);
                    try (ObjectOutputStream writer = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(FILE_PATH)))) {
                        messages.forEach((c) -> {
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
                else throw new NoSuchElementException();
            } catch (Exception e) {
                System.out.println("채널에 존재하지 않는 사용자입니다.");;
            }


        }

        @Override
        public void read() {
            //InputStream으로 파일 검색 후 메세지 내용 출력.
            try (ObjectInputStream reader= new ObjectInputStream(new BufferedInputStream(new FileInputStream(FILE_PATH)))){
                while(true){
                    try {
                        Message message = (Message) reader.readObject();
                        System.out.println(message);
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
            //InpuStream으로 파일 검색
            try (ObjectInputStream reader= new ObjectInputStream(new FileInputStream(FILE_PATH))){
                while(true){
                    try {
                        Message message = (Message) reader.readObject();
                        //Id가 일치할경우 출력
                        if(message.getId().equals(id)){
                            System.out.println(message);
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
        public void update(UUID id, Message message) {
            //InpuStream으로 파일 검색후 메세지 List에 저장
            List<Message> messages = new ArrayList<>();
            try (ObjectInputStream reader= new ObjectInputStream(new BufferedInputStream(new FileInputStream(FILE_PATH)))){
                while(true){
                    try {
                        messages.add((Message) reader.readObject());
                    }catch (EOFException e){
                        break;
                    }
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }

            //id가 일치하는 메세지를 찾아 수정
            messages.stream()
                    .filter((c)->c.getId().equals(id))
                    .forEach((c)->{c.updateText(message.getText());
                        c.updateUpdatedAt(System.currentTimeMillis());
                        c.updatePicture(message.getPicture());
                        c.updateEmoticon(message.getEmoticon());});

            //OutputStream을 통해 파일에 출력
            try (ObjectOutputStream writer= new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(FILE_PATH)))){
                messages.forEach((c)->{
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
        public void delete(Message message) {
            //기존의 메세지를 저장할 List생성
            List<Message> messages = new ArrayList<>();
            try (ObjectInputStream reader= new ObjectInputStream(new BufferedInputStream(new FileInputStream(FILE_PATH)))){
                while(true){
                    try {
                        messages.add((Message) reader.readObject());
                    }catch (EOFException e){
                        break;
                    }
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }

            //삭제할 id의 메시지를 제외하고 새로운 리스트에 저장
            List<Message> deleteMessages = messages.stream()
                    .filter((c) -> !c.getId().equals(message.getId()))
                    .collect(Collectors.toList());

            //리스트를 OutputStream을 통해 파일에 출력
            try (ObjectOutputStream writer= new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(FILE_PATH)))){
                deleteMessages.forEach((c)->{
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
