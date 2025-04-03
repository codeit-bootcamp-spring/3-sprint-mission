package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Message;
import static com.sprint.mission.discodeit.entity.Message.msgNumber;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.System.currentTimeMillis;

public class MessageService {
    //메세지 목록을 저장할 MAP 생성
    public static Map<Integer, Message> messageMap = new HashMap<>();
    // Key : 메세지넘버(int msgNumber) , Value : 메세지내용(msgText)
    public static void sendMessage(String nMessage) {
         String nowTime = new SimpleDateFormat("HH:mm:ss").format(currentTimeMillis());
        messageMap.put(msgNumber++,new Message(List.of("Sender1",nMessage),List.of(nowTime,nowTime)));
    }
    public static void readAllMessage() {
        //메세지 목록 MAP 전체 불러오기
        System.out.println(" 입력된 메세지는 아래와 같습니다. \n 글번호 | 작성자 , 메세지 , 작성시간 , 수정시간");
        for (Map.Entry<Integer, Message> entry : messageMap.entrySet()) {
            int msgNumber = entry.getKey();
            Message msgBody = entry.getValue();
            System.out.println(msgNumber + " | " + msgBody);
        }
    }
}
