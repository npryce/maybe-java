package com.natpryce.maybe;

import com.google.common.base.Function;
import org.junit.Test;

import java.util.List;
import java.util.Set;

import static com.google.common.collect.Iterables.concat;
import static com.google.common.collect.Iterables.transform;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newHashSet;
import static com.natpryce.maybe.Maybe.just;
import static com.natpryce.maybe.Maybe.nothing;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

public class MaybeExamples {
    @Test
    public void collectingValidEmailAddresses() {
        List<Customer> customers = newArrayList(
            new Customer(),
            new Customer("alice@example.com"),
            new Customer("bob@example.com"),
            new Customer(),
            new Customer("alice@example.com")
        );

        Set<String> emailAddresses = newHashSet(
                concat(transform(customers, emailAddress)));
        
        assertThat(emailAddresses, equalTo((Set<String>)newHashSet(
                "alice@example.com",
                "bob@example.com",
                "alice@example.com")));
    }

    @Test
    public void otherwiseADefaultValue() throws Exception {
        assertThat(noString().otherwise(""), equalTo(""));
        assertThat(just("foo").otherwise(""), equalTo("foo"));
    }

    @Test
    public void chainingOtherwise() throws Exception {
        assertThat(noString().otherwise(noString()).otherwise(""), equalTo(""));
        assertThat(noString().otherwise(just("X")).otherwise(""), equalTo("X"));
        assertThat(just("X").otherwise(just("Y")).otherwise(""), equalTo("X"));
    }

    @Test
    public void transforming() throws Exception {
        assertThat(new Customer("alice@example.com").emailAddress().transform(toUpperCase).otherwise("nobody@example.com"),
                   equalTo("ALICE@EXAMPLE.COM"));
        assertThat(new Customer().emailAddress().transform(toUpperCase).otherwise("nobody@example.com"),
                   equalTo("nobody@example.com"));
    }

    @Test
    public void ifThen() throws Exception {
        Maybe<String> foo = just("foo");

        if (foo.exists()) for(String s : foo) {
            assertThat(s, equalTo("foo"));
        }
        else {
            fail("should not have been called");
        }
    }

    @Test
    public void ifElse() throws Exception {
        Maybe<String> foo = nothing();

        if (foo.exists()) for(String s : foo) {
            fail("should not have been called");
        }
        else {
            // ok!
        }
    }


    static Function<Customer,Maybe<String>> emailAddress = new Function<Customer, Maybe<String>>() {
        public Maybe<String> apply(Customer c) { return c.emailAddress(); }
    };

    static Function<String,String> toUpperCase = new Function<String, String>() {
        public String apply(String from) { return from.toUpperCase(); }
    };

    private Maybe<String> noString() {
        return nothing();
    }
}
