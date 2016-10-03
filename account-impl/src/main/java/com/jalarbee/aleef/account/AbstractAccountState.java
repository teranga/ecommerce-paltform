package com.jalarbee.aleef.account;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.jalarbee.aleef.account.model.Account;
import com.lightbend.lagom.javadsl.immutable.ImmutableStyle;
import org.immutables.value.Value;

import java.time.LocalDateTime;

/**
 * @author Abdoulaye Diallo
 */
@Value.Immutable
@ImmutableStyle
@JsonDeserialize(as = AccountState.class)
public interface AbstractAccountState {

    @Value.Parameter
    Account account();

    @Value.Parameter
    LocalDateTime timestamp();
}
