package com.jalarbee.aleef.account;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.jalarbee.aleef.product.model.Product;
import com.lightbend.lagom.javadsl.immutable.ImmutableStyle;
import org.immutables.value.Value;

import java.time.LocalDateTime;

/**
 * @author Abdoulaye Diallo
 */

@Value.Immutable
@ImmutableStyle
@JsonDeserialize(as = ProductState.class)
public interface AbstractProductState {

    @Value.Parameter
    LocalDateTime timestamp();

    @Value.Parameter
    Product product();
}
