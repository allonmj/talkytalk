package com.allonapps.talkytalk.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.allonapps.talkytalk.ui.injection.rolled.RolledPresenterFactory;
import com.allonapps.talkytalk.ui.presentation.SplashPresentation;
import com.allonapps.talkytalk.ui.presenter.SplashPresenter;

/**
 * Created by michael on 10/25/17.
 */

public class SplashActivity extends AppCompatActivity implements SplashPresentation {

    private SplashPresenter splashPresenter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        splashPresenter = RolledPresenterFactory.createSplashPresenter();

        splashPresenter.onCreateView(this);
    }

    @Override
    public void navigateToContacts() {
        Intent intent = new Intent(this, ContactsActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void navigateToSignUp() {
        Intent intent = new Intent(this, SignUpActivity.class);
        startActivity(intent);
        finish();
    }
}