package com.allonapps.talkytalk.data;

import android.support.annotation.Nullable;

/**
 * Created by michael on 10/14/17.
 */

public interface Receiver<S, E, Q> {

    void onSuccess(@Nullable Q query, S success);

    void onError(@Nullable Q query, E error);

}