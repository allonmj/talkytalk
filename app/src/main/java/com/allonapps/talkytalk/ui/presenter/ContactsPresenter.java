package com.allonapps.talkytalk.ui.presenter;

import android.support.annotation.NonNull;

import com.allonapps.talkytalk.data.Response;
import com.allonapps.talkytalk.data.async.rolled.AsyncManager;
import com.allonapps.talkytalk.data.async.rolled.AsyncUtils;
import com.allonapps.talkytalk.data.async.rxjava.RxRepository;
import com.allonapps.talkytalk.data.entity.CurrentUser;
import com.allonapps.talkytalk.data.entity.User;
import com.allonapps.talkytalk.data.error.CurrentUserError;
import com.allonapps.talkytalk.data.error.UserError;
import com.allonapps.talkytalk.data.query.CurrentUserQuery;
import com.allonapps.talkytalk.data.query.UserQuery;
import com.allonapps.talkytalk.data.repository.Repository;
import com.allonapps.talkytalk.ui.presentation.ContactsPresentation;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by michael on 10/14/17.
 */

public class ContactsPresenter extends LifecyclePresenter<ContactsPresentation> {

    private final Repository<CurrentUser, CurrentUserError, CurrentUserQuery> currentUserRepository;
    private final Repository<User, UserError, UserQuery> userRepository;


    private String userName;

    public ContactsPresenter(Repository<CurrentUser, CurrentUserError, CurrentUserQuery> currentUserRepository,
                             Repository<User, UserError, UserQuery> userRepository) {
        this.currentUserRepository = currentUserRepository;
        this.userRepository = userRepository;
    }

    @Override
    protected void afterCreateView() {
        // uncomment either of these functions to use the rxjava version or the one we implemented

        loadContactsRxJava();

//        loadContactsRolled();
    }

    private void loadContactsRxJava() {

        Flowable<List<CurrentUser>> currentUserFlowable = new RxRepository<>(currentUserRepository).createFlowable(new CurrentUserQuery());

        final Flowable<List<User>> otherUserFlowable = new RxRepository<>(userRepository)
                .createFlowable(new UserQuery());

        currentUserFlowable.subscribeOn(Schedulers.newThread())
                .flatMap(new Function<List<CurrentUser>, Flowable<List<User>>>() {
                    @Override
                    public Flowable<List<User>> apply(@NonNull List<CurrentUser> currentUsers) throws Exception {
                        userName = currentUsers.get(0).userName;
                        return otherUserFlowable;
                    }
                }).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<User>>() {
                    @Override
                    public void accept(List<User> users) throws Exception {
                        view.showCurrentUserName(userName);
                        view.showContacts(users);
                    }
                });
    }

    private void loadContactsRolled() {
        final AsyncUtils.RolledFlow<User, UserError, UserQuery> userRolledFlow =
                AsyncUtils.RolledFlow.create(new AsyncManager(), userRepository)
                        .respondOnUiThread(true)
                        .success(new AsyncUtils.RolledFlow.SuccessListener<User>() {
                            @Override
                            public void onSuccess(List<User> success) {
                                view.showCurrentUserName(userName);
                                view.showContacts(success);
                            }
                        });

        AsyncUtils.RolledFlow<CurrentUser, CurrentUserError, CurrentUserQuery> rolledFlow =
                AsyncUtils.RolledFlow.create(new AsyncManager(), currentUserRepository)
                        .success(new AsyncUtils.RolledFlow.SuccessListener<CurrentUser>() {
                            @Override
                            public void onSuccess(List<CurrentUser> success) {
                                userName = success.get(0).userName;
                            }
                        })
                        .then(new AsyncUtils.RolledFlow.RolledFlowQueryCreator<CurrentUser, CurrentUserError>() {
                            @Override
                            public AsyncUtils.RolledFlowQuery createForResponse(Response<List<CurrentUser>, CurrentUserError> response) {
                                return new AsyncUtils.RolledFlowQuery(userRolledFlow, new UserQuery());
                            }
                        })
                        .respondOnUiThread(false);

        rolledFlow.run(new CurrentUserQuery());
    }

    public void onTalkClick() {
        if (userName == null || userName.isEmpty()) {
            return;
        }
        view.navigateToTalk(userName);
    }
}