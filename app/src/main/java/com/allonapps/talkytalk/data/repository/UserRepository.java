package com.allonapps.talkytalk.data.repository;

import com.allonapps.talkytalk.data.Receiver;
import com.allonapps.talkytalk.data.Transformer;
import com.allonapps.talkytalk.data.entity.User;
import com.allonapps.talkytalk.data.entity.transformer.UserTransformer;
import com.allonapps.talkytalk.data.error.UserError;
import com.allonapps.talkytalk.data.query.UserQuery;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by michael on 10/25/17.
 */

public class UserRepository implements Repository<User, UserError, UserQuery> {

    private final FirebaseDatabase firebaseDatabase;

    private final Transformer<Map<String, Object>, User> userTransformer;

    public UserRepository(FirebaseDatabase firebaseDatabase) {
        this.firebaseDatabase = firebaseDatabase;
        this.userTransformer = new UserTransformer();
    }

    @Override
    public void query(final UserQuery query, final Receiver<List<User>, UserError, UserQuery> receiver) {
        DatabaseReference databaseReference = firebaseDatabase.getReference("users");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<User> userList = new ArrayList<User>();
                for (DataSnapshot childSnapShot : dataSnapshot.getChildren()) {
                    Transformer.TransformResult<User> transformResult = userTransformer.transform((Map<String, Object>) childSnapShot.getValue());
                    if (transformResult.isValid()) {
                        userList.add(transformResult.getResult());
                    }
                }

                receiver.onSuccess(query, userList);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                receiver.onError(query, new UserError());
            }
        });
    }

    @Override
    public void update(User toUpdate, Receiver<User, UserError, UserQuery> receiver) {

    }

    @Override
    public void delete(User toDelete, Receiver<User, UserError, UserQuery> receiver) {

    }
}