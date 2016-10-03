package com.jalarbee.aleef.order;

import akka.Done;
import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.PreparedStatement;
import com.jalarbee.aleef.order.model.Account;
import com.lightbend.lagom.javadsl.persistence.AggregateEventTag;
import com.lightbend.lagom.javadsl.persistence.cassandra.CassandraReadSideProcessor;
import com.lightbend.lagom.javadsl.persistence.cassandra.CassandraSession;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletionStage;

/**
 * @author Abdoulaye Diallo
 */
public class OrderEventProcessor extends CassandraReadSideProcessor<OrderEvent> {

    private PreparedStatement writeOrder = null; // initialized in prepare
    private PreparedStatement writeOffset = null; // initialized in prepare


    public void setWriteOrder(PreparedStatement writeOrder) {
        this.writeOrder = writeOrder;
    }

    public void setWriteOffset(PreparedStatement writeOffset) {
        this.writeOffset = writeOffset;
    }

    @Override
    public AggregateEventTag aggregateTag() {
        return OrderEventTag.INSTANCE;
    }

    @Override
    public CompletionStage<Optional<UUID>> prepare(CassandraSession session) {
        return prepareCreateTables(session)
                .thenCompose(x -> prepareWriteOrder(session))
                .thenCompose(x -> prepareWriteOffset(session))
                .thenCompose(x -> selectOffset(session));
    }

    private CompletionStage<Optional<UUID>> selectOffset(CassandraSession session) {
        return session.selectOne("select offset from orders_offset").thenApply(optionalRow -> optionalRow.map(r -> r.getUUID("offset")));
    }

    private CompletionStage<Done> prepareWriteOffset(CassandraSession session) {
        return session.prepare("insert into orders_offset(partition, offset) values (1, ?)").thenApply(ps -> {
            setWriteOffset(ps);
            return Done.getInstance();
        });
    }

    private CompletionStage<Done> prepareWriteOrder(CassandraSession session) {
        return session.prepare("insert into orders(id, openTime, closeTime, lineItems, originator, beneficiary) values(?, ?, ?, ?, ?, ?)").thenApply(
                ps -> {
                    setWriteOrder(ps);
                    return Done.getInstance();
                }
        );
    }

    private CompletionStage<Done> prepareCreateTables(CassandraSession session) {
        // @formatter:off
        return session.executeCreateTable("CREATE TYPE IF NOT EXISTS person(firstName text, lastName text, email text, cellPhone text)")
                .thenCompose(x -> session.executeCreateTable("CREATE TYPE IF NOT EXISTS account(accountId uuid, owner frozen<person>)"))
                .thenCompose(x -> session.executeCreateTable("CREATE TYPE IF NOT EXISTS price(buy decimal, sell decimal, expenses decimal)"))
                .thenCompose(x -> session.executeCreateTable("CREATE TYPE IF NOT EXISTS lineitem(id uuid, productId uuid, name text, price frozen<price>, unit text, quantity double)"))
                .thenCompose(x -> session.executeCreateTable("CREATE TABLE IF NOT EXISTS orders(id uuid primary key, openTime timestamp, closeTime timestamp, lastUpdateTime timestamp, lineItems frozen<list<lineitem>>, originator frozen<account>, beneficiary frozen<account>)"))
                .thenCompose(x -> session.executeCreateTable("CREATE TABLE IF NOT EXISTS orders_offset(partition int, offset timeuuid, PRIMARY KEY (partition))"));
        // @formatter:on
    }

    @Override
    public EventHandlers defineEventHandlers(EventHandlersBuilder builder) {
        builder.setEventHandler(OrderCreated.class, this::processOrderCreatedEvent);
        return builder.build();
    }

    //id, openTime, lineItems, originator, beneficiary, closeTime, lastUpdateTime
    private CompletionStage<List<BoundStatement>> processOrderCreatedEvent(OrderCreated event, UUID offset) {
        BoundStatement statement = writeOrder.bind();
        statement.setUUID("id", UUID.fromString(event.id()));
        statement.set("openTime", event.order().openTime(), LocalDateTime.class);
        statement.set("closeTime", event.order().closeTime(), LocalDateTime.class);
        statement.setList("lineItems", event.order().lineItems());
        statement.set("originator", event.order().originator(), Account.class);
        statement.set("beneficiary", event.order().beneficiary(), Account.class);
        return completedStatements(Arrays.asList(statement, writeOffset.bind(offset)));
    }
}
