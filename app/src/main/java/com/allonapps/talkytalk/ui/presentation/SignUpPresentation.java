package com.allonapps.talkytalk.ui.presentation;

/**
 * Created by michael on 10/13/17.
 */

public interface SignUpPresentation {

    void showSignUpEnabled(boolean enabled);

    void navigateToContacts();

    void requestAuthentication();

    void showSignUpError();
}