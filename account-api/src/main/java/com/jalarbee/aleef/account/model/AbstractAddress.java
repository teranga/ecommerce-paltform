package com.jalarbee.aleef.account.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.lightbend.lagom.javadsl.immutable.ImmutableStyle;
import org.immutables.value.Value;

import java.util.Optional;

/**
 * @author Abdoulaye Diallo
 */
@Value.Immutable
@ImmutableStyle
@JsonDeserialize(as = Address.class)
public interface AbstractAddress {

    @Value.Parameter
    String line1();

    @Value.Parameter
    Optional<String> line2();

    @Value.Parameter
    String city();

    @Value.Parameter
    String state();
}
