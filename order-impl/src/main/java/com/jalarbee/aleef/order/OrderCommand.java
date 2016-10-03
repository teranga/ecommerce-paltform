package com.jalarbee.aleef.order;

import akka.Done;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.jalarbee.aleef.order.model.LineItem;
import com.jalarbee.aleef.order.model.Order;
import com.lightbend.lagom.javadsl.immutable.ImmutableStyle;
import com.lightbend.lagom.javadsl.persistence.PersistentEntity;
import com.lightbend.lagom.serialization.CompressedJsonable;
import com.lightbend.lagom.serialization.Jsonable;
import org.immutables.value.Value;

/**
 * @author Abdoulaye Diallo
 */
public interface OrderCommand extends Jsonable{

    @Value.Immutable
    @ImmutableStyle
    @JsonDeserialize(as = CreateOrder.class)
    interface AbstractCreateOrder extends OrderCommand, CompressedJsonable, PersistentEntity.ReplyType<Done> {
        @Value.Parameter
        Order order();
    }

    @Value.Immutable
    @ImmutableStyle
    @JsonDeserialize(as = AddLineItem.class)
    interface AbstractAddLineItem extends OrderCommand, CompressedJsonable, PersistentEntity.ReplyType<Done> {

        @Value.Parameter
        String orderId();

        @Value.Parameter
        LineItem lineItem();
    }
}
