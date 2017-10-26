package com.allonapps.talkytalk.ui.presentation;

import com.allonapps.talkytalk.data.entity.User;

import java.util.List;

/**
 * Created by michael on 10/14/17.
 */

public interface ContactsPresentation {
    void showContacts(List<User> userList);

    void navigateToTalk(String userName);

    void showCurrentUserName(String userName);

    void navigateToSignup();
}