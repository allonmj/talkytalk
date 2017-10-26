package com.allonapps.talkytalk.data.entity;

import android.support.annotation.Nullable;

import com.google.firebase.auth.AuthCredential;

/**
 * Created by michael on 10/14/17.
 */

public class CurrentUser {

    public final
    @Nullable
    AuthCredential authCredential;

    public final String userName;

    public CurrentUser(AuthCredential authCredential, String userName) {
        this.authCredential = authCredential;
        this.userName = userName;
    }

}