package com.natpryce.maybe;

public class Customer {
    private Maybe<String> emailAddress;

    public Customer(String emailAddress) {
        this.emailAddress = Maybe.just(emailAddress);
    }

    public Customer() {
        this.emailAddress = Maybe.nothing();
    }
    
    public Maybe<String> emailAddress() {
        return emailAddress;
    }
}
