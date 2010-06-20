package com.natpryce.maybe;

import com.google.common.base.Function;

import java.util.Collections;
import java.util.Iterator;

public abstract class Maybe<T> implements Iterable<T> {
    public abstract boolean exists();
    public abstract T otherwise(T defaultValue);
    public abstract Maybe<T> otherwise(Maybe<T> maybeDefaultValue);
    public abstract <U> Maybe<U> transform(Function<T,U> mapping);

    public static <T> Maybe<T> nothing() {
        return new Maybe<T>() {
            @Override
            public boolean exists() {
                return false;
            }

            public Iterator<T> iterator() {
                return Collections.<T>emptyList().iterator();
            }

            @Override
            public T otherwise(T defaultValue) {
                return defaultValue;
            }

            @Override
            public Maybe<T> otherwise(Maybe<T> maybeDefaultValue) {
                return maybeDefaultValue;
            }

            @Override
            public <U> Maybe<U> transform(Function<T, U> mapping) {
                return nothing();
            }
        };
    }

    public static <T> Maybe<T> just(final T theValue) {
        return new Maybe<T>() {
            @Override
            public boolean exists() {
                return true;
            }

            public Iterator<T> iterator() {
                return Collections.singleton(theValue).iterator();
            }

            @Override
            public T otherwise(T defaultValue) {
                return theValue;
            }

            @Override
            public Maybe<T> otherwise(Maybe<T> maybeDefaultValue) {
                return this;
            }

            @Override
            public <U> Maybe<U> transform(Function<T, U> mapping) {
                return just(mapping.apply(theValue));
            }
        };
    }
}
