package com.allonapps.talkytalk.data;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by michael on 10/25/17.
 */
public abstract class Transformer<F, T> {

    public abstract TransformResult<T> transform(F from);

    public List<T> transform(List<F> from) {
        List<T> to = new ArrayList<T>(from == null ? 0 : from.size());
        if (from != null) {
            for (F f : from) {
                TransformResult<T> transformResult = transform(f);
                if (transformResult.isValid()) {
                    to.add(transformResult.getResult());
                }
            }
        }
        return to;
    }

    public T transformObject(F from) {
        TransformResult<T> transformResult = transform(from);
        if (transformResult.isValid()) {
            return transformResult.result;
        } else {
            return null;
        }
    }

    public static class TransformResult<T> {

        private T result;

        private TransformResult(Builder<T> builder) {
            this.result = builder.result;
        }

        public boolean isValid() {
            return result != null;
        }

        public T getResult() {
            return result;
        }

        public static class Builder<T> {
            private T result;

            public TransformResult<T> validResult(T result) {
                this.result = result;
                return new TransformResult<T>(this);
            }

            public TransformResult<T> invalidResult() {
                this.result = null;
                return new TransformResult<T>(this);
            }
        }
    }
}