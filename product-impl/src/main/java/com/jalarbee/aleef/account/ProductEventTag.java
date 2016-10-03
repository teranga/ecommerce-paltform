package com.jalarbee.aleef.account;

import com.lightbend.lagom.javadsl.persistence.AggregateEventTag;

/**
 * @author Abdoulaye Diallo
 */
public class ProductEventTag {

    public static final AggregateEventTag<ProductEvent> INSTANCE = AggregateEventTag.of(ProductEvent.class);
}
