package com.allonapps.talkytalk.ui.presenter;

import android.support.annotation.Nullable;

import com.allonapps.talkytalk.data.Receiver;
import com.allonapps.talkytalk.data.entity.CurrentUser;
import com.allonapps.talkytalk.data.error.CurrentUserError;
import com.allonapps.talkytalk.data.query.CurrentUserQuery;
import com.allonapps.talkytalk.data.repository.Repository;
import com.allonapps.talkytalk.ui.presentation.SignUpPresentation;
import com.google.firebase.auth.AuthCredential;

/**
 * Created by michael on 10/13/17.
 */

public class SignUpPresenter extends LifecyclePresenter<SignUpPresentation> implements Receiver<CurrentUser, CurrentUserError, CurrentUserQuery> {

    private String userName;
    private final Repository<CurrentUser, CurrentUserError, CurrentUserQuery> currentUserRepository;

    public SignUpPresenter(Repository<CurrentUser, CurrentUserError, CurrentUserQuery> currentUserRepository) {
        this.currentUserRepository = currentUserRepository;
    }

    @Override
    protected void afterCreateView() {
        view.showSignUpEnabled(isUserNameValid());
    }

    public void onUserNameChanged(String userName) {
        this.userName = userName;
        view.showSignUpEnabled(isUserNameValid());
    }

    public void onSignUpClick() {
        view.requestAuthentication();
    }

    private boolean isUserNameValid() {
        return userName != null
                && userName.length() >= 5;
    }

    public void onAuthenticationGranted(AuthCredential credential) {
        currentUserRepository.update(new CurrentUser(credential, userName), this);
    }

    @Override
    public void onSuccess(@Nullable CurrentUserQuery query, CurrentUser success) {
        view.navigateToContacts();
    }

    @Override
    public void onError(@Nullable CurrentUserQuery query, CurrentUserError error) {
        view.showSignUpError();
    }
}