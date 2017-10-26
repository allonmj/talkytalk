package com.allonapps.talkytalk.data.async.rolled;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;

import com.allonapps.talkytalk.data.Receiver;
import com.allonapps.talkytalk.data.Response;
import com.allonapps.talkytalk.data.repository.Repository;

import java.util.List;

/**
 * Created by michael on 10/26/17.
 */

public class AsyncUtils {

    public static class RolledFlow<S, E, Q> {

        public interface SuccessListener<S> {
            void onSuccess(List<S> success);
        }

        public interface ErrorListener<E> {
            void onError(@Nullable E error);
        }

        public interface RolledFlowQueryCreator<S, E> {
            RolledFlowQuery createForResponse(Response<List<S>, E> response);
        }

        private final Repository<S, E, Q> repository;
        private final AsyncManager asyncManager;

        private
        @Nullable
        SuccessListener<S> successListener;

        private
        @Nullable
        ErrorListener<E> errorListener;

        private
        @Nullable
        RolledFlowQueryCreator<S, E> rolledFlowQueryCreator;


        private boolean respondOnUiThread;

        private RolledFlow(AsyncManager asyncManager, Repository<S, E, Q> repository) {
            this.asyncManager = asyncManager;
            this.repository = repository;
        }

        public static <S, E, Q> RolledFlow<S, E, Q> create(AsyncManager asyncManager, Repository<S, E, Q> repository) {
            return new RolledFlow<S, E, Q>(asyncManager, repository);
        }

        public RolledFlow<S, E, Q> then(RolledFlowQueryCreator<S, E> rolledFlowQueryCreator) {
            this.rolledFlowQueryCreator = rolledFlowQueryCreator;
            return this;
        }

        public RolledFlow<S, E, Q> success(SuccessListener<S> successListener) {
            this.successListener = successListener;
            return this;
        }

        public RolledFlow<S, E, Q> error(ErrorListener<E> errorListener) {
            this.errorListener = errorListener;
            return this;
        }

        public RolledFlow<S, E, Q> respondOnUiThread(boolean respondOnUiThread) {
            this.respondOnUiThread = respondOnUiThread;
            return this;
        }

        public void run(final Q query) {
            try {
                asyncManager.runAsyncOp(new AsyncManager.AsyncOp() {
                    @Override
                    public void doInBackground() {
                        repository.query(query, new Receiver<List<S>, E, Q>() {
                            @Override
                            public void onSuccess(@Nullable Q query, List<S> success) {
                                Response<List<S>, E> response = new Response<>(success, null);
                                if (rolledFlowQueryCreator == null) {
                                    respondSuccess(response.response);
                                } else {
                                    RolledFlowQuery rolledFlowQuery = rolledFlowQueryCreator.createForResponse(response);
                                    if (rolledFlowQuery != null) {
                                        rolledFlowQuery.rolledFlow.run(rolledFlowQuery.query);
                                    }
                                    respondSuccess(response.response);
                                }
                            }

                            @Override
                            public void onError(@Nullable Q query, E error) {
                                Response<List<S>, E> response = new Response<>(null, error);
                                if (rolledFlowQueryCreator == null) {
                                    respondError(response.error);
                                } else {
                                    RolledFlowQuery rolledFlowQuery = rolledFlowQueryCreator.createForResponse(response);
                                    if (rolledFlowQuery != null) {
                                        rolledFlowQuery.rolledFlow.run(rolledFlowQuery.query);
                                    }
                                    respondError(response.error);
                                }
                            }
                        });
                    }
                });
            } catch (InterruptedException e) {
                if (errorListener != null) {
                    errorListener.onError(null);
                }
            }
        }

        private void respondSuccess(final List<S> success) {
            if (respondOnUiThread) {
                runOnUIThread(new Runnable() {
                    @Override
                    public void run() {
                        if (successListener == null) {
                            return;
                        }
                        successListener.onSuccess(success);
                    }
                });
            } else {
                if (successListener == null) {
                    return;
                }
                successListener.onSuccess(success);

            }
        }

        private void respondError(final E error) {
            if (respondOnUiThread) {
                runOnUIThread(new Runnable() {
                    @Override
                    public void run() {
                        if (errorListener == null) {
                            return;
                        }
                        errorListener.onError(error);
                    }
                });
            } else {
                if (errorListener == null) {
                    return;
                }
                errorListener.onError(error);
            }
        }
    }

    public static class RolledFlowQuery {
        private RolledFlow rolledFlow;
        private Object query;

        public RolledFlowQuery(RolledFlow rolledFlow, Object query) {
            this.rolledFlow = rolledFlow;
            this.query = query;
        }
    }

    public static void runOnUIThread(Runnable runnable) {
        new Handler(Looper.getMainLooper()).post(runnable);
    }
}