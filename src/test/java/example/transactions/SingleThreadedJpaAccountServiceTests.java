package example.transactions;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.math.BigDecimal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import example.transactions.model.Account;
import example.transactions.service.SingleThreadedJpaAccountService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Import(TestcontainersConfiguration.class)
@SpringBootTest
class SingleThreadedJpaAccountServiceTests {

    @Autowired
    private SingleThreadedJpaAccountService service;

    private Account userA;
    private Account userB;

    @BeforeEach
    void setUp() {
        userA = service
                .save(Account.builder().balance(new BigDecimal("1000.23")).owner("UserA").build());
        userB = service
                .save(Account.builder().balance(new BigDecimal("1100.23")).owner("UserB").build());
        log.info("Initial: userA = {}, userB = {}", userA, userB);
    }

    @Test
    void checkDatabaseTest() {
        // given
        // when
        Account acc = service.getById(userA.getId());

        // then
        assertEquals(userA.getOwner(), acc.getOwner());
        assertEquals(0, userA.getBalance().compareTo(acc.getBalance()));
    }

    @Test
    void withdrawTest() {
        // given
        BigDecimal amount = new BigDecimal("10.78");

        // when
        service.withdraw(userA, amount);

        // then
        Account copyA = service.getById(userA.getId());
        assertEquals(0, copyA.getBalance().compareTo(new BigDecimal("989.45")));
    }

    @Test
    void depositTest() {
        // given
        BigDecimal amount = new BigDecimal("14.34");

        // when
        service.deposit(userA, amount);

        // then
        Account copyA = service.getById(userA.getId());
        assertEquals(0, copyA.getBalance().compareTo(new BigDecimal("1014.57")));
    }

    @Test
    void transferTest() {
        // given
        BigDecimal amount = new BigDecimal("23.43");

        // when
        service.transfer(userA, userB, amount);

        // then
        Account copyA = service.getById(userA.getId());
        Account copyB = service.getById(userB.getId());
        assertEquals(0, copyA.getBalance().compareTo(new BigDecimal("976.80")));
        assertEquals(0, copyB.getBalance().compareTo(new BigDecimal("1123.66")));
    }
}
