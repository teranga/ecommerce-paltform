package com.jalarbee.aleef.account;

import com.lightbend.lagom.javadsl.persistence.AggregateEventTag;

/**
 * @author Abdoulaye Diallo
 */
public class AccountEventTag {

    public static final AggregateEventTag<AccountEvent> INSTANCE = AggregateEventTag.of(AccountEvent.class);
}
