package com.allonapps.talkytalk.data.repository;

import com.allonapps.talkytalk.data.Receiver;

import java.util.List;

/**
 * Created by michael on 10/26/17.
 */

public class CachedRepository<S, E, Q> implements Repository<S, E, Q> {

    // TODO: implement a 'cached repository' which combines live data and database data.  It should fetch from live if available, and it should have configurable cache policy timeouts etc.
    // TODO: not done in this demo for the sake of time :(

    @Override
    public void query(Q query, Receiver<List<S>, E, Q> receiver) {

    }

    @Override
    public void update(S toUpdate, Receiver<S, E, Q> receiver) {

    }

    @Override
    public void delete(S toDelete, Receiver<S, E, Q> receiver) {

    }
}