package com.allonapps.talkytalk.ui.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.allonapps.talkytalk.R;
import com.allonapps.talkytalk.data.entity.Message;
import com.allonapps.talkytalk.ui.adapter.MessagesAdapter;
import com.allonapps.talkytalk.ui.injection.rolled.RolledPresenterFactory;
import com.allonapps.talkytalk.ui.presentation.TalkPresentation;
import com.allonapps.talkytalk.ui.presenter.TalkPresenter;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by michael on 10/14/17.
 */

public class TalkActivity extends AppCompatActivity implements TalkPresentation {

    @BindView(R.id.rvMessages)
    RecyclerView rvMessages;

    @BindView(R.id.etSend)
    EditText etSend;

    @BindView(R.id.ivSend)
    ImageView ivSend;

    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    private MessagesAdapter messagesAdapter;

    private TalkPresenter talkPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_talk);
        ButterKnife.bind(this);

        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        rvMessages.setLayoutManager(layoutManager);

        messagesAdapter = new MessagesAdapter(this);
        rvMessages.setAdapter(messagesAdapter);

        Toolbar toolbar = findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getResources().getString(R.string.talk));

        Bundle bundle = getIntent().getExtras();
        String userName = bundle.getString("userName");

        talkPresenter = RolledPresenterFactory.createTalkPresenter(this, userName);

        talkPresenter.onCreateView(this);

        etSend.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                talkPresenter.onMessageTextChanged(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        ivSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                talkPresenter.onSendMessageClick();
            }
        });
    }

    @Override
    public void showMessageList(List<Message> messageList) {
        messagesAdapter.setMessageList(messageList);
        messagesAdapter.notifyDataSetChanged();
        rvMessages.scrollToPosition(messageList.size() - 1);
    }

    @Override
    public void showLoadMessagesError() {
        Toast.makeText(this, "error loading messages", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showInvalidMessage() {
        Toast.makeText(this, "must be 1 - 128 chars", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showProgress(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    @Override
    public void showSendMessageError() {
        Toast.makeText(this, "error sending message", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void clearMessageField() {
        etSend.setText("");
    }

    @Override
    public void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(etSend.getWindowToken(), 0);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}