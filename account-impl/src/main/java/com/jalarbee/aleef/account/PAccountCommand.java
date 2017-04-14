package com.jalarbee.aleef.account;

import akka.Done;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.jalarbee.aleef.account.api.model.Account;
import com.jalarbee.aleef.account.model.PAccount;
import com.lightbend.lagom.javadsl.persistence.PersistentEntity;
import com.lightbend.lagom.serialization.Jsonable;
import lombok.Value;

import java.time.Duration;
import java.util.Optional;
import java.util.UUID;

/**
 * @author Abdoulaye Diallo
 */

interface PAccountCommand extends Jsonable {

    @Value
    class CreateAccount implements PAccountCommand, PersistentEntity.ReplyType<Done> {

        private final PAccount account;

        @JsonCreator
        public CreateAccount(PAccount account) {
            this.account = account;
        }
   }

    enum GetAccount implements PAccountCommand, PersistentEntity.ReplyType<Optional<Account>> {
        INSTANCE
    }

    @Value
    class AdminGetAccount implements PAccountCommand, PersistentEntity.ReplyType<Optional<Account>> {

        private final String id;

        @JsonCreator
        public AdminGetAccount(String id) {
            this.id = id;
        }
    }

    @Value
    class VerifyAccount implements PAccountCommand, PersistentEntity.ReplyType<Done> {

        private final String id;
        private final UUID token;

        @JsonCreator
        public VerifyAccount(String id, UUID token) {
            this.id = id;
            this.token = token;
        }
    }

    @Value
    class DeleteAccount implements PAccountCommand, PersistentEntity.ReplyType<Done> {

        private final String id;

        @JsonCreator
        public DeleteAccount(String id) {
            this.id = id;
        }

    }

    @Value
    class SuspendAccount implements PAccountCommand, PersistentEntity.ReplyType<Done> {

        private final String id;
        private final Duration duration;

        @JsonCreator
        public SuspendAccount(String id, Duration duration) {
            this.id = id;
            this.duration = duration;
        }
    }

    @Value
    class LiftSuspension implements PAccountCommand, PersistentEntity.ReplyType<Done> {

        private final String id;

        @JsonCreator
        public LiftSuspension(String id) {
            this.id = id;
        }
    }
}
