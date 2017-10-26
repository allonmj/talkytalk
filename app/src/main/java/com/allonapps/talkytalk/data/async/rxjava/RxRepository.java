package com.allonapps.talkytalk.data.async.rxjava;

import android.support.annotation.Nullable;

import com.allonapps.talkytalk.data.Receiver;
import com.allonapps.talkytalk.data.repository.Repository;

import java.util.List;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.annotations.NonNull;

/**
 * Created by michael on 10/25/17.
 */

public class RxRepository<S, E, Q> {

    private final Repository<S, E, Q> repository;

    public RxRepository(Repository<S, E, Q> repository) {
        this.repository = repository;
    }

    public void onRxSuccess(List<S> success) {
        // override if necessary
    }

    public void onRxError() {

    }

    public Flowable<List<S>> createFlowable(final Q query) {
        return Flowable.create(new FlowableOnSubscribe<List<S>>() {
            @Override
            public void subscribe(@NonNull final FlowableEmitter<List<S>> emitter) throws Exception {
                repository.query(query, new Receiver<List<S>, E, Q>() {
                    @Override
                    public void onSuccess(@Nullable Q query, List<S> success) {
                        onRxSuccess(success);
                        emitter.onNext(success);
                    }

                    @Override
                    public void onError(@Nullable Q query, E error) {
                        onRxError();
                        emitter.onError(new Exception("repository exception"));
                    }
                });
            }
        }, BackpressureStrategy.BUFFER);
    }
}