package com.jalarbee.aleef.account;


import akka.Done;
import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.PreparedStatement;
import com.jalarbee.aleef.product.model.Pricing;
import com.lightbend.lagom.javadsl.persistence.AggregateEventTag;
import com.lightbend.lagom.javadsl.persistence.cassandra.CassandraReadSideProcessor;
import com.lightbend.lagom.javadsl.persistence.cassandra.CassandraSession;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletionStage;

/**
 * @author Abdoulaye Diallo
 */
public class ProductEventProcessor extends CassandraReadSideProcessor<ProductEvent> {


    private PreparedStatement writeProduct = null; // initialized in prepare
    private PreparedStatement writeOffset = null; // initialized in prepare

    public void setWriteProduct(PreparedStatement writeProduct) {
        this.writeProduct = writeProduct;
    }

    public void setWriteOffset(PreparedStatement writeOffset) {
        this.writeOffset = writeOffset;
    }

    @Override
    public AggregateEventTag<ProductEvent> aggregateTag() {
        return ProductEventTag.INSTANCE;
    }

    @Override
    public CompletionStage<Optional<UUID>> prepare(CassandraSession session) {
        return prepareCreateTables(session)
                .thenCompose(x -> prepareWriteProduct(session))
                .thenCompose(x -> prepareWriteOffset(session))
                .thenCompose(x -> selectOffset(session));
    }

    @Override
    public EventHandlers defineEventHandlers(EventHandlersBuilder builder) {
        builder.setEventHandler(ProductCreated.class, this::processProductCreatedEvent);
        return builder.build();
    }

    private CompletionStage<List<BoundStatement>> processProductCreatedEvent(ProductCreated event, UUID offset) {
        BoundStatement boundStatement = writeProduct.bind();
        boundStatement.setString("name", event.product().name());
        boundStatement.setString("description", event.product().description().orElse(null));
        boundStatement.set("pricing", event.product().pricing(), Pricing.class);
        boundStatement.setList("pricinghistory", event.product().pricingHistory(), Pricing.class);
        boundStatement.setUUID("id", UUID.fromString(event.product().id()));
        return completedStatements(Arrays.asList(boundStatement, writeOffset.bind(offset)));
    }

    private CompletionStage<Optional<UUID>> selectOffset(CassandraSession session) {
        return session.selectOne("select offset from products_offset").thenApply(x -> x.map(r -> r.getUUID("offset")));
    }

    private CompletionStage<Done> prepareWriteOffset(CassandraSession session) {
        return session.prepare("INSERT INTO products_offset (partition, offset) VALUES (1, ?)").thenApply(ps -> {
            setWriteOffset(ps);
            return Done.getInstance();
        });
    }

    private CompletionStage<Done> prepareWriteProduct(CassandraSession session) {
        return session.prepare("INSERT INTO products (id, name, pricing, description, pricinghistory) VALUES (?, ?, ?, ?, ?)")
                .thenApply(ps -> {
                    setWriteProduct(ps);
                    return Done.getInstance();
                });
    }

    private CompletionStage<Done> prepareCreateTables(CassandraSession session) {
        // @formatter:off
        return session.executeCreateTable("CREATE TYPE IF NOT EXISTS pricing (fromtime timestamp, totime timestamp, pricing map<text, frozen<tuple<decimal, decimal, decimal>>>)")
                .thenCompose(a -> session.executeCreateTable(
                        "CREATE TABLE IF NOT EXISTS products (id text, name text, description text, pricing frozen<pricing>, pricinghistory frozen<list<pricing>>, PRIMARY KEY (id))"))
                .thenCompose(a -> session.executeCreateTable(
                        "CREATE TABLE IF NOT EXISTS products_offset (partition int, offset timeuuid, PRIMARY KEY (partition))"));
        // @formatter:on
    }

}
