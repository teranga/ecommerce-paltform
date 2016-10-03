package com.jalarbee.aleef.account;

import akka.Done;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.jalarbee.aleef.account.model.Registration;
import com.lightbend.lagom.javadsl.immutable.ImmutableStyle;
import com.lightbend.lagom.javadsl.persistence.PersistentEntity;
import com.lightbend.lagom.serialization.CompressedJsonable;
import com.lightbend.lagom.serialization.Jsonable;
import org.immutables.value.Value;

/**
 * @author Abdoulaye Diallo
 */
public interface AccountCommand extends Jsonable {

    @Value.Immutable
    @ImmutableStyle
    @JsonDeserialize(as = CreateAccount.class)
    public static interface AbstractCreateAccount extends AccountCommand, CompressedJsonable, PersistentEntity.ReplyType<Done> {

        @Value.Parameter
        Registration registration();
    }
}
