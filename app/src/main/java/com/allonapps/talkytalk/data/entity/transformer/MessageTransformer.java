package com.allonapps.talkytalk.data.entity.transformer;

import com.allonapps.talkytalk.data.Transformer;
import com.allonapps.talkytalk.data.entity.Message;

import java.util.Map;

/**
 * Created by michael on 10/25/17.
 */

public class MessageTransformer extends Transformer<Map<String, Object>, Message> {
    @Override
    public TransformResult<Message> transform(Map<String, Object> from) {
        if (from == null) {
            return new TransformResult.Builder<Message>().invalidResult();
        }

        String userName = "";
        if (from.containsKey("userName")) {
            userName = (String) from.get("userName");
        }

        String text = "";
        if (from.containsKey("messageText")) {
            text = (String) from.get("messageText");
        }

        long time = -1;
        if (from.containsKey("messageDate")) {
            time = (long) from.get("messageDate");
        }

        if (userName.isEmpty() || text.isEmpty() || time == -1) {
            return new TransformResult.Builder<Message>().invalidResult();
        }

        String messageId = createMessageId(userName, time);

        Message message = new Message(userName, text, time, messageId);

        return new TransformResult.Builder<Message>().validResult(message);
    }

    private String createMessageId(String userName, long messageDate) {
        // create a unique-ish id.  Don't do this in your apps!  Instead we should return a unique id from backend
        return userName + String.valueOf(messageDate);
    }
}