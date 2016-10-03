package com.jalarbee.aleef.product.model;

import com.google.common.collect.ImmutableMap;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 *
 * create type pricing (fromtime timestamp, totime timestamp, pricing map<text, tuple<frozen<decimal, decimal, decimal>>>);
 *
 * @author Abdoulaye Diallo
 */
public class Pricing {

    private final LocalDateTime fromTime;
    private final LocalDateTime toTime;
    private final ImmutableMap<Unit, Price> pricing;

    public Pricing(@NotNull LocalDateTime fromTime, LocalDateTime toTime, ImmutableMap<Unit, Price> pricing) {
        this.fromTime = fromTime;
        this.toTime = toTime;
        this.pricing = pricing;
    }

    public final Optional<LocalDateTime> toTime() {
        return Optional.ofNullable(toTime);
    }

    public final LocalDateTime fromTime() {
        return fromTime;
    }

    public final ImmutableMap<Unit, Price> pricing() {
        return pricing;
    }

    public static class Builder {
        private Map<Unit, Price> pricing = new HashMap<>();
        private LocalDateTime from;
        private LocalDateTime to;

        public Builder(@NotNull LocalDateTime from, LocalDateTime to) {
            this.from = from;
            this.to = to;
        }

        public Builder() {
            this.from = LocalDateTime.now();
        }

        public Builder add(Unit u, Price p) {
            pricing.put(u, p);
            return this;
        }

        public Pricing build() {
            return new Pricing(from, to, ImmutableMap.copyOf(pricing));
        }
    }
}

