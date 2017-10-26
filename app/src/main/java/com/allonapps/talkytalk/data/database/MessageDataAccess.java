package com.allonapps.talkytalk.data.database;

import com.allonapps.talkytalk.data.entity.Message;

import java.util.List;

/**
 * Created by michael on 10/26/17.
 */

public abstract class MessageDataAccess {

    // this interface is purely so that we can swap out the Room database with our own implementation
    // easier.  There's probably a better place to put something like this within the package structure

    public abstract List<Message> getAllMessages();

    public abstract void saveAllMessages(List<Message> messageList);
}