package com.jalarbee.aleef.account;

import akka.Done;
import com.jalarbee.aleef.account.model.Account;
import com.jalarbee.aleef.account.model.Person;
import com.lightbend.lagom.javadsl.persistence.PersistentEntity;
import org.mindrot.jbcrypt.BCrypt;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * @author Abdoulaye Diallo
 */
public class AccountEntity extends PersistentEntity<AccountCommand, AccountEvent, AccountState> {

    @Override
    public Behavior initialBehavior(Optional<AccountState> initial) {
        Account accountZero = Account.of("99999999", Person.of("Account", "Zero", "xxx@yyy.zz", "111-111-1111"));
        BehaviorBuilder b = newBehaviorBuilder(initial.orElse(AccountState.builder()
                .account(accountZero)
                .timestamp(LocalDateTime.now())
                .build()));

        b.setCommandHandler(CreateAccount.class, (cmd, ctx) -> {
            String salt = BCrypt.gensalt();
            AccountCreated accountCreated = AccountCreated.builder()
                    .owner(cmd.registration().owner().orElse(null))
                    .id(cmd.registration().login().id())
                    .password(BCrypt.hashpw(cmd.registration().login().password(), salt))
                    .passwordSalt(salt)
                    .build();
            return ctx.thenPersist(accountCreated, x -> ctx.reply(Done.getInstance()));
        });

        b.setEventHandler(AccountCreated.class,
                event ->
                        state().withAccount(Account.of(event.id(), event.owner()))
                                .withTimestamp(LocalDateTime.now()));

        return b.build();
    }
}
