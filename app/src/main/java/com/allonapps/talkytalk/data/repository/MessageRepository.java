package com.allonapps.talkytalk.data.repository;

import android.support.annotation.NonNull;

import com.allonapps.talkytalk.data.Receiver;
import com.allonapps.talkytalk.data.Transformer;
import com.allonapps.talkytalk.data.entity.Message;
import com.allonapps.talkytalk.data.entity.transformer.MessageTransformer;
import com.allonapps.talkytalk.data.error.MessageError;
import com.allonapps.talkytalk.data.query.MessageQuery;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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

public class MessageRepository implements Repository<Message, MessageError, MessageQuery> {

    private final FirebaseDatabase firebaseDatabase;

    private final Transformer<Map<String, Object>, Message> messageTransformer;

    public MessageRepository(FirebaseDatabase firebaseDatabase) {
        this.firebaseDatabase = firebaseDatabase;
        messageTransformer = new MessageTransformer();
    }


    @Override
    public void query(final MessageQuery query, final Receiver<List<Message>, MessageError, MessageQuery> receiver) {
        DatabaseReference databaseReference = firebaseDatabase.getReference("messages");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Message> messageList = new ArrayList<Message>();
                for (DataSnapshot childSnapShot : dataSnapshot.getChildren()) {
                    Transformer.TransformResult<Message> transformResult = messageTransformer.transform((Map<String, Object>) childSnapShot.getValue());
                    if (transformResult.isValid()) {
                        messageList.add(transformResult.getResult());
                    }
                }


                receiver.onSuccess(query, messageList);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                receiver.onError(query, new MessageError());
            }
        });
    }

    @Override
    public void update(final Message toUpdate, final Receiver<Message, MessageError, MessageQuery> receiver) {
        DatabaseReference databaseReference = firebaseDatabase.getReference("messages");
        databaseReference.push().setValue(toUpdate).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    receiver.onSuccess(null, toUpdate);
                } else {
                    receiver.onError(null, new MessageError());
                }
            }
        });
    }

    @Override
    public void delete(Message toDelete, Receiver<Message, MessageError, MessageQuery> receiver) {

    }
}
