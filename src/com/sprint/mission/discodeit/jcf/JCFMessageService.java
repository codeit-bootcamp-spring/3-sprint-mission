package com.sprint.mission.discodeit.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.MessageService;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class JCFMessageService implements MessageService {
    private final List<Message> messages = new ArrayList<>();
    private static Scanner scanner = new Scanner(System.in);

    @Override
    public boolean uploadMsg(User user){
        System.out.print("\n 메세지를 입력해주세요 >>  ");
        String txtMsg = scanner.nextLine();
        if (txtMsg.length()!=0){
            long now = System.currentTimeMillis();
            Message message = new Message(Message.totMsgNumber++,user.getName(),txtMsg,now,now);
            messages.add(message);
            System.out.print("\n System : [메세지 등록 완료]");
            System.out.println(" >> 메세지 번호 : " + message.getMsgNumber() + "     등록시간 : " + message.getCreatedAt());
            return true;} else{
            System.out.println(" System : 메세지 입력을 종료합니다");
            return false;
        }
    }

    @Override
    public void updateMsg(Message message) {
        System.out.println("저장된 메세지 : " + message.getTextMsg());
        System.out.print("변경할 메세지 : ");
        String newMsg = scanner.nextLine();
        message.setTextMsg(newMsg);
        message.setUpdatedAt(System.currentTimeMillis());
        System.out.println();
    }

    public void printAllMessages() {
        System.out.println("<< 입력된 전체 메세지 >>");
        messages.forEach(m -> System.out.println(m.getMsgNumber() + "|" + m.getAuthor() + "|" + m.getTextMsg() + "|" + m.getCreatedAt() + "|" + m.getUpdatedAt() + "|||" + m.getId()));
    }
    public List<Message> getMessagesList() {
        return new ArrayList<>(messages);
    }
}
