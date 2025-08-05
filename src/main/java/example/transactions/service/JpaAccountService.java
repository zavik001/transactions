package example.transactions.service;

import java.math.BigDecimal;
import java.util.Optional;
import org.springframework.stereotype.Service;
import example.transactions.exaption.NotFoundException;
import example.transactions.model.Account;
import example.transactions.repository.AccountRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@AllArgsConstructor
@Slf4j
@Service
public class JpaAccountService implements AccountService {

    private final AccountRepository repository;

    public Account save(Account account) {
        return repository.save(account);
    }

    public Optional<Account> findById(Long id) {
        return repository.findById(id);
    }

    public Account getById(Long id) {
        return findById(id).orElseThrow(() -> new NotFoundException("Account not found"));
    }

    @Override
    public void withdraw(Account account, BigDecimal amout) {}

    @Override
    public void deposit(Account account, BigDecimal amout) {}

    @Override
    public void transfer(Account fromAccount, Account toAccount, BigDecimal amout) {}
}
