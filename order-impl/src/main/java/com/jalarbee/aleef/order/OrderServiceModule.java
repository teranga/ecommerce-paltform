package com.jalarbee.aleef.order;

import com.google.inject.AbstractModule;
import com.lightbend.lagom.javadsl.server.ServiceGuiceSupport;

/**
 * @author Abdoulaye Diallo
 */
public class OrderServiceModule extends AbstractModule implements ServiceGuiceSupport {
    @Override
    protected void configure() {
        bindServices(serviceBinding(OrderService.class, OrderServiceImpl.class));
    }
}
