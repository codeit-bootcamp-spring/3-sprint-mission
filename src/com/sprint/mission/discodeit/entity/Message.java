package com.sprint.mission.discodeit.entity;

import java.util.List;

public class Message {
    public static int msgNumber = 0;
    List<String> msgs;
    List<String> msgTstamps;



    public Message(List<String> msgs, List<String> msgTstamps) {
        this.msgs = msgs;
        this.msgTstamps = msgTstamps;
    }

    @Override
    public String toString() { return "" + msgs + msgTstamps; }
}

