package com.allonapps.talkytalk.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.allonapps.talkytalk.R;
import com.allonapps.talkytalk.data.entity.User;
import com.allonapps.talkytalk.ui.TalkyTalkApplication;
import com.allonapps.talkytalk.ui.adapter.ContactsAdapter;
import com.allonapps.talkytalk.ui.presentation.ContactsPresentation;
import com.allonapps.talkytalk.ui.presenter.ContactsPresenter;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by michael on 10/14/17.
 */

public class ContactsActivity extends AppCompatActivity implements ContactsPresentation {

    @BindView(R.id.rvContacts)
    RecyclerView rvContacts;

    @BindView(R.id.tvUserName)
    TextView tvUserName;

    @BindView(R.id.buttonTalk)
    Button buttonTalk;

    private ContactsAdapter contactsAdapter;

    @Inject
    ContactsPresenter contactsPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);
        ButterKnife.bind(this);

        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        rvContacts.setLayoutManager(layoutManager);

        Toolbar toolbar = findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getResources().getString(R.string.contacts));

        contactsAdapter = new ContactsAdapter();
        rvContacts.setAdapter(contactsAdapter);


        // Inject presenter with Dagger
        TalkyTalkApplication talkyTalkApplication = (TalkyTalkApplication) getApplication();
        talkyTalkApplication.getContactsComponent().inject(this);

        contactsPresenter.onCreateView(this);

        buttonTalk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                contactsPresenter.onTalkClick();
            }
        });

    }

    @Override
    public void showContacts(List<User> userList) {
        contactsAdapter.setUserList(userList);
        contactsAdapter.notifyDataSetChanged();
    }

    @Override
    public void navigateToTalk(String userName) {
        Intent intent = new Intent(this, TalkActivity.class);
        intent.putExtra("userName", userName);
        startActivity(intent);
    }

    @Override
    public void showCurrentUserName(String userName) {
        tvUserName.setText(userName);
    }

    @Override
    public void navigateToSignup() {
        Intent intent = new Intent(this, SignUpActivity.class);
        startActivity(intent);
        finish();
    }
}