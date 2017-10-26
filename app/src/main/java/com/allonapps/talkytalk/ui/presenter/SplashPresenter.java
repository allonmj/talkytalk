package com.allonapps.talkytalk.ui.presenter;

import android.support.annotation.Nullable;

import com.allonapps.talkytalk.data.Receiver;
import com.allonapps.talkytalk.data.entity.CurrentUser;
import com.allonapps.talkytalk.data.error.CurrentUserError;
import com.allonapps.talkytalk.data.query.CurrentUserQuery;
import com.allonapps.talkytalk.data.repository.Repository;
import com.allonapps.talkytalk.ui.presentation.SplashPresentation;

import java.util.List;

/**
 * Created by michael on 10/25/17.
 */

public class SplashPresenter extends LifecyclePresenter<SplashPresentation> implements Receiver<List<CurrentUser>, CurrentUserError, CurrentUserQuery> {

    private final Repository<CurrentUser, CurrentUserError, CurrentUserQuery> currentUserRepository;

    public SplashPresenter(Repository<CurrentUser, CurrentUserError, CurrentUserQuery> currentUserRepository) {
        this.currentUserRepository = currentUserRepository;
    }

    @Override
    protected void afterCreateView() {
        // TODO: this operation has the potential to take too long for a splash screen - consider caching
        currentUserRepository.query(new CurrentUserQuery(), this);
    }

    @Override
    public void onSuccess(@Nullable CurrentUserQuery query, List<CurrentUser> success) {
        view.navigateToContacts();
    }

    @Override
    public void onError(@Nullable CurrentUserQuery query, CurrentUserError error) {
        view.navigateToSignUp();
    }
}