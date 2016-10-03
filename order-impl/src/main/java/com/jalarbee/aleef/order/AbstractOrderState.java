package com.jalarbee.aleef.order;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.jalarbee.aleef.order.model.Order;
import com.lightbend.lagom.javadsl.immutable.ImmutableStyle;
import org.immutables.value.Value;

import java.time.LocalDateTime;

/**
 * @author Abdoulaye Diallo
 */
@ImmutableStyle
@Value.Immutable
@JsonDeserialize(as = OrderState.class)
public interface AbstractOrderState {

    @Value.Parameter
    Order order();

    @Value.Parameter
    LocalDateTime timestamp();

}
