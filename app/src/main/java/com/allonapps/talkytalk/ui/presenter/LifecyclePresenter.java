package com.allonapps.talkytalk.ui.presenter;

/**
 * Created by michael on 10/13/17.
 */

public abstract class LifecyclePresenter<V> {

    protected V view;

    public final void onCreateView(V view) {
        this.view = view;
        afterCreateView();
    }

    protected abstract void afterCreateView();

}