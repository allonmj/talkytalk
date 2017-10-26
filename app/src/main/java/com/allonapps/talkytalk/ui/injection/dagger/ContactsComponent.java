package com.allonapps.talkytalk.ui.injection.dagger;

import com.allonapps.talkytalk.ui.activity.ContactsActivity;

import dagger.Component;

/**
 * Created by michael on 10/25/17.
 */

@Component(modules = {ContactsModule.class})
public interface ContactsComponent {

    void inject(ContactsActivity contactsActivity);

}