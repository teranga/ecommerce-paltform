package com.jalarbee.aleef.account.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.jalarbee.aleef.account.api.model.Account;
import com.lightbend.lagom.serialization.Jsonable;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

@Data
@EqualsAndHashCode(of = "accountId")
public class PAccount implements Jsonable {


    public static final PAccount EMPTY = new PAccount(null, null, null);

    private final String accountId;
    private final PPerson owner;
    private final PAccountStatus status;
    private final Optional<Instant> suspendedTill;

    public PAccount(String accountId, PPerson owner) {
        this(accountId, owner, PAccountStatus.CREATED);
    }


    public PAccount(String accountId, PPerson owner, PAccountStatus status) {
        this.accountId = accountId;
        this.owner = owner;
        this.status = status;
        this.suspendedTill = Optional.empty();
    }

    @JsonCreator
    public PAccount(String accountId, PPerson owner, PAccountStatus status, Optional<Instant> suspendedTill) {
        this.accountId = accountId;
        this.owner = owner;
        this.status = status;
        this.suspendedTill = suspendedTill;
    }

    public Account toAccount() {
        return new Account(accountId, owner.toPerson(), status.toAccountStatus());
    }


    public static PAccount fromAccount(Account account, PAccountStatus status) {
        return new PAccount(account.getId(), PPerson.fromPerson(account.getOwner()), status);
    }

    public PAccount updateStatus(PAccountStatus status) {
        return new PAccount(this.accountId, this.owner, status);
    }

    public PAccount applySuspension(Duration duration) {
        return suspendedTill.isPresent()
                ?
                new PAccount(this.accountId, this.owner, status, suspendedTill.map(x -> x.plusSeconds(duration.toMillis() / 1000)))
                :
                new PAccount(this.accountId, this.owner, status, Optional.of(Instant.now().plusSeconds(duration.toMillis() / 1000)));
    }

}
