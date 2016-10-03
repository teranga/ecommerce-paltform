package com.jalarbee.aleef.account.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.lightbend.lagom.javadsl.immutable.ImmutableStyle;
import org.immutables.value.Value;

/**
 * @author Abdoulaye Diallo
 */
@Value.Immutable
@ImmutableStyle
@JsonDeserialize(as = Person.class)
public interface AbstractPerson {

    @Value.Parameter
    String firstName();

    @Value.Parameter
    String lastName();

    @Value.Parameter
    String email();

    @Value.Parameter
    String cellPhone();
}
