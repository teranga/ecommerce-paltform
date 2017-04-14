package com.jalarbee.aleef.account;

import akka.Done;
import com.datastax.driver.core.*;
import com.datastax.driver.extras.codecs.enums.EnumNameCodec;
import com.datastax.driver.extras.codecs.jdk8.OptionalCodec;
import com.datastax.driver.extras.codecs.json.JacksonJsonCodec;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.jalarbee.aleef.account.api.model.AccountStatus;
import com.jalarbee.aleef.account.model.PAccount;
import com.jalarbee.aleef.account.model.PAccountStatus;
import com.jalarbee.aleef.account.model.PPerson;
import com.lightbend.lagom.javadsl.persistence.AggregateEventTag;
import com.lightbend.lagom.javadsl.persistence.ReadSide;
import com.lightbend.lagom.javadsl.persistence.ReadSideProcessor;
import com.lightbend.lagom.javadsl.persistence.cassandra.CassandraReadSide;
import com.lightbend.lagom.javadsl.persistence.cassandra.CassandraSession;
import org.pcollections.PSequence;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

import static com.jalarbee.aleef.account.CompletionStageUtils.*;

@Singleton
public class AccountRepository {

    private final CassandraSession session;


    @Inject
    public AccountRepository(CassandraSession session, ReadSide readSide) {
        this.session = session;
        session.underlying().thenAccept(
                s -> registerCodec(s,
                        new EnumNameCodec<>(AccountStatus.class),
                        new JacksonJsonCodec<>(PPerson.class),
                        new OptionalCodec<>(TypeCodec.varchar())));
        readSide.register(PAccountEventProcessor.class);
    }

    CompletionStage<Optional<PAccount>> getAccount(String accountId) {

        return session.selectOne(
                "SELECT * FROM account " +
                        "WHERE accountId = ? ",
                accountId
        ).thenApply(row -> row.map(this::convertAccount));

    }

    private PAccount convertAccount(Row row) {
        return new PAccount(
                row.getString("accountId"),
                row.get("owner", PPerson.class),
                row.get("status", PAccountStatus.class)
        );
    }

    private void registerCodec(Session session, TypeCodec<?>... codecs) {
        Arrays.asList(codecs).forEach(x -> session.getCluster().getConfiguration().getCodecRegistry().register(x));

    }

    private static class PAccountEventProcessor extends ReadSideProcessor<PAccountEvent> {

        private final CassandraReadSide readSide;
        private final CassandraSession session;

        private PreparedStatement insertAccountStatement;
        private PreparedStatement updateAccountStatusStatement;
        private PreparedStatement updateAccountsByEmailStatement;
        private PreparedStatement updateAccountsByCellPhoneStatement;


        public PAccountEventProcessor(CassandraReadSide readSide, CassandraSession session) {
            this.readSide = readSide;
            this.session = session;
        }

        @Override
        public ReadSideHandler<PAccountEvent> buildHandler() {
            return this.readSide.<PAccountEvent>builder("pAccountEventOffset")
                    .setGlobalPrepare(this::createTables)
                    .setPrepare(tag -> prepareStatements())
                    .setEventHandler(PAccountEvent.AccountCreated.class, e -> persistAccount(e.getAccount()))
                    .setEventHandler(PAccountEvent.AccountVerified.class, e -> updateAccountStatus(e.getId(), PAccountStatus.VERIFIED))
                    .setEventHandler(PAccountEvent.AccountSuspended.class, e -> updateAccountStatus(e.getId(), PAccountStatus.SUSPENDED))
                    .setEventHandler(PAccountEvent.SuspensionLifted.class, e -> updateAccountStatus(e.getId(), PAccountStatus.VERIFIED))
                    .setEventHandler(PAccountEvent.AccountDeleted.class, e -> updateAccountStatus(e.getId(), PAccountStatus.DELETED))
                    .build();
        }

        private CompletionStage<List<BoundStatement>> updateAccountStatus(String accountId, PAccountStatus status) {
            return completedStatements(
                    updateAccountStatusStatement.bind(
                            status,
                            accountId
                    ));
        }

