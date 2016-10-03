package com.jalarbee.aleef.account;

import akka.Done;
import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.TypeCodec;
import com.datastax.driver.extras.codecs.guava.OptionalCodec;
import com.datastax.driver.extras.codecs.json.JacksonJsonCodec;
import com.jalarbee.aleef.account.model.Person;
import com.lightbend.lagom.javadsl.persistence.AggregateEventTag;
import com.lightbend.lagom.javadsl.persistence.cassandra.CassandraReadSideProcessor;
import com.lightbend.lagom.javadsl.persistence.cassandra.CassandraSession;
import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletionStage;

/**
 * @author Abdoulaye Diallo
 */
public class AccountEventProcessor extends CassandraReadSideProcessor<AccountEvent> {

    private final Logger log = LoggerFactory.getLogger(AccountEventProcessor.class);

    private PreparedStatement writeAccount = null; // initialized in prepare
    private PreparedStatement writeOffset = null; // initialized in prepare

    private void setWriteAccount(PreparedStatement writeAccount) {
        this.writeAccount = writeAccount;
    }

    private void setWriteOffset(PreparedStatement writeOffset) {
        this.writeOffset = writeOffset;
    }

    @Override
    public AggregateEventTag<AccountEvent> aggregateTag() {
        return AccountEventTag.INSTANCE;
    }

    @Override
    public CompletionStage<Optional<UUID>> prepare(CassandraSession session) {
        return prepareCreateTables(session)
                .thenCompose(x -> prepareCustomCodecs(session))
                .thenCompose(x -> prepareWriteAccount(session))
                .thenCompose(x -> prepareWriteOffset(session))
                .thenCompose(x -> selectOffset(session));
    }

    private CompletionStage<Done> prepareCustomCodecs(CassandraSession session) {
        return session.underlying().thenApply(x -> {
            x.getCluster().getConfiguration().getCodecRegistry().register(new JacksonJsonCodec<>(Person.class)).register(new OptionalCodec<>(TypeCodec.varchar()));
            return Done.getInstance();
        });
    }

    @Override
    public EventHandlers defineEventHandlers(EventHandlersBuilder builder) {
        builder.setEventHandler(AccountCreated.class, this::processAccountCreatedEvent);
        return builder.build();
    }

    private CompletionStage<List<BoundStatement>> processAccountCreatedEvent(AccountCreated e, UUID offset) {
        BoundStatement boundStatement = writeAccount.bind();
        boundStatement.setString("id", e.id());
        String salt = BCrypt.gensalt();
        boundStatement.setString("password", e.password());
        boundStatement.setString("password_salt", e.passwordSalt());
        boundStatement.set("owner", e.owner(), Person.class);
        return completedStatements(Arrays.asList(boundStatement, writeOffset.bind(offset)));
    }


    private CompletionStage<Done> prepareCreateTables(CassandraSession session) {
        // @formatter:off
        return session.executeCreateTable(
                "CREATE TABLE IF NOT EXISTS account (id text, password text, password_salt text, owner varchar, PRIMARY KEY (id))")
                .thenCompose(y -> session.executeCreateTable(
                        "CREATE TABLE IF NOT EXISTS account_offset (partition int, offset timeuuid, PRIMARY KEY (partition))"));
        // @formatter:on
    }

    private CompletionStage<Done> prepareWriteAccount(CassandraSession session) {
        return session.prepare("INSERT INTO account (id, password, password_salt, owner) VALUES (?, ?, ?, ?)")
                .thenApply(ps -> {
                    setWriteAccount(ps);
                    return Done.getInstance();
                });
    }

    private CompletionStage<Done> prepareWriteOffset(CassandraSession session) {
        return session.prepare("INSERT INTO account_offset (partition, offset) VALUES (1, ?)").thenApply(ps -> {
            setWriteOffset(ps);
            return Done.getInstance();
        });
    }

    private CompletionStage<Optional<UUID>> selectOffset(CassandraSession session) {
        return session.selectOne("SELECT offset FROM account_offset")
                .thenApply(
                        optionalRow -> optionalRow.map(r -> r.getUUID("offset")));
    }
}
