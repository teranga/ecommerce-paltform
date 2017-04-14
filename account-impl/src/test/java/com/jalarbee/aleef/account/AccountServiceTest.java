package com.jalarbee.aleef.account;

import akka.Done;
import akka.NotUsed;
import akka.stream.Materializer;
import akka.stream.javadsl.Source;
import akka.stream.testkit.TestSubscriber;
import akka.stream.testkit.javadsl.TestSink;
import com.google.inject.Inject;
import com.jalarbee.aleef.account.api.AccountEvent;
import com.jalarbee.aleef.account.api.AccountService;
import com.jalarbee.aleef.account.api.model.Account;
import com.jalarbee.aleef.account.api.model.Person;
import com.lightbend.lagom.javadsl.api.ServiceCall;
import com.lightbend.lagom.javadsl.api.broker.Topic;
import org.junit.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.lightbend.lagom.javadsl.testkit.ServiceTest.*;
import static org.junit.Assert.*;

public class AccountServiceTest {

    private static TestServer server;
    private static AccountService service;


    private final static Setup setup = defaultSetup().withCassandra(true)
            .configureBuilder(b ->
                    b.configure("cassandra-query-journal.eventual-consistency-delay", "0")
//                            .overrides(bind(AccountService.class).to(AccountStub.class))

            );

    @BeforeClass
    public static void beforeAll() {
        server = startServer(setup);
        service = server.client(AccountService.class);
    }

    @AfterClass
    public static void afterAll() {
        server.stop();
    }

    @Before
    public void init() {

    }

    @After
    public void finish() {

    }

    @Test
    @Ignore
    public void shouldRegisterAndRetrieveNewAccount() throws Exception {
        AccountService service = server.client(AccountService.class);

        Account registration = new Account("2312312", new Person("Mamadou", "Diallo", Optional.of("Lamine"), "mld@namatasarl.com", "21321312"));

        CompletionStage<Account> result = service.register().invoke(registration);
        assertEquals(registration, result.toCompletableFuture().get(5, TimeUnit.SECONDS));

        TimeUnit.SECONDS.sleep(5);

        Account remoteAccount = service.getAccount("2312312").invoke().toCompletableFuture().get(15, TimeUnit.SECONDS);

        assertNotNull(remoteAccount);
        assertTrue(remoteAccount.getOwner().equals(registration.getOwner()));
        assertTrue(remoteAccount.getOwner().equals(registration.getId()));

    }

    @Test
    @Ignore
    public void shouldStreamNewAccounts() throws Exception {
        AccountService service = server.client(AccountService.class);

        Source<Account, ?> stream1 = null; //service.accountEvents().su().toCompletableFuture().get(15, TimeUnit.SECONDS);
        Source<Account, ?> stream2 = null; //service.accountEvents().invoke(NotUsed.getInstance()).toCompletableFuture().get(15, TimeUnit.SECONDS);

        List<Account> accounts = Stream.of("Lamine", "Sellou", "Ami", "Kaoussou").map(x -> newAccount(x)).collect(Collectors.toList());

        TestSubscriber.Probe<Account> probe1 = stream1.runWith(TestSink.probe(server.system()), server.materializer());
        probe1.request(10);

        service.register().invoke(accounts.get(0)).toCompletableFuture().get(15, TimeUnit.SECONDS);
        service.register().invoke(accounts.get(1)).toCompletableFuture().get(15, TimeUnit.SECONDS);
        service.register().invoke(accounts.get(2)).toCompletableFuture().get(15, TimeUnit.SECONDS);

        TestSubscriber.Probe<Account> probe2 = stream2.runWith(TestSink.probe(server.system()), server.materializer());
        probe2.request(10);

        service.register().invoke(accounts.get(3)).toCompletableFuture().get(15, TimeUnit.SECONDS);

        probe1.expectNext(accounts.get(0));
        probe2.expectNext(accounts.get(0));
        probe2.cancel();
        probe1.expectNext(accounts.get(1));
        probe1.expectNext(accounts.get(2));
        probe1.expectNext(accounts.get(3));
        probe1.cancel();

    }

    private Account newAccount(String firstName) {
        return new Account(UUID.randomUUID().toString(), new Person(firstName, "Diallo", Optional.empty(), "mld@namatasarl.com", "21321312"));
    }

    private static class AccountStub implements AccountService {

        private final Materializer materializer;

        @Inject
        public AccountStub(Materializer materializer) {
            this.materializer = materializer;
        }

        @Override
        public ServiceCall<Account, Account> register() {
            return null;
        }

        @Override
        public Topic<AccountEvent> accountEvents() {
            return null;
        }

        @Override
        public ServiceCall<NotUsed, Account> getAccount(String accountId) {
            return null;
        }

        @Override
        public ServiceCall<Long, Done> suspend(String accountId) {
            return null;
        }

        @Override
        public ServiceCall<NotUsed, Done> liftSuspension(String accountId) {
            return null;
        }

        @Override
        public ServiceCall<NotUsed, Done> delete(String accountId) {
            return null;
        }
    }
}
