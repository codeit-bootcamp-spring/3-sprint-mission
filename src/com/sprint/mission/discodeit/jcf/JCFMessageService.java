package com.sprint.mission.discodeit.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.MessageService;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import static com.sprint.mission.discodeit.entity.Message.msgNumber;
import static java.lang.System.currentTimeMillis;

public class JCFMessage implements MessageService {

    public void sendMessage() {             // 새로운 메세지 입력
        while (true) {
            System.out.print("메세지 입력 >>> ");
            Scanner sc = new Scanner(System.in);
            String scannedMsg = sc.nextLine();
            if (scannedMsg.length() == 0) {break;}
            String nowTime = new SimpleDateFormat("HH:mm:ss").format(currentTimeMillis()); // 현재시각 TimeStamp생성
            messageMap.put(msgNumber++, new Message(List.of("Sender1", scannedMsg, nowTime, nowTime)));
            System.out.println(messageMap.get(msgNumber -1));
        }
    }

    public void readAllMessage() {                         // 메세지 목록 MAP 전체 불러오기
        System.out.println(" 입력된 메세지는 아래와 같습니다. \n 글번호 | 작성자 , 메세지 , 작성시간 , 수정시간");
        for (Map.Entry<Integer, Message> entry : messageMap.entrySet()) {
            int msgNumber = entry.getKey();
            Message msgBody = entry.getValue();
            System.out.println(msgNumber + " | " + msgBody);
        }
    }
//    public void modifyMessage(int modMsgNumber){
//        String newmessage = "modified";
//        messageMap.get(modMsgNumber).setMsgs(1,newmessage);
//        messageMap.put(modMsgNumber,)
//
//    }

}
