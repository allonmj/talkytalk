package com.allonapps.talkytalk.data.database.rolled;

import com.allonapps.talkytalk.data.entity.Message;
import com.allonapps.talkytalk.data.query.MessageQuery;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by michael on 10/26/17.
 */

public class MessageSchemaObject extends DatabaseSchemaObject<Message, MessageQuery> {

    private static final String MESSAGE_ID = "messageId";
    private static final String USER_NAME = "userName";
    private static final String MESSAGE_TEXT = "messageText";
    private static final String MESSAGE_DATE = "messageDate";

    @Override
    protected String getDatabaseName() {
        return "Message";
    }

    @Override
    public String getPrimaryKeyColumn() {
        return MESSAGE_ID;
    }

    @Override
    public List<Data> getDataColumns() {
        List<Data> dataList = new ArrayList<>();
        dataList.add(new Data(USER_NAME, Data.DataType.STRING));
        dataList.add(new Data(MESSAGE_TEXT, Data.DataType.STRING));
        dataList.add(new Data(MESSAGE_DATE, Data.DataType.LONG));
        return dataList;
    }

    @Override
    protected Object getFieldObject(Data data, Message message) {
        if (MESSAGE_ID.equals(data.name)) {
            return getUniqueId(message);
        } else if (USER_NAME.equals(data.name)) {
            return message.userName;
        } else if (MESSAGE_TEXT.equals(data.name)) {
            return message.messageText;
        } else if (MESSAGE_DATE.equals(data.name)) {
            return message.messageDate;
        }
        return null;
    }

    @Override
    protected Message createItem(Map<Data, Object> objectMap) {
        String userName = "";
        String messageText = "";
        long messageDate = -1l;
        String messageId = "";

        for (Map.Entry<Data, Object> entry : objectMap.entrySet()) {
            Data data = entry.getKey();
            Object value = entry.getValue();

            if (MESSAGE_ID.equals(data.name)) {
                messageId = (String) value;
            } else if (USER_NAME.equals(data.name)) {
                userName = (String) value;
            } else if (MESSAGE_TEXT.equals(data.name)) {
                messageText = (String) value;
            } else if (MESSAGE_DATE.equals(data.name)) {
                messageDate = (long) value;
            }
        }


        return new Message(userName, messageText, messageDate, messageId);
    }

    @Override
    protected String getUniqueId(Message object) {
        return object.messageId;
    }
}