package com.jalarbee.aleef.account;


import com.google.inject.Inject;
import com.lightbend.lagom.javadsl.persistence.AggregateEventTag;
import com.lightbend.lagom.javadsl.persistence.ReadSideProcessor;
import com.lightbend.lagom.javadsl.persistence.cassandra.CassandraReadSide;
import org.pcollections.PSequence;
import org.pcollections.TreePVector;

public class ProductEventProcessor extends ReadSideProcessor<ProductEvent> {

    private final ProductDatabase db;
    private final CassandraReadSide readSide;

    @Inject
    public ProductEventProcessor(ProductDatabase db, CassandraReadSide readSide) {
        this.db = db;
        this.readSide = readSide;
    }

    @Override
    public ReadSideHandler<ProductEvent> buildHandler() {
        CassandraReadSide.ReadSideHandlerBuilder<ProductEvent> builder = readSide.builder("productoffset");
        builder.setEventHandler(ProductCreated.class, db::handle);
        return builder.build();
    }

    @Override
    public PSequence<AggregateEventTag<ProductEvent>> aggregateTags() {
        return TreePVector.singleton(ProductEventTag.INSTANCE);
    }
}
