package com.shashankchamoli.tango;

/**
 * Created by sam on 19-07-2017.
 */
public class ChatBubble {

    private String content;
    private boolean myMessage;
    String time;

    public ChatBubble(String content, boolean myMessage, String time) {
        this.content = content;
        this.myMessage = myMessage;
        this.time = time;
    }

    public String getContent() {
        return content;
    }

    public boolean myMessage() {
        return myMessage;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}