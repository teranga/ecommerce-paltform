package com.jalarbee.aleef.account;

import akka.Done;
import com.jalarbee.aleef.product.model.Pricing;
import com.jalarbee.aleef.product.model.Product;
import com.lightbend.lagom.javadsl.persistence.PersistentEntity;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

/**
 * @author Abdoulaye Diallo
 */
public class ProductEntity extends PersistentEntity<ProductCommand, ProductEvent, ProductState> {

    @Override
    public Behavior initialBehavior(Optional<ProductState> snapshotState) {
        //@formatter:off
        BehaviorBuilder behaviorBuilder = newBehaviorBuilder(snapshotState.orElse(
                ProductState.builder()
                        .timestamp(LocalDateTime.now())
                        .product(
                                Product.builder()
                                        .id(UUID.randomUUID().toString())
                                        .name("")
                                        .description("")
                                        .pricing(new Pricing.Builder().build())
                                        .build())
                        .build()));
        //@formatter:on
        behaviorBuilder.setCommandHandler(CreateProduct.class, (cmd, ctx) -> {
            ProductCreated event = ProductCreated.builder().id(UUID.fromString(cmd.product().id())).product(cmd.product()).build();
            return ctx.thenPersist(event, x -> ctx.reply(Done.getInstance()));
        });

        behaviorBuilder.setEventHandler(ProductCreated.class,
                event -> state().withProduct(event.product()).withTimestamp(LocalDateTime.now()));

        return behaviorBuilder.build();
    }
}
