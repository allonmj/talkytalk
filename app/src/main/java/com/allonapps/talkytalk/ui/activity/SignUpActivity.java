package com.allonapps.talkytalk.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.allonapps.talkytalk.R;
import com.allonapps.talkytalk.ui.injection.rolled.RolledPresenterFactory;
import com.allonapps.talkytalk.ui.presentation.SignUpPresentation;
import com.allonapps.talkytalk.ui.presenter.SignUpPresenter;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.GoogleAuthProvider;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SignUpActivity extends AppCompatActivity implements SignUpPresentation, GoogleApiClient.OnConnectionFailedListener {

    @BindView(R.id.etUserName)
    EditText etUserName;

    @BindView(R.id.buttonSignUp)
    SignInButton buttonSignUp;

    private static final int RC_SIGN_IN = 9001;

    private SignUpPresenter signUpPresenter;

    private GoogleApiClient googleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        ButterKnife.bind(this);

        signUpPresenter = RolledPresenterFactory.createSignUpPresenter();

        signUpPresenter.onCreateView(this);

        etUserName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                signUpPresenter.onUserNameChanged(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        buttonSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUpPresenter.onSignUpClick();
            }
        });

        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, googleSignInOptions)
                .build();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult googleSignInResult = Auth.GoogleSignInApi.getSignInResultFromIntent(data);

            if (googleSignInResult.isSuccess()) {
                GoogleSignInAccount account = googleSignInResult.getSignInAccount();
                AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
                signUpPresenter.onAuthenticationGranted(credential);
            }
        }
    }

    @Override
    public void showSignUpEnabled(boolean enabled) {
        buttonSignUp.setEnabled(enabled);
    }

    @Override
    public void navigateToContacts() {
        Intent intent = new Intent(this, ContactsActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void requestAuthentication() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void showSignUpError() {
        Toast.makeText(this, "Error signing up", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }
}