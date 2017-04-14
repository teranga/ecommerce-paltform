package com.jalarbee.aleef.account;

import akka.Done;
import com.datastax.driver.core.BoundStatement;
import com.lightbend.lagom.javadsl.persistence.AggregateEventTag;
import com.lightbend.lagom.javadsl.persistence.Offset;

import java.util.List;
import java.util.concurrent.CompletionStage;

public class CassandraProductDatabase implements ProductDatabase {


    @Override
    public CompletionStage<Done> createTables() {
        return null;
    }

    @Override
    public CompletionStage<Offset> loadOffset(AggregateEventTag<ProductEvent> tag) {
        return null;
    }

    @Override
    public CompletionStage<List<BoundStatement>> handle(ProductCreated event, Offset offset) {
        return null;
    }
}
