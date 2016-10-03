package com.jalarbee.aleef.ami.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.lightbend.lagom.javadsl.immutable.ImmutableStyle;
import org.immutables.value.Value;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * @author Abdoulaye Diallo
 */
@Value.Immutable
@ImmutableStyle
@JsonDeserialize(as = DebitOp.class)
public interface AbstractDebitOp {

    @Value.Parameter
    UUID id();
    @Value.Parameter
    int itemCount();
    @Value.Parameter
    LocalDateTime on();
    @Value.Parameter
    BigDecimal amount();
    @Value.Parameter
    String notes();
}
