package example.transactions.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import example.transactions.entity.Account;

public interface AccountRepository extends JpaRepository<Account, Long> {

}
