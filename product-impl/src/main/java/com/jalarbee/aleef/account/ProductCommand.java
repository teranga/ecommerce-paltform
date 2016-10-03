package com.jalarbee.aleef.account;

import akka.Done;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.jalarbee.aleef.product.model.Product;
import com.lightbend.lagom.javadsl.immutable.ImmutableStyle;
import com.lightbend.lagom.javadsl.persistence.PersistentEntity;
import com.lightbend.lagom.serialization.CompressedJsonable;
import com.lightbend.lagom.serialization.Jsonable;
import org.immutables.value.Value;

/**
 * @author Abdoulaye Diallo
 */
public interface ProductCommand extends Jsonable {

    @Value.Immutable
    @ImmutableStyle
    @JsonDeserialize(as = CreateProduct.class)
    interface AbstractCreateProduct extends ProductCommand, CompressedJsonable, PersistentEntity.ReplyType<Done> {

        @Value.Parameter
        Product product();
    }
}
