package com.jalarbee.aleef.account;

import akka.Done;
import akka.NotUsed;
import akka.stream.javadsl.Source;
import com.jalarbee.aleef.account.model.Account;
import com.jalarbee.aleef.account.model.Registration;
import com.lightbend.lagom.javadsl.api.Descriptor;
import com.lightbend.lagom.javadsl.api.Service;
import com.lightbend.lagom.javadsl.api.ServiceCall;
import com.lightbend.lagom.javadsl.api.transport.Method;

import java.util.Optional;

import static com.lightbend.lagom.javadsl.api.Service.*;

/**
 * @author Abdoulaye Diallo
 */
public interface AccountService extends Service {

    ServiceCall<Registration, Done> register();

    ServiceCall<NotUsed, Source<Account, ?>> streamRegistrations();

    ServiceCall<NotUsed, Optional<Account>> getAccount(String accountId);


    @Override
    default Descriptor descriptor() {
        // @formatter:off
        return named("accountService").withCalls(
                restCall(Method.POST, "/api/registration", this::register),
                pathCall("/api/registration/live", this::streamRegistrations),
                restCall(Method.GET, "/api/account/:id", this::getAccount)
        ).withAutoAcl(true);
        // @formatter:on
    }
}
