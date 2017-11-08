package com.shashankchamoli.tango;

/**
 * Created by sam on 27-07-2017.
 */

public class messagereceiver {
    String text;
    String from;
    String time;
    messagereceiver() {}

    public messagereceiver(String text, String from, String time) {
        this.text = text;
        this.from = from;
        this.time = time;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }



    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }



}
