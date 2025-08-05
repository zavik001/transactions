package example.transactions.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import example.transactions.model.Account;

public interface AccountRepository extends JpaRepository<Account, Long> {
}
