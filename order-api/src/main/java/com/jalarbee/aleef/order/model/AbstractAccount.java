package com.jalarbee.aleef.order.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.jalarbee.aleef.account.model.Person;
import com.lightbend.lagom.javadsl.immutable.ImmutableStyle;
import org.immutables.value.Value;

/**
 * @author Abdoulaye Diallo
 */
@Value.Immutable
@ImmutableStyle
@JsonDeserialize(as = Account.class)
public interface AbstractAccount {

    @Value.Parameter
    String accountId();

    @Value.Parameter
    Person owner();
}
