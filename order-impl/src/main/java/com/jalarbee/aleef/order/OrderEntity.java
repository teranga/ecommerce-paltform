package com.jalarbee.aleef.order;

import akka.Done;
import com.lightbend.lagom.javadsl.persistence.PersistentEntity;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * @author Abdoulaye Diallo
 */
public class OrderEntity extends PersistentEntity<OrderCommand, OrderEvent, OrderState> {

    @Override
    public Behavior initialBehavior(Optional<OrderState> snapshotState) {

        BehaviorBuilder behaviorBuilder = newBehaviorBuilder(snapshotState.orElse(initialOrderState()));
        behaviorBuilder.setCommandHandler(CreateOrder.class, (cmd, ctx) -> {
            OrderCreated orderCreated = OrderCreated.builder().order(cmd.order()).id(cmd.order().id()).build();
            return ctx.thenPersist(orderCreated, x -> ctx.reply(Done.getInstance()));
        });
        behaviorBuilder.setEventHandler(OrderCreated.class, event -> state().withOrder(event.order()).withTimestamp(LocalDateTime.now())
);
        return behaviorBuilder.build();
    }

    private OrderState initialOrderState() {
        return OrderState.builder()
                .order(null)
                .timestamp(LocalDateTime.MIN)
                .build();
    }

}
