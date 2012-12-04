package com.natpryce.maybe;

import com.google.common.base.Function;
import com.google.common.base.Predicate;

import java.util.Collections;
import java.util.Iterator;

public abstract class Maybe<T> implements Iterable<T> {
    public abstract boolean isKnown();
    public abstract T otherwise(T defaultValue);
    public abstract Maybe<T> otherwise(Maybe<T> maybeDefaultValue);
    public abstract <U> Maybe<U> to(Function<? super T, ? extends U> mapping);
    public abstract Maybe<Boolean> query(Predicate<? super T> mapping);
    
    public static <T> Maybe<T> unknown() {
        return new Maybe<T>() {
            @Override
            public boolean isKnown() {
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
            public <U> Maybe<U> to(Function<? super T, ? extends U> mapping) {
                return unknown();
            }

            @Override
            public Maybe<Boolean> query(Predicate<? super T> mapping) {
                return unknown();
            }

            @Override
            public String toString() {
                return "unknown";
            }

            @Override
            @SuppressWarnings({"EqualsWhichDoesntCheckParameterClass"})
            public boolean equals(Object obj) {
                return false;
            }

            @Override
            public int hashCode() {
                return 0;
            }
        };
    }

    public static <T> Maybe<T> definitely(final T theValue) {
        return new DefiniteValue<T>(theValue);
    }

    private static class DefiniteValue<T> extends Maybe<T> {
        private final T theValue;

        public DefiniteValue(T theValue) {
            this.theValue = theValue;
        }

        @Override
        public boolean isKnown() {
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
        public <U> Maybe<U> to(Function<? super T, ? extends U> mapping) {
            return definitely(mapping.apply(theValue));
        }

        @Override
        public Maybe<Boolean> query(Predicate<? super T> mapping) {
            return definitely(mapping.apply(theValue));
        }

        @Override
        public String toString() {
            return "definitely " + theValue.toString();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            DefiniteValue<?> that = (DefiniteValue<?>) o;

            return theValue.equals(that.theValue);

        }

        @Override
        public int hashCode() {
            return theValue.hashCode();
        }
    }
}