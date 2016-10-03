package com.jalarbee.aleef.account;

import akka.Done;
import akka.NotUsed;
import akka.stream.javadsl.Source;
import com.google.inject.Inject;
import com.jalarbee.aleef.product.ProductService;
import com.jalarbee.aleef.product.model.PricingModif;
import com.jalarbee.aleef.product.model.Product;
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
public class ProductServiceImpl implements ProductService {

    private final Logger log = LoggerFactory.getLogger(ProductServiceImpl.class);

    private final PersistentEntityRegistry persistentEntityRegistry;
    private final PubSubRegistry topics;
    private final CassandraSession db;


    @Inject
    public ProductServiceImpl(PersistentEntityRegistry persistentEntityRegistry, CassandraReadSide readSide, PubSubRegistry topics, CassandraSession db) {
        this.persistentEntityRegistry = persistentEntityRegistry;
        this.topics = topics;
        this.db = db;
        persistentEntityRegistry.register(ProductEntity.class);
        readSide.register(ProductEventProcessor.class);
    }

    @Override
    public ServiceCall<Product, Done> create() {

        return request -> {
            PubSubRef<Product> topic = topics.refFor(TopicId.of(Product.class, "products"));
            topic.publish(request);
            PersistentEntityRef<ProductCommand> entityRef = persistentEntityRegistry.refFor(ProductEntity.class, request.id().toString());
            return entityRef.ask(CreateProduct.of(request));
        };
    }

    @Override
    public ServiceCall<NotUsed, Source<Product, ?>> stream() {
        return request -> {
            PubSubRef<Product> topic = topics.refFor(TopicId.of(Product.class, "products"));
            return CompletableFuture.completedFuture(topic.subscriber());
        };
    }

    @Override
    public ServiceCall<PricingModif, Done> newPricing(String id) {
        return req -> db.executeWrite("update products set pricing = ? where id = ?", req.pricing(), id);
    }
}
