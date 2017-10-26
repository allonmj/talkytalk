package com.allonapps.talkytalk.data.repository;

import com.allonapps.talkytalk.data.Receiver;

import java.util.List;

/**
 * Created by michael on 10/14/17.
 */

public interface Repository<S, E, Q> {

    void query(Q query, Receiver<List<S>, E, Q> receiver);

    void update(S toUpdate, Receiver<S, E, Q> receiver);

    void delete(S toDelete, Receiver<S, E, Q> receiver);

    // TODO: Blocking calls can and should also be implemented (not shown in this demo).  queryBlocking(query, receiver), updateBlocking etc...

}