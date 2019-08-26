package com.example.demo;

public class MessageEvent {

    String message ;

    public MessageEvent(String message){

        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
