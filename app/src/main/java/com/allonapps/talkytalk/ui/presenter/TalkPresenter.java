package com.allonapps.talkytalk.ui.presenter;

import android.support.annotation.Nullable;

import com.allonapps.talkytalk.data.Receiver;
import com.allonapps.talkytalk.data.async.rxjava.RxRepository;
import com.allonapps.talkytalk.data.database.MessageDataAccess;
import com.allonapps.talkytalk.data.entity.Message;
import com.allonapps.talkytalk.data.error.MessageError;
import com.allonapps.talkytalk.data.query.MessageQuery;
import com.allonapps.talkytalk.data.repository.Repository;
import com.allonapps.talkytalk.ui.presentation.TalkPresentation;

import java.util.List;
import java.util.concurrent.Callable;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by michael on 10/14/17.
 */

public class TalkPresenter extends LifecyclePresenter<TalkPresentation> {

    private final Repository<Message, MessageError, MessageQuery> messageRepository;

    private final MessageDataAccess messageDataAccess;

    private final String userName;

    private String messageText = "";

    private boolean sendingMessage;

    public TalkPresenter(final String userName,
                         Repository<Message, MessageError, MessageQuery> messageRepository,
                         MessageDataAccess messageDataAccess) {
        this.userName = userName;
        this.messageRepository = messageRepository;
        this.messageDataAccess = messageDataAccess;
    }

    @Override
    protected void afterCreateView() {
        view.showProgress(true);

        loadMessages();
    }

    private void loadMessages() {
        final Flowable<List<Message>> liveMessageFlowable = new RxRepository<Message, MessageError, MessageQuery>(messageRepository)
                .createFlowable(new MessageQuery());

        Flowable<List<Message>> cachedMessageFlowable = Flowable.fromCallable(
                new Callable<List<Message>>() {
                    @Override
                    public List<Message> call() throws Exception {
                        List<Message> messageList = messageDataAccess.getAllMessages();
                        return messageList;
                    }
                }
        );

        cachedMessageFlowable.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<Message>>() {
                    @Override
                    public void accept(List<Message> messageList) throws Exception {
                        view.showMessageList(messageList);
                    }
                });

        liveMessageFlowable.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnError(new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        view.showProgress(false);
                        view.showLoadMessagesError();
                    }
                })
                .subscribe(new Consumer<List<Message>>() {
                    @Override
                    public void accept(List<Message> messageList) throws Exception {
                        updateMessageDatabase(messageList);
                        view.showProgress(false);
                        view.showMessageList(messageList);
                    }
                });
    }

    private void updateMessageDatabase(final List<Message> messageList) {
        Flowable.fromCallable(new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                messageDataAccess.saveAllMessages(messageList);
                return new Object();
            }
        }).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) throws Exception {
                    }
                });
    }

    public void onMessageTextChanged(String messageText) {
        this.messageText = messageText;
    }

    private boolean isMessageValid(String message) {
        return message != null && !message.isEmpty() && message.length() <= 128;
    }

    public void onSendMessageClick() {
        if (sendingMessage) {
            return;
        }

        view.hideKeyboard();

        if (!isMessageValid(messageText)) {
            view.showInvalidMessage();
            return;
        }
        view.showProgress(true);
        sendingMessage = true;
        messageRepository.update(new Message(userName, messageText, System.currentTimeMillis(), ""), sendMessageReceiver);
    }

    private Receiver<Message, MessageError, MessageQuery> sendMessageReceiver = new Receiver<Message, MessageError, MessageQuery>() {
        @Override
        public void onSuccess(@Nullable MessageQuery query, Message success) {
            sendingMessage = false;
            view.clearMessageField();
            view.showProgress(false);
        }

        @Override
        public void onError(@Nullable MessageQuery query, MessageError error) {
            sendingMessage = false;
            view.showSendMessageError();
            view.showProgress(false);
        }
    };
}