package com.jalarbee.aleef.order;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.jalarbee.aleef.order.model.Order;
import com.lightbend.lagom.javadsl.immutable.ImmutableStyle;
import com.lightbend.lagom.javadsl.persistence.AggregateEvent;
import com.lightbend.lagom.javadsl.persistence.AggregateEventTag;
import com.lightbend.lagom.serialization.Jsonable;
import org.immutables.value.Value;

/**
 * @author Abdoulaye Diallo
 */
public interface OrderEvent extends Jsonable, AggregateEvent<OrderEvent> {

    @ImmutableStyle
    @Value.Immutable
    @JsonDeserialize(as = OrderCreated.class)
    interface AbstractOrderCreated extends OrderEvent {

        @Override
        default AggregateEventTag<OrderEvent> aggregateTag() {
            return OrderEventTag.INSTANCE;
        }

        @Value.Parameter
        String id();

        @Value.Parameter
        Order order();

    }
}
