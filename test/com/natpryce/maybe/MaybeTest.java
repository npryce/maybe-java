package com.natpryce.maybe;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import org.junit.Test;

import java.util.List;
import java.util.Set;

import static com.google.common.collect.Iterables.concat;
import static com.google.common.collect.Iterables.transform;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newHashSet;
import static com.natpryce.maybe.Maybe.definitely;
import static com.natpryce.maybe.Maybe.unknown;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

public class MaybeTest {
    public static class Customer {
        private Maybe<String> emailAddress;

        public Customer(String emailAddress) {
            this.emailAddress = Maybe.definitely(emailAddress);
        }

        public Customer() {
            this.emailAddress = Maybe.unknown();
        }

        public Maybe<String> emailAddress() {
            return emailAddress;
        }
    }

    @Test
    public void equalsOfKnownValues() throws Exception {
        assertThat(definitely(1), equalTo(definitely(1)));
        assertThat(definitely(1), not(equalTo(definitely(2))));
    }

    @Test
    public void unknownValuesAreNeverEqual() throws Exception {
        assertThat(unknown(), not(equalTo(unknown())));

        Maybe<Object> u = unknown();
        assertThat(u, not(equalTo(u)));
    }

    @Test
    public void anUnknownThingIsNeverEqualToAKnownThing() throws Exception {
        assertThat(Maybe.<Integer>unknown(), not(equalTo(definitely(1))));
        assertThat(Maybe.<String>unknown(), not(equalTo(definitely("rumsfeld"))));

        assertThat(definitely(1), not(equalTo(Maybe.<Integer>unknown())));
        assertThat(definitely("rumsfeld"), not(equalTo(Maybe.<String>unknown())));
    }

    @Test
    public void otherwiseADefaultValue() throws Exception {
        assertThat(noString().otherwise(""), equalTo(""));
        assertThat(definitely("foo").otherwise(""), equalTo("foo"));
    }

    @Test
    public void chainingOtherwise() throws Exception {
        assertThat(noString().otherwise(noString()).otherwise(""), equalTo(""));
        assertThat(noString().otherwise(definitely("X")).otherwise(""), equalTo("X"));
        assertThat(definitely("X").otherwise(definitely("Y")).otherwise(""), equalTo("X"));
    }

    @Test
    public void transforming() throws Exception {
        assertThat(new Customer("alice@example.com").emailAddress().to(toUpperCase).otherwise("nobody@example.com"),
                equalTo("ALICE@EXAMPLE.COM"));
        assertThat(new Customer().emailAddress().to(toUpperCase).otherwise("UNKNOWN"),
                equalTo("UNKNOWN"));
    }

    @Test
    public void querying() throws Exception {
        assertThat(definitely("example@example.com").query(isValidEmailAddress), equalTo(definitely(true)));
        assertThat(definitely("invalid-email-address").query(isValidEmailAddress), equalTo(definitely(false)));

        assertThat(Maybe.<String>unknown().query(isValidEmailAddress).isKnown(), equalTo(false));
    }

    @Test
    public void ifThen() throws Exception {
        Maybe<String> foo = definitely("foo");

        if (foo.isKnown()) for (String s : foo) {
            assertThat(s, equalTo("foo"));
        }
        else {
            fail("should not have been called");
        }
    }

    @SuppressWarnings({"UnusedDeclaration"})
    @Test
    public void ifElse() throws Exception {
        Maybe<String> foo = unknown();

        if (foo.isKnown()) for (String s : foo) {
            fail("should not have been called");
        }
        else {
            // ok!
        }
    }

    @Test
    public void exampleCollectingValidEmailAddresses() {
        List<Customer> customers = newArrayList(
                new Customer(),
                new Customer("alice@example.com"),
                new Customer("bob@example.com"),
                new Customer(),
                new Customer("alice@example.com")
        );

        Set<String> emailAddresses = newHashSet(
                concat(transform(customers, emailAddress)));

        assertThat(emailAddresses, equalTo((Set<String>) newHashSet(
                "alice@example.com",
                "bob@example.com",
                "alice@example.com")));
    }


    static final Function<Customer, Maybe<String>> emailAddress = new Function<Customer, Maybe<String>>() {
        public Maybe<String> apply(Customer c) {
            return c.emailAddress();
        }
    };

    static final Predicate<String> isValidEmailAddress = new Predicate<String>() {
        public boolean apply(String input) {
            return input.contains("@");
        }
    };

    static final Function<String, String> toUpperCase = new Function<String, String>() {
        public String apply(String from) {
            return from.toUpperCase();
        }
    };

    private Maybe<String> noString() {
        return unknown();
    }

}