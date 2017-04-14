package com.jalarbee.aleef.account.api;

import akka.Done;
import akka.NotUsed;
import com.jalarbee.aleef.account.api.model.Account;
import com.lightbend.lagom.javadsl.api.Descriptor;
import com.lightbend.lagom.javadsl.api.Service;
import com.lightbend.lagom.javadsl.api.ServiceCall;
import com.lightbend.lagom.javadsl.api.broker.Topic;

import static com.lightbend.lagom.javadsl.api.Service.*;

/**
 * @author Abdoulaye Diallo
 */
public interface AccountService extends Service {

    ServiceCall<Account, Account> register();

    Topic<AccountEvent> accountEvents();

    ServiceCall<NotUsed, Account> getAccount(String accountId);

    ServiceCall<Long, Done> suspend(String accountId);

    ServiceCall<NotUsed, Done> liftSuspension(String accountId);

    ServiceCall<NotUsed, Done> delete(String accountId);


    @Override
    default Descriptor descriptor() {
        // @formatter:off
        return named("accountService").withCalls(
                pathCall("/api/accounts", this::register),
                pathCall("/api/account/:id/suspend", this::suspend),
                pathCall("/api/account/:id/delete", this::delete),
                pathCall("/api/account/:id/unsuspend", this::liftSuspension),
                pathCall("/api/account/:id", this::getAccount)
        ).publishing(
                topic("account-AccountEvent", this::accountEvents)
        ).withAutoAcl(true);
        // @formatter:on
    }
}
