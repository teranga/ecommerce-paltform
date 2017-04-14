package com.jalarbee.aleef.account;

import akka.Done;
import com.datastax.driver.core.BoundStatement;
import com.lightbend.lagom.javadsl.persistence.AggregateEventTag;
import com.lightbend.lagom.javadsl.persistence.Offset;

import java.util.List;
import java.util.concurrent.CompletionStage;

public interface ProductDatabase {

    CompletionStage<Done> createTables();

    CompletionStage<Offset> loadOffset(AggregateEventTag<ProductEvent> tag);

    CompletionStage<List<BoundStatement>> handle(ProductCreated event, Offset offset);

}
