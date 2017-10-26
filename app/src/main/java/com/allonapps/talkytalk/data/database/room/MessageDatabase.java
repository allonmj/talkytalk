package com.allonapps.talkytalk.data.database.room;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import com.allonapps.talkytalk.data.entity.Message;

/**
 * Created by michael on 10/25/17.
 */

@Database(entities = {Message.class}, version = 1)
public abstract class MessageDatabase extends RoomDatabase {
    public abstract MessageDao messageDao();
}