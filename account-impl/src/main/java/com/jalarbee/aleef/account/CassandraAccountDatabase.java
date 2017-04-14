package com.jalarbee.aleef.account;

/**
 * @author Abdoulaye Diallo
 */
public class CassandraAccountDatabase {

//
//    private PreparedStatement writeAccount = null; // initialized in prepare
//    private final CassandraSession cassandraSession;
//
//    private final static String ACCOUNT_OFFSET_ID = "accountoffset";
//
//    @Inject
//    public CassandraAccountDatabase(CassandraSession cassandraSession) {
//        this.cassandraSession = cassandraSession;
//    }
//
//    @Override
//    public CompletionStage<Done> createTables() {
//        return prepareCreateTables(cassandraSession);
//    }
//
//    @Override
//    public CompletionStage<Offset> loadOffset(AggregateEventTag<PAccountEvent> tag) {
//        return  prepareCustomCodecs(cassandraSession)
//                .thenCompose(x -> prepareWriteAccount(cassandraSession))
//                .thenCompose(x -> selectOffset(cassandraSession, tag));
//    }
//
//    @Override
//    public CompletionStage<List<BoundStatement>> handle(AccountCreated event, Offset offset) {
//        return processAccountCreatedEvent(event);
//    }
//
//    @Override
//    public CompletionStage<List<BoundStatement>> handleEvent(PAccountEvent event, Offset offset) {
//        return Flow.<Pair<PAccountEvent, Offset>>create()
//                .mapAsync(1, eventAndOffset ->
//                        this.handle(eventAndOffset.first(),
//                                eventAndOffset.second())
//                );
//    }
//
//    private void setWriteAccount(PreparedStatement writeAccount) {
//        this.writeAccount = writeAccount;
//    }
//
//    private CompletionStage<Done> prepareCustomCodecs(CassandraSession session) {
//        return session.underlying().thenApply(x -> {
//            x.getCluster().getConfiguration().getCodecRegistry().register(new JacksonJsonCodec<>(Person.class)).register(new OptionalCodec<>(TypeCodec.varchar()));
//            return Done.getInstance();
//        });
//    }
//
//    private CompletionStage<List<BoundStatement>> processAccountCreatedEvent(AccountCreated e) {
//        BoundStatement boundStatement = writeAccount.bind();
//        boundStatement.setString("id", e.id());
//        boundStatement.setString("password", e.password());
//        boundStatement.setString("password_salt", e.passwordSalt());
//        boundStatement.set("owner", e.owner(), Person.class);
//        return CassandraReadSide.completedStatement(boundStatement);
//    }
//
//
//    private CompletionStage<Done> prepareCreateTables(CassandraSession session) {
//        // @formatter:off
//        return session.executeCreateTable(
//                "CREATE TABLE IF NOT EXISTS account (id text, password text, password_salt text, owner varchar, PRIMARY KEY (id))")
//                .thenCompose(y -> session.executeCreateTable(
//                        "CREATE TABLE IF NOT EXISTS offsetStore ( eventProcessorId text, tag text, timeUuidOffset timeuuid, sequenceOffset bigint, PRIMARY KEY (eventProcessorId, tag))"));
//        // @formatter:on
//    }
//
//    private CompletionStage<Done> prepareWriteAccount(CassandraSession session) {
//        return session.prepare("INSERT INTO account (id, password, password_salt, owner) VALUES (?, ?, ?, ?)")
//                .thenApply(ps -> {
//                    setWriteAccount(ps);
//                    return Done.getInstance();
//                });
//    }
//
//    private CompletionStage<Offset> selectOffset(CassandraSession session, AggregateEventTag<PAccountEvent> tag) {
//        return session.selectOne("SELECT timeUuidOffset FROM offsetStore where eventProcessorId=? and tag=?", ACCOUNT_OFFSET_ID, tag.tag())
//                .thenApply(
//                        optionalRow -> optionalRow.map(r -> Offset.timeBasedUUID(r.getUUID("timeUuidOffset"))).orElse(Offset.NONE));
//    }
}
