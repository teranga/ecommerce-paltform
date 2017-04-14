package com.jalarbee.aleef.account;

import akka.Done;
import com.jalarbee.aleef.account.api.model.Account;
import com.jalarbee.aleef.account.model.PAccountStatus;
import com.lightbend.lagom.javadsl.persistence.PersistentEntity;

import java.time.Instant;
import java.util.Optional;

/**
 * @author Abdoulaye Diallo
 */
public class AccountEntity extends PersistentEntity<PAccountCommand, PAccountEvent, PAccountState> {

    @Override
    public Behavior initialBehavior(Optional<PAccountState> snapshot) {

        PAccountStatus status = snapshot.map(PAccountState::getStatus).orElse(PAccountStatus.NOT_CREATED);

        switch (status) {
            case NOT_CREATED:
                return empty();
            case CREATED:
                return created(snapshot.get());
            case VERIFIED:
                return verified(snapshot.get());
            case DELETED:
                return deleted(snapshot.get());
            case SUSPENDED:
                return suspended(snapshot.get());
            default:
                throw new IllegalStateException("unknown status: " + status);
        }

    }

    private Behavior empty() {

        BehaviorBuilder builder = newBehaviorBuilder(PAccountState.empty());
        builder.setCommandHandler(PAccountCommand.CreateAccount.class,
                (cmd, ctx)
                        -> persistAndDone(ctx, new PAccountEvent.AccountCreated(cmd.getAccount(), Instant.now()))
        );

        builder.setEventHandlerChangingBehavior(PAccountEvent.AccountCreated.class, ev -> created(state().initAccount(ev.getAccount())));
        return builder.build();
    }


    private Behavior created(PAccountState state) {

        BehaviorBuilder builder = newBehaviorBuilder(state);
        builder.setReadOnlyCommandHandler(PAccountCommand.GetAccount.class, this::getAccount);
        builder.setCommandHandler(PAccountCommand.VerifyAccount.class, this::verifyAccount);

        builder.setEventHandlerChangingBehavior(PAccountEvent.AccountVerified.class, ev -> verified(state().updateStatus(PAccountStatus.VERIFIED)));

        return builder.build();
    }

    private Behavior verified(PAccountState state) {

        BehaviorBuilder builder = newBehaviorBuilder(state);
        builder.setCommandHandler(PAccountCommand.DeleteAccount.class, this::deleteAccount);
        builder.setCommandHandler(PAccountCommand.SuspendAccount.class, this::suspendAccount);

        builder.setReadOnlyCommandHandler(PAccountCommand.GetAccount.class, this::getAccount);

        builder.setEventHandlerChangingBehavior(PAccountEvent.AccountSuspended.class, ev -> suspended(state().updateStatus(PAccountStatus.SUSPENDED)));
        builder.setEventHandlerChangingBehavior(PAccountEvent.AccountDeleted.class, ev -> deleted(state().updateStatus(PAccountStatus.DELETED)));

        return builder.build();

    }


    private Behavior suspended(PAccountState state) {

        BehaviorBuilder builder = newBehaviorBuilder(state);
        builder.setReadOnlyCommandHandler(PAccountCommand.GetAccount.class, this::ignoreGetAccount);
        builder.setReadOnlyCommandHandler(PAccountCommand.AdminGetAccount.class, this::adminGetAccount);
        builder.setCommandHandler(PAccountCommand.LiftSuspension.class, this::liftSuspension);

        builder.setEventHandlerChangingBehavior(PAccountEvent.SuspensionLifted.class, ev -> verified(state().updateStatus(PAccountStatus.VERIFIED)));

        return builder.build();
    }

    private Behavior deleted(PAccountState state) {

        BehaviorBuilder builder = newBehaviorBuilder(state);
        builder.setReadOnlyCommandHandler(PAccountCommand.GetAccount.class, this::ignoreGetAccount);
        builder.setReadOnlyCommandHandler(PAccountCommand.AdminGetAccount.class, this::adminGetAccount);

        return builder.build();
    }

    private Persist<PAccountEvent.SuspensionLifted> liftSuspension(PAccountCommand.LiftSuspension cmd, CommandContext<Done> ctx) {
        return persistAndDone(ctx, new PAccountEvent.SuspensionLifted(cmd.getId(), Instant.now()));
    }

    private Persist<PAccountEvent.AccountDeleted> deleteAccount(PAccountCommand.DeleteAccount cmd, CommandContext<Done> ctx) {
        return persistAndDone(ctx, new PAccountEvent.AccountDeleted(cmd.getId(), Instant.now()));
    }

    private Persist<PAccountEvent.AccountSuspended> suspendAccount(PAccountCommand.SuspendAccount cmd, CommandContext<Done> ctx) {
        return persistAndDone(ctx, new PAccountEvent.AccountSuspended(cmd.getId()));
    }

    private void ignoreGetAccount(PAccountCommand.GetAccount cmd, ReadOnlyCommandContext<Optional<Account>> ctx) {
        ctx.reply(Optional.empty());
    }

    private Optional<Account> getAccount(PAccountCommand.GetAccount get, ReadOnlyCommandContext<Optional<Account>> ctx) {
        return Optional.of(state().getAccount().toAccount());
    }

    private Optional<Account> adminGetAccount(PAccountCommand.AdminGetAccount get, ReadOnlyCommandContext<Optional<Account>> ctx) {
        return Optional.of(state().getAccount().toAccount());
    }

    private Persist<PAccountEvent.AccountVerified> verifyAccount(PAccountCommand.VerifyAccount cmd, CommandContext<Done> ctx) {
        return persistAndDone(ctx, new PAccountEvent.AccountVerified(cmd.getId()));
    }

    private <U extends PAccountEvent>Persist<U> persistAndDone(CommandContext<Done> ctx, U event) {
        return ctx.thenPersist(event, (e) -> ctx.reply(Done.getInstance()));
    }

}