        private CompletionStage<List<BoundStatement>> persistAccount(PAccount account) {
            return completedStatements(
                    insertAccount(account),
                    updateAccountsByEmail(account),
                    updateAccountsByCellPhone(account)
            );
        }

        private BoundStatement updateAccountsByCellPhone(PAccount account) {
            return updateAccountsByCellPhoneStatement.bind(
                    account.getAccountId(),
                    account.getOwner().getCellPhone()
            );
        }

        private BoundStatement updateAccountsByEmail(PAccount account) {
            return updateAccountsByEmailStatement.bind(
                    account.getAccountId(),
                    account.getOwner().getEmail()
            );
        }

        private BoundStatement insertAccount(PAccount account) {
            return insertAccountStatement.bind(
                    account.getAccountId(),
                    account.getStatus(),
                    account.getOwner()
            );
        }

        private CompletionStage<Done> prepareStatements() {
            return doAll(
                    prepareInsertAccountStatement(),
                    prepareUpdateAccountsByEmailStatement(),
                    prepareUpdateAccountsByCellPhoneStatement(),
                    prepareUpdateAccountStatusStatement()
            );
        }

        private CompletionStage<Done> prepareUpdateAccountStatusStatement() {
            return session.
                    prepare("UPDATE account(" +
                            "SET status = ? " +
                            "WHERE accountId = ? " +
                            ")"
                    )
                    .thenApply(accept(s -> updateAccountStatusStatement = s));
        }

        private CompletionStage<Done> prepareUpdateAccountsByCellPhoneStatement() {
            return session.
                    prepare("UPDATE accountsByCellPhone(" +
                            "SET accountIds = accountIds + ? " +
                            "WHERE cellPhone = ? " +
                            ")"
                    )
                    .thenApply(accept(s -> updateAccountsByCellPhoneStatement = s));
        }

        private CompletionStage<Done> prepareUpdateAccountsByEmailStatement() {
            return session.
                    prepare("UPDATE accountsByEmail(" +
                            "SET accountIds = accountIds + ? " +
                            "WHERE email = ? " +
                            ")"
                    )
                    .thenApply(accept(s -> updateAccountsByEmailStatement = s));
        }

        private CompletionStage<Done> prepareInsertAccountStatement() {
            return session.
                    prepare("INSERT INTO account(" +
                            "accountId, " +
                            "status, " +
                            "owner" +
                            ") VALUES (" +
                            "?, " + // accountId
                            "?, " + // status
                            "? " + // owner
                            ")"
                    )
                    .thenApply(accept(s -> insertAccountStatement = s));
        }

        private CompletionStage<Done> createTables() {
            return doAll(
                    session.executeCreateTable(
                            "CREATE TABLE IF NOT EXISTS account (" +
                                    "accountId text PRIMARY KEY, " +
                                    "status text, " +
                                    "owner varchar" +
                                    ")"
                    ),
                    session.executeCreateTable(
                            "CREATE TABLE IF NOT EXISTS accountsByEmail ( " +
                                    "email text PRIMARY KEY, " +
                                    "set<frozen<text>> accountIds " +
                                    ")"
                    ),
                    session.executeCreateTable(
                            "CREATE TABLE IF NOT EXISTS accountsByCellPhone (" +
                                    "cellPhone text PRIMARY KEY," +
                                    "set<frozen<text>> accountIds" +
                                    ")"
                    ),
                    session.executeCreateTable(
                            "CREATE MATERIALIZED VIEW IF NOT EXISTS accountInDeletedState AS " +
                                    "SELECT * FROM account " +
                                    "WHERE status = 'DELETED' " +
                                    "PRIMARY KEY ( accountId ) " +
                                    ")"
                    ),
                    session.executeCreateTable(
                            "CREATE MATERIALIZED VIEW IF NOT EXISTS accountInSuspendedState AS " +
                                    "SELECT * FROM account " +
                                    "WHERE status = 'SUSPENDED' " +
                                    "PRIMARY KEY (accountId) " +
                                    ")"
                    )
            );
        }

        @Override
        public PSequence<AggregateEventTag<PAccountEvent>> aggregateTags() {
            return PAccountEvent.TAG.allTags();
        }
    }
}
