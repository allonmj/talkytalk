package com.allonapps.talkytalk.ui;

import android.app.Application;

import com.allonapps.talkytalk.ui.injection.dagger.ContactsComponent;
import com.allonapps.talkytalk.ui.injection.dagger.DaggerContactsComponent;

/**
 * Created by michael on 10/25/17.
 */

public class TalkyTalkApplication extends Application {

    private ContactsComponent contactsComponent;

    @Override
    public void onCreate() {
        super.onCreate();

        contactsComponent = DaggerContactsComponent.builder()
                .build();
    }

    public ContactsComponent getContactsComponent() {
        return contactsComponent;
    }

}