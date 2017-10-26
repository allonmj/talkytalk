package com.allonapps.talkytalk.data.database.room;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.allonapps.talkytalk.data.entity.Message;

import java.util.List;

/**
 * Created by michael on 10/25/17.
 */

@Dao
public interface MessageDao {

    @Query("SELECT * FROM message")
    List<Message> getAllMessages();

    @Query("DELETE FROM message")
    void deleteAllMessages();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void addAllMessages(List<Message> messageList);

}