package com.jalarbee.aleef.account;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.jalarbee.aleef.account.model.PAccount;
import com.jalarbee.aleef.account.model.PAccountStatus;
import com.lightbend.lagom.serialization.Jsonable;
import lombok.Value;

@Value
public class PAccountState implements Jsonable {

    private final PAccount account;

    @JsonCreator
    public PAccountState(PAccount account) {
        this.account = account;
    }

    PAccountStatus getStatus() {
        return account.getStatus();
    }

    public static PAccountState empty() {
        return new PAccountState(PAccount.EMPTY);
    }

    public PAccountState updateStatus(PAccountStatus status) {
        return new PAccountState(account.updateStatus(status));
    }

    public PAccountState applySuspension(PAccountCommand.SuspendAccount event) {
        return new PAccountState(account.applySuspension(event.getDuration()));
    }
//
//    public static PAccountState from(PAccount account) {
//        assert account.getStatus() == PAccountStatus.CREATED;
//        return new PAccountState(Optional.of(account));
//        return null;
//    }

    public PAccountState initAccount(PAccount _account) {
        if(!PAccount.EMPTY.equals(account)) throw new IllegalStateException("account already created.");
        _account.updateStatus(PAccountStatus.CREATED);
        return new PAccountState(_account);

    }
}
