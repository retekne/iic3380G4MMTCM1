package com.quelves.iic3380g4mmtcm1.model;

import java.util.UUID;

public class ChatMessage {
    private String mAuthor;
    private String mMessage;
    private String mUuid;

    // Java objects used in firebase realtime database must declare an empty constructor
    public ChatMessage() {}

    public ChatMessage(String author, String message) {
        this(author, message, UUID.randomUUID().toString());
    }

    public ChatMessage(String author, String message, String uuid) {
        mAuthor = author;
        mMessage = message;
        mUuid = uuid;
    }

    public String getMessage() {
        return mMessage;
    }

    public void setMessage(String message) {
        mMessage = message;
    }

    public String getAuthor() {
        return mAuthor;
    }

    public void setAuthor(String author) {
        mAuthor = author;
    }

    public String getUuid() {
        return mUuid;
    }

    public void setUuid(String uuid) {
        mUuid = uuid;
    }

    @Override
    public String toString() {
        return mMessage;
    }
}
