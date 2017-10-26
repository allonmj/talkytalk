package com.allonapps.talkytalk.data.entity.transformer;

import com.allonapps.talkytalk.data.Transformer;
import com.allonapps.talkytalk.data.entity.User;

import java.util.Map;

/**
 * Created by michael on 10/25/17.
 */

public class UserTransformer extends Transformer<Map<String, Object>, User> {
    @Override
    public TransformResult<User> transform(Map<String, Object> from) {
        if (from == null) {
            return new TransformResult.Builder<User>().invalidResult();
        }

        String userName = "";
        if (from.containsKey("name")) {
            userName = (String) from.get("name");
        }

        if (userName.isEmpty()) {
            return new TransformResult.Builder<User>().invalidResult();
        }

        User user = new User(userName);
        return new TransformResult.Builder<User>().validResult(user);
    }
}