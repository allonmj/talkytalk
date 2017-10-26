package com.allonapps.talkytalk.data.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

/**
 * Created by michael on 10/14/17.
 */

@Entity
public class Message {

    @ColumnInfo(name = "userName")
    public final String userName;

    @ColumnInfo(name = "messageText")
    public final String messageText;

    @ColumnInfo(name = "messageDate")
    public final long messageDate;

    @PrimaryKey
    public final String messageId;

    public Message(String userName, String messageText, long messageDate, String messageId) {
        this.userName = userName;
        this.messageText = messageText;
        this.messageDate = messageDate;
        this.messageId = messageId;
    }
}