package example.transactions.service;

import java.math.BigDecimal;

import example.transactions.entity.Account;

public interface AccountService {
    void withdraw(Account account, BigDecimal amout);

    void deposit(Account account, BigDecimal amout);

    void transfer(Account fromAccount, Account toAccount, BigDecimal amout);
}
