package com.jalarbee.aleef.account.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
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
    String id();

    @Value.Parameter
    Person owner();
    

}
