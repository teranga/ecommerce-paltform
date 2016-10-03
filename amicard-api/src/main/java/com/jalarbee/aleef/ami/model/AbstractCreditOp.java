package com.jalarbee.aleef.ami.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.lightbend.lagom.javadsl.immutable.ImmutableStyle;
import org.immutables.value.Value;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @author Abdoulaye Diallo
 */
@Value.Immutable
@ImmutableStyle
@JsonDeserialize(as = CreditOp.class)
public interface AbstractCreditOp {

    LocalDateTime on();
    BigDecimal of();
    String by();
}
