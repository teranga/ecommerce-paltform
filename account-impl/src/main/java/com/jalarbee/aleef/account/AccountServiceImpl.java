package com.jalarbee.aleef.account;

import akka.Done;
import akka.NotUsed;
import akka.japi.Pair;
import com.google.inject.Inject;
import com.jalarbee.aleef.account.api.AccountEvent;
import com.jalarbee.aleef.account.api.AccountService;
import com.jalarbee.aleef.account.api.model.Account;
import com.jalarbee.aleef.account.model.PAccount;
import com.jalarbee.aleef.account.model.PAccountStatus;
import com.lightbend.lagom.javadsl.api.ServiceCall;
import com.lightbend.lagom.javadsl.api.broker.Topic;
import com.lightbend.lagom.javadsl.api.transport.NotFound;
import com.lightbend.lagom.javadsl.broker.TopicProducer;
import com.lightbend.lagom.javadsl.persistence.Offset;
import com.lightbend.lagom.javadsl.persistence.PersistentEntityRef;
import com.lightbend.lagom.javadsl.persistence.PersistentEntityRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Singleton;
import javax.validation.constraints.NotNull;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

/**
 * @author Abdoulaye Diallo
 */
@Singleton
class AccountServiceImpl implements AccountService {


    private final PersistentEntityRegistry registry;
    private final Logger log = LoggerFactory.getLogger(AccountServiceImpl.class);

    @Inject
    public AccountServiceImpl(PersistentEntityRegistry registry) {
        this.registry = registry;
        registry.register(AccountEntity.class);
    }

    @Override
    public ServiceCall<Account, Account> register() {

        return request -> {
            PAccount pAccount = PAccount.fromAccount(request, PAccountStatus.NOT_CREATED); //TODO: perform validation
            PersistentEntityRef<PAccountCommand> ref = registry.refFor(AccountEntity.class, request.getId());
            return ref.ask(new PAccountCommand.CreateAccount(pAccount)).thenApply(done -> pAccount.toAccount());
        };
    }

    @Override
    public Topic<AccountEvent> accountEvents() {
        //@formatter:off
        return TopicProducer.taggedStreamWithOffset(PAccountEvent.TAG.allTags(),
                (tag, offset) -> registry.eventStream(tag, offset)
                        .filter(this::filterEvent)
                        .mapAsync(1, eventAndOffset -> convertEvent(eventAndOffset.first()).thenApply(event -> Pair.create(event, eventAndOffset.second()))));
        //@formatter:on
    }


    @Override
    public ServiceCall<Long, Done> suspend(String accountId) {
        return request -> entityRef(accountId).ask(new PAccountCommand.SuspendAccount(accountId, Duration.ofMinutes(request)));
    }

    @Override
    public ServiceCall<NotUsed, Done> liftSuspension(String accountId) {
        return request -> entityRef(accountId).ask(new PAccountCommand.LiftSuspension(accountId));
    }

    @Override
    public ServiceCall<NotUsed, Done> delete(String accountId) {
        return request -> entityRef(accountId).ask(new PAccountCommand.DeleteAccount(accountId));
    }

    @NotNull
    private CompletionStage<AccountEvent> convertEvent(PAccountEvent pEvent) {
        if (pEvent instanceof PAccountEvent.AccountCreated) {
            PAccountEvent.AccountCreated event = (PAccountEvent.AccountCreated) pEvent;
            return CompletableFuture.completedFuture(new AccountEvent.AccountCreated(event.getAccount().toAccount().getId(), event.getCreatedOn()));
        } else {
            throw new IllegalStateException("trying to publish a non public event");
        }
    }

    private boolean filterEvent(Pair<PAccountEvent, Offset> pAccountEventOffsetPair) {
        return pAccountEventOffsetPair.first() instanceof PAccountEvent.AccountCreated;
    }

    @Override
    public ServiceCall<NotUsed, Account> getAccount(String id) {
        return request -> entityRef(id).ask(PAccountCommand.GetAccount.INSTANCE).thenApply(x -> x.orElseGet(() -> {
            throw new NotFound("Account " + id + " not found");
        }));
    }

    private PersistentEntityRef<PAccountCommand> entityRef(String id) {
        return registry.refFor(AccountEntity.class, id);
    }
}
