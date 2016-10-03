package com.jalarbee.aleef.account.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.lightbend.lagom.javadsl.immutable.ImmutableStyle;
import org.immutables.value.Value;

/**
 * @author Abdoulaye Diallo
 */
@Value.Immutable
@ImmutableStyle
@JsonDeserialize(as = Login.class)
public interface AbstractLogin {

    @Value.Parameter
    String id();

    @Value.Parameter
    String password();
}
