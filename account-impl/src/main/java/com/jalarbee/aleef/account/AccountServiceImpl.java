package com.jalarbee.aleef.account;

import akka.Done;
import akka.NotUsed;
import akka.stream.javadsl.Source;
import com.google.inject.Inject;
import com.jalarbee.aleef.account.model.Account;
import com.jalarbee.aleef.account.model.Person;
import com.jalarbee.aleef.account.model.Registration;
import com.lightbend.lagom.javadsl.api.ServiceCall;
import com.lightbend.lagom.javadsl.persistence.PersistentEntityRef;
import com.lightbend.lagom.javadsl.persistence.PersistentEntityRegistry;
import com.lightbend.lagom.javadsl.persistence.cassandra.CassandraReadSide;
import com.lightbend.lagom.javadsl.persistence.cassandra.CassandraSession;
import com.lightbend.lagom.javadsl.pubsub.PubSubRef;
import com.lightbend.lagom.javadsl.pubsub.PubSubRegistry;
import com.lightbend.lagom.javadsl.pubsub.TopicId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * @author Abdoulaye Diallo
 */
public class AccountServiceImpl implements AccountService {


    private final PersistentEntityRegistry persistentEntityRegistry;
    private final PubSubRegistry topics;
    private final CassandraSession db;
    private final Logger log = LoggerFactory.getLogger(AccountServiceImpl.class);

    @Inject
    public AccountServiceImpl(PersistentEntityRegistry persistentEntityRegistry, CassandraReadSide readSide, PubSubRegistry topics, CassandraSession db) {
        this.persistentEntityRegistry = persistentEntityRegistry;
        this.topics = topics;
        this.db = db;
        persistentEntityRegistry.register(AccountEntity.class);
        readSide.register(AccountEventProcessor.class);
    }

    @Override
    public ServiceCall<Registration, Done> register() {

        return request -> {
            PubSubRef<Registration> topic = topics.refFor(TopicId.of(Registration.class, "topic"));
            topic.publish(request);
            log.info("account created - id = : {}", request.login().id());
            PersistentEntityRef<AccountCommand> ref = persistentEntityRegistry.refFor(AccountEntity.class, request.login().id());
            return ref.ask(CreateAccount.of(request));
        };
    }

    @Override
    public ServiceCall<NotUsed, Source<Account, ?>> streamRegistrations() {
        return req -> {
            PubSubRef<Account> topic = topics.refFor(TopicId.of(Account.class, "topic"));
            return CompletableFuture.completedFuture(topic.subscriber());
        };
    }

    @Override
    public ServiceCall<NotUsed, Optional<Account>> getAccount(String id) {
        return request -> db.selectOne("select id, owner from account where id=?", id)
                .thenApply(
                        row -> row.map(r -> Optional.of(Account.of(r.getString("id"), r.get("owner", Person.class)))).orElse(Optional.empty())
                );
    }
}
