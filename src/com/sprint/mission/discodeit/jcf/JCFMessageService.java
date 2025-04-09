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
    // 테스트용 초기 더미 데이터 입력
    public JCFMessageService() {
        messages.add(new Message(001,"희동이","요리보고 조리봐도", 1744036548250L , 1744036548250L));
        messages.add(new Message(002,"희동이","알수없는 둘리둘리", 1744036558250L , 1744036558250L));
        messages.add(new Message(003,"도우너","빙하타고~내려와아~", 1744036938250L , 1744036938250L));
        messages.add(new Message(004,"고길동","우리집에 들어왔지 이 식충이녀석들", 1744036938250L , 1744036938250L));
    }
    @Override
    public boolean uploadMsg(User user){
        System.out.print(" >>  ");
        String txtMsg = scanner.nextLine();
        if (txtMsg.length()!=0){
            long now = System.currentTimeMillis();
            Message.totMsgNumber = messages.size();
            Message message = new Message(Message.totMsgNumber + 1,user.getName(),txtMsg,now,now);
            messages.add(message);
            System.out.print("\n ▶ [메세지 등록 완료]");
            System.out.println(" ▶ 메세지 번호 : " + message.getMsgNumber() + "     ▶ 등록시간 : " + message.getCreatedAt());
            return true;} else{
            System.out.println(" ▶ 메세지 입력을 종료합니다");
            return false;
        }
    }

    @Override
    public void updateMsg(Message message,String newMsg) {
        message.setTextMsg(newMsg);
        message.setUpdatedAt(System.currentTimeMillis());
    }

    public void deleteMessage(Message message){
        message.setTextMsg("*deleted");
        message.setAuthor("*deleted");
        message.setUpdatedAt(System.currentTimeMillis());
    }
    public Message findMessageByNum(int num){
        for (Message message : messages) {
            if (message.getMsgNumber() == num) {
                return message; // 일치하는 메세지 번호 발견시 message 리턴
            }
        }return null;
    }
    public void printAllMessages() {
        System.out.println("<< 입력된 전체 메세지 >>");
        System.out.println("번호 | 사용자 : 메세지");
        messages.forEach(m -> System.out.println(m.getMsgNumber() + "    | " + m.getAuthor() + " : " + m.getTextMsg() + "  |   " + m.getCreatedAt() + "    |   " + m.getUpdatedAt() + "    |    " + m.getId()));
        System.out.println("");
    }
    public List<Message> getMessagesList() {
        return new ArrayList<>(messages);
    }
}
