package com.jalarbee.aleef.order.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.lightbend.lagom.javadsl.immutable.ImmutableStyle;
import org.immutables.value.Value;
import org.pcollections.PSequence;

import java.time.LocalDateTime;

/**
 * @author Abdoulaye Diallo
 */
@Value.Immutable
@ImmutableStyle
@JsonDeserialize(as = Order.class)
public interface AbstractOrder {

    @Value.Parameter
    String id();

    @Value.Parameter
    LocalDateTime openTime();

    @Value.Parameter
    LocalDateTime closeTime();

    @Value.Parameter
    LocalDateTime lastUpdateTime();

    @Value.Parameter
    PSequence<LineItem> lineItems();

    @Value.Parameter
    Account originator();

    @Value.Parameter
    Account beneficiary();

}
