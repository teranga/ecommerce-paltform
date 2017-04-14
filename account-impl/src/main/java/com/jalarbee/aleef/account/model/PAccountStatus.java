package com.jalarbee.aleef.account.model;

import com.jalarbee.aleef.account.api.model.AccountStatus;

/**
 * @author Abdoulaye Diallo
 */
public enum PAccountStatus {

    CREATED,

    VERIFIED,

    SUSPENDED,

    DELETED,

    NOT_CREATED;

    public static PAccountStatus fromAccountStatus(AccountStatus accountStatus) {
        return PAccountStatus.valueOf(accountStatus.name());
    }

    public AccountStatus toAccountStatus() {
        return AccountStatus.valueOf(name());
    }

}
