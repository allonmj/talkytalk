package com.allonapps.talkytalk.ui.injection.rolled;

import android.arch.persistence.room.Room;
import android.content.Context;

import com.allonapps.talkytalk.data.database.MessageDataAccess;
import com.allonapps.talkytalk.data.database.rolled.DiskDatabase;
import com.allonapps.talkytalk.data.database.rolled.MessageSchemaObject;
import com.allonapps.talkytalk.data.database.room.MessageDatabase;
import com.allonapps.talkytalk.data.entity.CurrentUser;
import com.allonapps.talkytalk.data.entity.Message;
import com.allonapps.talkytalk.data.entity.User;
import com.allonapps.talkytalk.data.error.CurrentUserError;
import com.allonapps.talkytalk.data.error.MessageError;
import com.allonapps.talkytalk.data.error.UserError;
import com.allonapps.talkytalk.data.query.CurrentUserQuery;
import com.allonapps.talkytalk.data.query.MessageQuery;
import com.allonapps.talkytalk.data.query.UserQuery;
import com.allonapps.talkytalk.data.repository.CurrentUserRepository;
import com.allonapps.talkytalk.data.repository.MessageRepository;
import com.allonapps.talkytalk.data.repository.Repository;
import com.allonapps.talkytalk.data.repository.UserRepository;
import com.allonapps.talkytalk.ui.presenter.ContactsPresenter;
import com.allonapps.talkytalk.ui.presenter.SignUpPresenter;
import com.allonapps.talkytalk.ui.presenter.SplashPresenter;
import com.allonapps.talkytalk.ui.presenter.TalkPresenter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

/**
 * Created by michael on 10/26/17.
 */

public class RolledPresenterFactory {

    private static FirebaseAuth createFirebaseAuth() {
        return FirebaseAuth.getInstance();
    }

    private static FirebaseDatabase createFirebaseDatabase() {
        return FirebaseDatabase.getInstance();
    }

    private static MessageDatabase createRoomMessageDatabase(Context context) {
        return Room.databaseBuilder(context.getApplicationContext(), MessageDatabase.class, "message-db")
                .build();
    }

    private static DiskDatabase<Message, MessageQuery> createRolledMessageDatabase(Context context) {
        return new DiskDatabase<>(context.getApplicationContext(), "message-db-rolled", 1, new MessageSchemaObject());
    }

    private static MessageDataAccess createRoomMessageDataAccess(final Context context) {
        return new MessageDataAccess() {

            private final MessageDatabase messageDatabase = createRoomMessageDatabase(context);

            @Override
            public List<Message> getAllMessages() {
                return messageDatabase.messageDao().getAllMessages();
            }

            @Override
            public void saveAllMessages(List<Message> messageList) {
                messageDatabase.messageDao().deleteAllMessages();
                messageDatabase.messageDao().addAllMessages(messageList);
            }
        };
    }

    private static MessageDataAccess createRolledMessageDataAccess(final Context context) {
        return new MessageDataAccess() {

            private final DiskDatabase<Message, MessageQuery> messageDatabase = createRolledMessageDatabase(context);

            @Override
            public List<Message> getAllMessages() {
                return messageDatabase.getItemList(new MessageQuery());
            }

            @Override
            public void saveAllMessages(List<Message> messageList) {
                messageDatabase.flush();
                messageDatabase.updateItemList(messageList);
            }
        };
    }

    private static Repository<CurrentUser, CurrentUserError, CurrentUserQuery> createCurrentUserRepository() {
        return new CurrentUserRepository(createFirebaseAuth(), createFirebaseDatabase());
    }

    private static Repository<User, UserError, UserQuery> createUserRepository() {
        return new UserRepository(createFirebaseDatabase());
    }

    private static Repository<Message, MessageError, MessageQuery> createMessageRepository() {
        return new MessageRepository(createFirebaseDatabase());
    }

    public static ContactsPresenter createContactsPresenter() {
        return new ContactsPresenter(createCurrentUserRepository(), createUserRepository());
    }

    public static SignUpPresenter createSignUpPresenter() {
        return new SignUpPresenter(createCurrentUserRepository());
    }

    public static SplashPresenter createSplashPresenter() {
        return new SplashPresenter(createCurrentUserRepository());
    }

    public static TalkPresenter createTalkPresenter(Context context, String userName) {
        return new TalkPresenter(userName, createMessageRepository(), createRoomMessageDataAccess(context));
    }
}