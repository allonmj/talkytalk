package com.allonapps.talkytalk.ui.presentation;

import com.allonapps.talkytalk.data.entity.Message;

import java.util.List;

/**
 * Created by michael on 10/14/17.
 */

public interface TalkPresentation {
    void showMessageList(List<Message> messageList);

    void showLoadMessagesError();

    void showInvalidMessage();

    void showProgress(boolean show);

    void showSendMessageError();

    void clearMessageField();

    void hideKeyboard();
}