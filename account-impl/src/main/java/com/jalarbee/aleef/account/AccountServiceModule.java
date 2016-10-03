package com.jalarbee.aleef.account;

import com.google.inject.AbstractModule;
import com.lightbend.lagom.javadsl.server.ServiceGuiceSupport;

/**
 * @author Abdoulaye Diallo
 */
public class AccountServiceModule extends AbstractModule implements ServiceGuiceSupport {

    @Override
    protected void configure() {
        bindServices(serviceBinding(AccountService.class, AccountServiceImpl.class));
    }
}
