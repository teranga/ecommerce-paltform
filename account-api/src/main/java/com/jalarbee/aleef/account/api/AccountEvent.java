package com.jalarbee.aleef.account.api;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;

import java.time.Instant;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type", defaultImpl = Void.class)
@JsonSubTypes({
        @JsonSubTypes.Type(AccountEvent.AccountCreated.class),
        @JsonSubTypes.Type(AccountEvent.AccountDeleted.class),
        @JsonSubTypes.Type(AccountEvent.AccountSuspended.class),
        @JsonSubTypes.Type(AccountEvent.AccountVerifed.class)

})
public abstract class AccountEvent {

    private final String accountId;
    private final Instant happenedAt;

    public String getAccountId() {
        return accountId;
    }

    public Instant getEventTime() {
        return happenedAt;
    }

    private AccountEvent(String account, Instant happenedAt) {
        this.accountId = account;
        this.happenedAt = happenedAt;
    }

    @JsonTypeName(value = "account-created")
    public static class AccountCreated extends AccountEvent {

        public AccountCreated(String account, Instant happenedAt) {
            super(account, happenedAt);
        }
    }

    @JsonTypeName(value = "account-verified")
    static class AccountVerifed extends AccountEvent {

        public AccountVerifed(String account, Instant happenedAt) {
            super(account, happenedAt);
        }
    }


    @JsonTypeName(value = "account-suspended")
    static class AccountSuspended extends AccountEvent {

        public AccountSuspended(String account, Instant happenedAt) {
            super(account, happenedAt);
        }
    }


    @JsonTypeName(value = "account-deleted")
    static class AccountDeleted extends AccountEvent {

        public AccountDeleted(String account, Instant happenedAt) {
            super(account, happenedAt);
        }
    }

}
