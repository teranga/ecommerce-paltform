package com.jalarbee.aleef.account;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.jalarbee.aleef.product.model.Product;
import com.lightbend.lagom.javadsl.immutable.ImmutableStyle;
import com.lightbend.lagom.javadsl.persistence.AggregateEvent;
import com.lightbend.lagom.javadsl.persistence.AggregateEventTag;
import com.lightbend.lagom.serialization.Jsonable;
import org.immutables.value.Value;

import java.util.UUID;

/**
 * @author Abdoulaye Diallo
 */
public interface ProductEvent extends Jsonable, AggregateEvent<ProductEvent> {

    @Value.Immutable
    @ImmutableStyle
    @JsonDeserialize(as = ProductCreated.class)
    public interface AbstractProductCreated extends ProductEvent {

        @Override
        default AggregateEventTag<ProductEvent> aggregateTag() {
            return ProductEventTag.INSTANCE;
        }

        @Value.Parameter
        Product product();

        @Value.Parameter
        UUID id();
    }

}
