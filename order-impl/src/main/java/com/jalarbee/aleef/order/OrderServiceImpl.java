package com.jalarbee.aleef.order;

import akka.Done;
import akka.NotUsed;
import akka.stream.javadsl.Source;
import com.google.inject.Inject;
import com.jalarbee.aleef.order.model.Order;
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

import java.util.concurrent.CompletableFuture;

/**
 * @author Abdoulaye Diallo
 */
public class OrderServiceImpl implements OrderService {

    private final Logger log = LoggerFactory.getLogger(OrderServiceImpl.class);

    private final PersistentEntityRegistry persistentEntityRegistry;
    private final PubSubRegistry topics;
    private final CassandraSession db;

    @Inject
    public OrderServiceImpl(PersistentEntityRegistry persistentEntityRegistry, CassandraReadSide readSide, PubSubRegistry topics, CassandraSession db) {
        this.persistentEntityRegistry = persistentEntityRegistry;
        this.topics = topics;
        this.db = db;
        persistentEntityRegistry.register(OrderEntity.class);
        readSide.register(OrderEventProcessor.class);
    }

    @Override
    public ServiceCall<Order, Done> create() {
        return request -> {
            PubSubRef<Order> topic = topics.refFor(TopicId.of(Order.class, "topic"));
            topic.publish(request);
            log.info("order created - id = : {}", request.id());
            PersistentEntityRef<OrderCommand> entityRef = persistentEntityRegistry.refFor(OrderEntity.class, request.id());
            return entityRef.ask(CreateOrder.of(request));
        };
    }

    @Override
    public ServiceCall<NotUsed, Source<Order, ?>> stream() {
        return request -> {
            PubSubRef<Order> topic = topics.refFor(TopicId.of(Order.class, "topic"));
            return CompletableFuture.completedFuture(topic.subscriber());
        };
    }
}
