package com.allonapps.talkytalk.ui.injection.dagger;

import com.allonapps.talkytalk.data.entity.CurrentUser;
import com.allonapps.talkytalk.data.entity.User;
import com.allonapps.talkytalk.data.error.CurrentUserError;
import com.allonapps.talkytalk.data.error.UserError;
import com.allonapps.talkytalk.data.query.CurrentUserQuery;
import com.allonapps.talkytalk.data.query.UserQuery;
import com.allonapps.talkytalk.data.repository.CurrentUserRepository;
import com.allonapps.talkytalk.data.repository.Repository;
import com.allonapps.talkytalk.data.repository.UserRepository;
import com.allonapps.talkytalk.ui.presenter.ContactsPresenter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import dagger.Module;
import dagger.Provides;

/**
 * Created by michael on 10/25/17.
 */

@Module
public class ContactsModule {

    @Provides
    static FirebaseDatabase provideFirebaseDatabase() {
        return FirebaseDatabase.getInstance();
    }

    @Provides
    static Repository<User, UserError, UserQuery> provideUserRepository(FirebaseDatabase firebaseDatabase) {
        return new UserRepository(firebaseDatabase);
    }

    @Provides
    static FirebaseAuth provideFirebaseAuth() {
        return FirebaseAuth.getInstance();
    }

    @Provides
    static Repository<CurrentUser, CurrentUserError, CurrentUserQuery> provideCurrentUserRepository(FirebaseAuth firebaseAuth, FirebaseDatabase firebaseDatabase) {
        return new CurrentUserRepository(firebaseAuth, firebaseDatabase);
    }

    @Provides
    static ContactsPresenter provideContactsPresenter(Repository<CurrentUser, CurrentUserError, CurrentUserQuery> currentUserRepository,
                                                      Repository<User, UserError, UserQuery> userRepository) {
        return new ContactsPresenter(currentUserRepository, userRepository);
    }

}