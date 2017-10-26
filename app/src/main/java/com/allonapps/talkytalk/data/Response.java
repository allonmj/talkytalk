package com.allonapps.talkytalk.data;

/**
 * Created by michael on 10/14/17.
 */

public class Response<S, E> {

    public final S response;

    public final E error;

    public Response(S response, E error) {
        this.response = response;
        this.error = error;
    }

}