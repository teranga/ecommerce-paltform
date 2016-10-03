package com.jalarbee.aleef.order;

import akka.Done;
import akka.NotUsed;
import akka.stream.javadsl.Source;
import com.jalarbee.aleef.order.model.Order;
import com.lightbend.lagom.javadsl.api.Descriptor;
import com.lightbend.lagom.javadsl.api.Service;
import com.lightbend.lagom.javadsl.api.ServiceCall;
import com.lightbend.lagom.javadsl.api.transport.Method;

import static com.lightbend.lagom.javadsl.api.Service.*;

/**
 * @author Abdoulaye Diallo
 */
public interface OrderService extends Service {

    @Override
    default Descriptor descriptor() {
        return named("orderService").withCalls(
                restCall(Method.POST, "/api/orders", this::create),
                pathCall("/api/orders", this::stream)

        ).withAutoAcl(true);
    }

    ServiceCall<Order, Done> create();

    ServiceCall<NotUsed, Source<Order, ?>> stream();
}
