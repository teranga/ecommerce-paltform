package com.jalarbee.aleef.account;

import akka.actor.ActorSystem;
import akka.testkit.JavaTestKit;
import com.jalarbee.aleef.account.model.PAccount;
import com.jalarbee.aleef.account.model.PAccountStatus;
import com.jalarbee.aleef.account.model.PPerson;
import com.lightbend.lagom.javadsl.testkit.PersistentEntityTestDriver;
import com.lightbend.lagom.javadsl.testkit.PersistentEntityTestDriver.Outcome;
import org.junit.*;

import java.time.Duration;
import java.util.Optional;
import java.util.UUID;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.matchers.JUnitMatchers.hasItem;

public class AccountEntityTest {

    private static ActorSystem system;

    @BeforeClass
    public static void startActorSystem() {
        system = ActorSystem.create("AccountEntityTest");
    }

    @AfterClass
    public static void shutdownActorSystem() {
        JavaTestKit.shutdownActorSystem(system);
        system = null;
    }

    private PersistentEntityTestDriver<PAccountCommand, PAccountEvent, PAccountState> driver;

    private final String accountId = "123";
    private final PPerson owner = new PPerson("Mamadou", "Diallo", Optional.empty(), "lamine@namasarl.com", "99898899");

    private PAccount newAccount(final String accountId) {
        return new PAccount(accountId, owner);
    }

    @Before
    public void createTestDriver() {
        driver = new PersistentEntityTestDriver<>(system, new AccountEntity(), accountId);
    }

    @After
    public void noIssues() {
        if (!driver.getAllIssues().isEmpty()) {
            driver.getAllIssues().forEach(System.out::println);
            System.out.println("There were issues " + driver.getAllIssues().get(0));
        }
    }

    @Test
    public void createAccount() {
        final PAccount account = newAccount(accountId);
        Outcome<PAccountEvent, PAccountState> outcome = driver.run(new PAccountCommand.CreateAccount(account));

        assertThat(outcome.state().getAccount(), equalTo(account));
        assertThat(outcome.state().getAccount().getStatus(), equalTo(PAccountStatus.CREATED));
        assertThat(outcome.state().getAccount().getOwner(), equalTo(account.getOwner()));
        assertThat(outcome.events(), hasItem(new PAccountEvent.AccountCreated(account, null)));
    }

    @Test
    public void activateAccount() {
        Outcome<PAccountEvent, PAccountState> outcome = driver.run(new PAccountCommand.CreateAccount(newAccount(accountId)));
        assertThat(outcome.state().getAccount().getStatus(), equalTo(PAccountStatus.CREATED));

        outcome = driver.run(new PAccountCommand.VerifyAccount(accountId, UUID.randomUUID()));

        assertThat(outcome.state().getAccount().getStatus(), equalTo(PAccountStatus.VERIFIED));
    }

    @Test
    public void suspendAccount() {
        Outcome<PAccountEvent, PAccountState> outcome = driver.run(new PAccountCommand.CreateAccount(newAccount(accountId)));
        assertThat(outcome.state().getAccount().getStatus(), equalTo(PAccountStatus.CREATED));

        outcome = driver.run(new PAccountCommand.VerifyAccount(accountId, UUID.randomUUID()));

        assertThat(outcome.state().getAccount().getStatus(), equalTo(PAccountStatus.VERIFIED));

        outcome = driver.run(new PAccountCommand.SuspendAccount(accountId, Duration.ofSeconds(60)));

        assertThat(outcome.state().getAccount().getStatus(), equalTo(PAccountStatus.SUSPENDED));

    }

    @Test
    public void liftAccountSuspension() {
        Outcome<PAccountEvent, PAccountState> outcome = driver.run(new PAccountCommand.CreateAccount(newAccount(accountId)));
        assertThat(outcome.state().getAccount().getStatus(), equalTo(PAccountStatus.CREATED));

        outcome = driver.run(new PAccountCommand.VerifyAccount(accountId, UUID.randomUUID()));
        assertThat(outcome.state().getAccount().getStatus(), equalTo(PAccountStatus.VERIFIED));

        outcome = driver.run(new PAccountCommand.SuspendAccount(accountId, Duration.ofSeconds(60)));
        assertThat(outcome.state().getAccount().getStatus(), equalTo(PAccountStatus.SUSPENDED));

        outcome = driver.run(new PAccountCommand.SuspendAccount(accountId, Duration.ofSeconds(60)));
        assertThat(outcome.state().getAccount().getStatus(), equalTo(PAccountStatus.SUSPENDED));

        outcome = driver.run(new PAccountCommand.LiftSuspension(accountId));
        assertThat(outcome.state().getAccount().getStatus(), equalTo(PAccountStatus.VERIFIED));

    }


}
