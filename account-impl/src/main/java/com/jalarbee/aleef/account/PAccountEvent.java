package com.jalarbee.aleef.account;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.jalarbee.aleef.account.model.PAccount;
import com.lightbend.lagom.javadsl.persistence.AggregateEvent;
import com.lightbend.lagom.javadsl.persistence.AggregateEventShards;
import com.lightbend.lagom.javadsl.persistence.AggregateEventTag;
import com.lightbend.lagom.javadsl.persistence.AggregateEventTagger;
import com.lightbend.lagom.serialization.Jsonable;
import lombok.EqualsAndHashCode;
import lombok.Value;

import java.time.Instant;

/**
 * @author Abdoulaye Diallo
 */
public interface PAccountEvent extends Jsonable, AggregateEvent<PAccountEvent> {


    int NUM_SHARDS = 4;

    AggregateEventShards<PAccountEvent> TAG = AggregateEventTag.sharded(PAccountEvent.class, NUM_SHARDS);

    @Override
    default AggregateEventTagger<PAccountEvent> aggregateTag() {
        return TAG;
    }

    @Value
    @EqualsAndHashCode(of = "account")
    final class AccountCreated implements PAccountEvent {

        private final PAccount account;
        private final Instant createdOn;

        @JsonCreator
        public AccountCreated(PAccount account, Instant createdOn) {
            this.account = account;
            this.createdOn = createdOn;
        }
    }


    @Value
    @EqualsAndHashCode(of = "id")
    final class AccountVerified implements PAccountEvent {

        private final String id;
        private final Instant verifiedOn;

        @JsonCreator
        public AccountVerified(String id, Instant verifiedOn) {
            this.id = id;
            this.verifiedOn = verifiedOn;
        }

        public AccountVerified(String id) {
            this(id, Instant.now());
        }
    }


    @Value
    @EqualsAndHashCode(of = "id")
    final class AccountSuspended implements PAccountEvent {

        private final String id;
        private final Instant suspendedOn;

        @JsonCreator
        public AccountSuspended(String id, Instant suspendedOn) {
            this.id = id;
            this.suspendedOn = suspendedOn;
        }

        public AccountSuspended(String id) {
            this(id, Instant.now());
        }
    }


    @Value
    @EqualsAndHashCode(of = "id")
    class SuspensionLifted implements PAccountEvent {

        private final String id;
        private final Instant suspensionLiftedOn;

        @JsonCreator
        public SuspensionLifted(String id, Instant suspensionLiftedOn) {
            this.id = id;
            this.suspensionLiftedOn = suspensionLiftedOn;
        }
    }


    @Value
    @EqualsAndHashCode(of = "id")
    class AccountDeleted implements PAccountEvent {

        private final String id;
        private Instant deletedOn;

        @JsonCreator
        public AccountDeleted(String id, Instant deletedOn) {
            this.id = id;
            this.deletedOn = deletedOn;
        }
    }


}
