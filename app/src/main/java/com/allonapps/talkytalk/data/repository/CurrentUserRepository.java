package com.allonapps.talkytalk.data.repository;

import android.support.annotation.NonNull;

import com.allonapps.talkytalk.data.Receiver;
import com.allonapps.talkytalk.data.entity.CurrentUser;
import com.allonapps.talkytalk.data.error.CurrentUserError;
import com.allonapps.talkytalk.data.query.CurrentUserQuery;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by michael on 10/14/17.
 */

public class CurrentUserRepository implements Repository<CurrentUser, CurrentUserError, CurrentUserQuery> {

    private final FirebaseAuth firebaseAuth;
    private final FirebaseDatabase firebaseDatabase;

    public CurrentUserRepository(FirebaseAuth firebaseAuth, FirebaseDatabase firebaseDatabase) {
        this.firebaseAuth = firebaseAuth;
        this.firebaseDatabase = firebaseDatabase;
    }


    @Override
    public void query(CurrentUserQuery query, Receiver<List<CurrentUser>, CurrentUserError, CurrentUserQuery> receiver) {
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser != null) {
            fetchUserName(query, firebaseUser.getUid(), receiver);
        } else {
            receiver.onError(query, new CurrentUserError());
        }
    }

    private void fetchUserName(final CurrentUserQuery query, final String userId, final Receiver<List<CurrentUser>, CurrentUserError, CurrentUserQuery> receiver) {
        DatabaseReference databaseReference = firebaseDatabase.getReference("users/" + userId + "/name");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String userName = dataSnapshot.getValue(String.class);
                List<CurrentUser> currentUserList = new ArrayList<CurrentUser>();
                currentUserList.add(new CurrentUser(null, userName));
                receiver.onSuccess(query, currentUserList);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                receiver.onError(query, new CurrentUserError());
            }
        });
    }

    @Override
    public void update(final CurrentUser toUpdate, final Receiver<CurrentUser, CurrentUserError, CurrentUserQuery> receiver) {
        firebaseAuth.signInWithCredential(toUpdate.authCredential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful() && firebaseAuth.getCurrentUser() != null) {
                            String userId = firebaseAuth.getCurrentUser().getUid();
                            updateUserName(userId, toUpdate, receiver);
                        } else {
                            receiver.onError(null, new CurrentUserError());
                        }
                    }
                });
    }

    private void updateUserName(final String userId, final CurrentUser toUpdate, final Receiver<CurrentUser, CurrentUserError, CurrentUserQuery> receiver) {
        DatabaseReference databaseReference = firebaseDatabase.getReference("users/" + userId + "/name");
        databaseReference.setValue(toUpdate.userName).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    receiver.onSuccess(null, toUpdate);
                } else {
                    receiver.onError(null, new CurrentUserError());
                }
            }
        });
    }

    @Override
    public void delete(CurrentUser toDelete, Receiver<CurrentUser, CurrentUserError, CurrentUserQuery> receiver) {

    }
}