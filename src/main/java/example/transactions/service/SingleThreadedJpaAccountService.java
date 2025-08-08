package example.transactions.service;

import java.math.BigDecimal;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import example.transactions.exeption.NotFoundException;
import example.transactions.model.Account;
import example.transactions.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class SingleThreadedJpaAccountService implements AccountService {

    private final AccountRepository repository;

    public Account save(Account account) {
        if (account.getBalance() == null) {
            account.setBalance(BigDecimal.ZERO);
        }
        return repository.save(account);
    }

    public Optional<Account> findById(Long id) {
        return repository.findById(id);
    }

    public Account getById(Long id) {
        return findById(id).orElseThrow(() -> new NotFoundException("Account not found, id=" + id));
    }

    private void validateAmount(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
    }

    private void validateSufficientFunds(BigDecimal balance, BigDecimal amount) {
        validateAmount(amount);
        if (balance.compareTo(amount) < 0) {
            throw new IllegalArgumentException("Insufficient funds");
        }
    }

    @Override
    public void withdraw(Account account, BigDecimal amount) {
        Account acc = getById(account.getId());
        validateSufficientFunds(acc.getBalance(), amount);
        acc.setBalance(acc.getBalance().subtract(amount));
        repository.save(acc);
        log.info("Withdrawn {} from account {}", amount, acc.getId());
    }

    @Override
    public void deposit(Account account, BigDecimal amount) {
        validateAmount(amount);
        Account acc = getById(account.getId());
        acc.setBalance(acc.getBalance().add(amount));
        repository.save(acc);
        log.info("Deposited {} to account {}", amount, acc.getId());
    }

    @Override
    @Transactional
    public void transfer(Account fromAccount, Account toAccount, BigDecimal amount) {
        withdraw(fromAccount, amount);
        deposit(toAccount, amount);
        log.info("Transferred {} from account {} to account {}", amount, fromAccount.getId(),
                toAccount.getId());
    }
}
