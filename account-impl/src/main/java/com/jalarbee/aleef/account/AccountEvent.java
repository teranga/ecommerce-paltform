package com.jalarbee.aleef.account;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.jalarbee.aleef.account.model.Person;
import com.lightbend.lagom.javadsl.immutable.ImmutableStyle;
import com.lightbend.lagom.javadsl.persistence.AggregateEvent;
import com.lightbend.lagom.javadsl.persistence.AggregateEventTag;
import com.lightbend.lagom.serialization.Jsonable;
import org.immutables.value.Value;

/**
 * @author Abdoulaye Diallo
 */
public interface AccountEvent extends Jsonable, AggregateEvent<AccountEvent> {

    @Value.Immutable
    @ImmutableStyle
    @JsonDeserialize(as = AccountCreated.class)
    interface AbstractAccountCreated extends AccountEvent {

        @Override
        default AggregateEventTag<AccountEvent> aggregateTag() {
            return AccountEventTag.INSTANCE;
        }

        @Value.Parameter
        String id();

        @Value.Parameter
        String password();

        @Value.Parameter
        String passwordSalt();

        @Value.Parameter
        Person owner();
    }
}
