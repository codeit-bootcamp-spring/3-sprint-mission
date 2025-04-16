package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.file.FileMessageRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.util.FilePathUtil;
import com.sprint.mission.discodeit.util.FileSerializer;

import java.util.List;
import java.util.UUID;

/**
 * packageName    : com.sprint.mission.discodeit.service.file
 * fileName       : FileMessageService
 * author         : doungukkim
 * date           : 2025. 4. 14.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2025. 4. 14.        doungukkim       최초 생성
 */
public class FileMessageService implements MessageService {
    public FilePathUtil filePathUtil = new FilePathUtil();
    public FileSerializer fileSerializer = new FileSerializer();
    public final FileMessageRepository fmr = new FileMessageRepository(filePathUtil, fileSerializer);
    public ChannelService channelService;

    public FileMessageService(FileChannelService fileChannelService) {
        this.channelService = fileChannelService;
    }

//    -------------------interface-------------------

//    UUID createMessage(UUID senderId, UUID channelId, String message);
//    List<Message> findAllMessages();
//    Message findMessageByMessageId(UUID messageId);
//    void updateMessage(UUID messageId, String newMessage);
//    void deleteMessageById(UUID messageId);
//    void deleteMessagesByChannelId(UUID channelId);

//    -------------------------------------------------

    @Override
    public UUID createMessage(UUID senderId, UUID channelId, String message) {
        Message msg = fmr.saveMessage(senderId, channelId, message);
        if (msg != null) {
            // channel에 id 추가
            channelService.addMessageInChannel(channelId, msg);
            return msg.getId();
        }
        return null;


//        Message msg = new Message(senderId, channelId, message);
//        Path path = filePathUtil.getMessageFilePath(msg.getId());
//
//        if (!Files.exists(path)) {
//            try (FileOutputStream fos = new FileOutputStream(path.toFile());
//                 ObjectOutputStream oos = new ObjectOutputStream(fos)) {
//
//                oos.writeObject(msg);
//                // channel에 id 추가
//                channelService.addMessageInChannel(channelId, msg);
//
//                return msg.getId();
//
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            }
//        }
//        return null;
    }

    @Override
    public List<Message> findAllMessages() {
        return fmr.findAllMessages();
//        Path directory = filePathUtil.getMessageDirectory();
//
//        if (Files.exists(directory)) {
//            try {
//                List<Message> list = Files.list(directory)
//                        .filter(path -> path.toString().endsWith(".ser"))
//                        .map(path -> {
//                            try (
//                                    FileInputStream fis = new FileInputStream(path.toFile());
//                                    ObjectInputStream ois = new ObjectInputStream(fis)
//                            ) {
//                                Object data = ois.readObject();
//                                return (Message) data;
//                            } catch (IOException | ClassNotFoundException exception) {
//                                throw new RuntimeException(exception);
//                            }
//                        }).toList();
//                return list;
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            }
//        }
//        return new ArrayList<>();
    }
    @Override
    public Message findMessageByMessageId(UUID messageId) {
        return fmr.findMessageById(messageId);
//        Path path = filePathUtil.getMessageFilePath(messageId);
//        Message message;
//
//        if (Files.exists(path)) {
//            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path.toFile()))) {
//                message = (Message) ois.readObject();
//                return message;
//
//            } catch (ClassNotFoundException | IOException e) {
//                throw new RuntimeException(e);
//            }
//        }
//        return null;
    }
    @Override
    public void updateMessage(UUID messageId, String newMessage) {
        fmr.updateMessage(messageId, newMessage);
//        Path path = filePathUtil.getMessageFilePath(messageId);
//        Message message;
//        // 메세지 읽어오기
//        if (Files.exists(path)) {
//            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path.toFile()))) {
//                message = (Message) ois.readObject();
//                message.setMessage(newMessage);
//                try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(path.toFile()))) {
//                    oos.writeObject(message);
//                }
//            } catch (IOException | ClassNotFoundException exception) {
//                throw new RuntimeException(exception);
//            }
//        }
    }

    @Override
    public void deleteMessageById(UUID messageId){
        UUID channelId = findMessageByMessageId(messageId).getChannelId();
        fmr.deleteMessageById(messageId);


        // channel 안의 메세지 관리(삭제)
        channelService.deleteMessageInChannel(channelId, messageId);


    }

    // 실제 메세지 파일 삭제 -> channel에 있는 메세지ID
    @Override
    public void deleteMessagesByChannelId(UUID channelId) {
        Channel channel = channelService.findChannelById(channelId);

        if (channel == null) {
            return;
        }

        List<Message> messages = channelService.findChannelById(channelId).getMessages();

        fmr.deleteMessagesByChannelId(channelId);
        for (Message message : messages) {
            // channel에 있는 messageId 삭제
            channelService.deleteMessageInChannel(channelId, message.getId());
        }
    }

}