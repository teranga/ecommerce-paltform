package com.jalarbee.aleef.product;

import akka.Done;
import akka.NotUsed;
import akka.stream.javadsl.Source;
import com.jalarbee.aleef.product.model.PricingModif;
import com.jalarbee.aleef.product.model.Product;
import com.lightbend.lagom.javadsl.api.Descriptor;
import com.lightbend.lagom.javadsl.api.Service;
import com.lightbend.lagom.javadsl.api.ServiceCall;
import com.lightbend.lagom.javadsl.api.transport.Method;

import static com.lightbend.lagom.javadsl.api.Service.*;


/**
 * @author Abdoulaye Diallo
 */
public interface ProductService extends Service {

    ServiceCall<Product, Done> create();

    ServiceCall<NotUsed, Source<Product, ?>> stream();

    ServiceCall<PricingModif, Done> newPricing(String id);

    @Override
    default Descriptor descriptor() {
        // @formatter:off
        return named("productService").withCalls(
                restCall(Method.POST, "/api/products", this::create),
                pathCall("/api/products/stream", this::stream),
                restCall(Method.POST, "/api/products/:id/pricing", this::newPricing)
        ).withAutoAcl(true);
        // @formatter:on
    }

}
