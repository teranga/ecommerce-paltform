package com.jalarbee.aleef.account;

import com.google.inject.AbstractModule;
import com.jalarbee.aleef.product.ProductService;
import com.lightbend.lagom.javadsl.server.ServiceGuiceSupport;

/**
 * @author Abdoulaye Diallo
 */
public class ProductServiceModule extends AbstractModule implements ServiceGuiceSupport {

    @Override
    protected void configure() {
        bindServices(serviceBinding(ProductService.class, ProductServiceImpl.class));
    }
}
