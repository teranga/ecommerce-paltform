package com.jalarbee.aleef.order.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.jalarbee.aleef.product.model.Price;
import com.jalarbee.aleef.product.model.Unit;
import com.lightbend.lagom.javadsl.immutable.ImmutableStyle;
import org.immutables.value.Value;

/**
 * @author Abdoulaye Diallo
 */
@Value.Immutable
@ImmutableStyle
@JsonDeserialize(as = LineItem.class)
public interface AbstractLineItem {

    @Value.Parameter
    String getId();

    @Value.Parameter
    String productId();

    @Value.Parameter
    String name();

    @Value.Parameter
    Price price();

    @Value.Parameter
    Unit unit();

    @Value.Parameter
    double quantity();

}
