package com.jalarbee.aleef.account;

import akka.Done;
import akka.NotUsed;
import akka.stream.javadsl.Source;
import akka.stream.testkit.TestSubscriber;
import akka.stream.testkit.javadsl.TestSink;
import com.jalarbee.aleef.account.model.Account;
import com.jalarbee.aleef.account.model.Login;
import com.jalarbee.aleef.account.model.Person;
import com.jalarbee.aleef.account.model.Registration;
import org.junit.*;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.lightbend.lagom.javadsl.testkit.ServiceTest.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class AccountServiceTest {

    private static TestServer server;

    @BeforeClass
    public static void setUp() {
        server = startServer(defaultSetup());
    }

    @AfterClass
    public static void tearDown() {
        if (server != null) {
            server.stop();
            server = null;
        }
    }

    @Before
    public void init() {

    }

    @After
    public void finish() {

    }

    @Test
    public void shouldRegisterAndRetrieveNewAccount() throws Exception {
        AccountService service = server.client(AccountService.class);

        Registration registration = Registration.builder()
                .login(Login.of("2312312", "ssss"))
                .owner(Person.of("Mamadou Lamine", "Diallo", "mld@namatasarl.com", "21321312"))
                .build();

        CompletionStage<Done> result = service.register().invoke(registration);
        assertEquals(Done.getInstance(), result.toCompletableFuture().get(5, TimeUnit.SECONDS));

        TimeUnit.SECONDS.sleep(5);

        Optional<Account> remoteAccount = service.getAccount("2312312").invoke().toCompletableFuture().get(10, TimeUnit.SECONDS);

        assertTrue(remoteAccount.isPresent());
        assertTrue(remoteAccount.get().owner().equals(registration.owner().get()));
        assertTrue(remoteAccount.get().id().equals(registration.login().id()));

    }

    @Test
    public void shouldStreamRegistrations() throws Exception {
        AccountService service = server.client(AccountService.class);

        Source<Account, ?> stream1 = service.streamRegistrations().invoke(NotUsed.getInstance()).toCompletableFuture().get(15, TimeUnit.SECONDS);
        Source<Account, ?> stream2 = service.streamRegistrations().invoke(NotUsed.getInstance()).toCompletableFuture().get(15, TimeUnit.SECONDS);

        List<Registration> accounts = Stream.of("Lamine", "Sellou", "Ami", "Kaoussou").map(x -> newAccount(x)).collect(Collectors.toList());

        TestSubscriber.Probe<Account> probe1 = stream1.runWith(TestSink.probe(server.system()), server.materializer());
        probe1.request(10);

        service.register().invoke(accounts.get(0)).toCompletableFuture().get(15, TimeUnit.SECONDS);
        service.register().invoke(accounts.get(1)).toCompletableFuture().get(15, TimeUnit.SECONDS);
        service.register().invoke(accounts.get(2)).toCompletableFuture().get(15, TimeUnit.SECONDS);

        TestSubscriber.Probe<Account> probe2 = stream2.runWith(TestSink.probe(server.system()), server.materializer());
        probe2.request(10);

        service.register().invoke(accounts.get(3)).toCompletableFuture().get(15, TimeUnit.SECONDS);

        probe1.expectNext(toAccount(accounts.get(0)));
        probe2.expectNext(toAccount(accounts.get(0)));
        probe2.cancel();
        probe1.expectNext(toAccount(accounts.get(1)));
        probe1.expectNext(toAccount(accounts.get(2)));
        probe1.expectNext(toAccount(accounts.get(3)));
        probe1.cancel();

    }

    private Registration newAccount(String firstName) {
        return Registration.builder()
                .login(Login.of(""+LocalDateTime.now().toEpochSecond(ZoneOffset.UTC), "ssss"))
                .owner(Person.of(firstName, "Diallo", "mld@namatasarl.com", "21321312"))
                .build();
    }

    private Account toAccount(Registration account) {
        return Account.of(account.login().id(), account.owner().orElse(null));
    }

}
