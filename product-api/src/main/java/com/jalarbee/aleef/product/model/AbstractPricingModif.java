package com.jalarbee.aleef.product.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.lightbend.lagom.javadsl.immutable.ImmutableStyle;
import org.immutables.value.Value;

import java.util.Map;
import java.util.UUID;

/**
 * @author Abdoulaye Diallo
 */
@ImmutableStyle
@Value.Immutable
@JsonDeserialize(as = PricingModif.class)
public interface AbstractPricingModif {

    @Value.Parameter
    UUID productId();

    @Value.Parameter
    Map<Unit, Price> pricing();
}
