//package com.sprint.mission.discodeit.service.basic;
//
//import com.sprint.mission.discodeit.entity.Message;
//import com.sprint.mission.discodeit.repository.MessageRepository;
//import com.sprint.mission.discodeit.repository.file.FileMessageRepository;
//import com.sprint.mission.discodeit.service.MessageService;
//
//import java.util.List;
//import java.util.Map;
//import java.util.UUID;
//import java.util.concurrent.ConcurrentHashMap;
//
//public class BasicMessageService implements MessageService {
//    private Map<UUID, List<Message>> channelMsgBoard = new ConcurrentHashMap<>();
//    private final MessageRepository messageRepository;
//
//    public BasicMessageService(MessageRepository messageRepository){
//        this.messageRepository = messageRepository;
//        this.channelMsgBoard = messageRepository.loadMessageBoard();
//    }
//    public BasicMessageService() {
//        this.messageRepository = new FileMessageRepository();
//        this.channelMsgBoard = messageRepository.loadMessageBoard();
//        messageRepository.saveMessageBoard(channelMsgBoard);
//    }
//    public void saveMessageBoard(){
//        messageRepository.saveMessageBoard(channelMsgBoard);
//    }
//    public Map<UUID, List<Message>> getChannelMsgBoard() {
//        return channelMsgBoard;
//    }
//    public void setChannelMsgBoard(Map<UUID, List<Message>> channelMsgBoard) {
//        this.channelMsgBoard = channelMsgBoard;
//    }
//}
