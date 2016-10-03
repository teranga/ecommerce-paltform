package com.jalarbee.aleef.product.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.lightbend.lagom.javadsl.immutable.ImmutableStyle;
import org.immutables.value.Value;

import java.util.List;
import java.util.Optional;

/**
 * @author Abdoulaye Diallo
 */

@ImmutableStyle
@Value.Immutable
@JsonDeserialize(as = Product.class)
public interface AbstractProduct {

    @Value.Parameter
    String id();

    @Value.Parameter
    String name();

    @Value.Parameter
    Optional<String> description();

    @Value.Parameter
    Pricing pricing();

    @Value.Parameter
    List<Pricing> pricingHistory();

}
