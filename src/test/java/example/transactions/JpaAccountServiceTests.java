package example.transactions;

import static org.junit.Assert.assertEquals;
import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import example.transactions.model.Account;
import example.transactions.service.JpaAccountService;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
class JpaAccountServiceTests {

	@Autowired
	private JpaAccountService service;
	private List<Account> list;
	private ThreadLocalRandom random;

	@BeforeEach
	void setUp() {
		random = ThreadLocalRandom.current();
		list = Stream
				.generate(() -> Account.builder().owner("user" + random.nextInt(1000))
						.balance(BigDecimal.valueOf(random.nextDouble() * 1000)).build())
				.limit(100).peek(service::save).toList();
	}

	@Test
	void checkDatabase() {
		// given
		Account account1 = list.getFirst();

		// when
		Account account2 = service.getById(account1.getId());

		// then
		assertEquals(account1.getOwner(), account2.getOwner());
	}
}
