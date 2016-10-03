package com.jalarbee.aleef.order;

import com.lightbend.lagom.javadsl.persistence.AggregateEventTag;

/**
 * @author Abdoulaye Diallo
 */
public class OrderEventTag {

    public static final AggregateEventTag<OrderEvent> INSTANCE = AggregateEventTag.of(OrderEvent.class);
}
