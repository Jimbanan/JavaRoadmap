package ru.personal.junit5basics.service;

import ru.personal.junit5basics.dto.Account;

import java.util.HashMap;
import java.util.Map;

public class AccountService {

    private final Map<String, Account> accounts = new HashMap<>();

    public Account createAccount(String accountId, String ownerName, double initialBalance) {
        Account account = new Account(accountId, ownerName, initialBalance);
        accounts.put(account.getId(), account);
        return account;
    }

    public void deposit(String accountId, double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Сумма депозита должна быть положительной");
        }
        Account account = accounts.get(accountId);
        if (account == null) {
            throw new IllegalArgumentException("Счет не найден");
        }
        account.setBalance(account.getBalance() + amount);
    }

    public void withdraw(String accountId, double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Сумма снятия должна быть положительной");
        }
        Account account = accounts.get(accountId);
        if (account == null) {
            throw new IllegalArgumentException("Счет не найден");
        }
        if (account.getBalance() < amount) {
            throw new IllegalStateException("Недостаточно средств");
        }
        account.setBalance(account.getBalance() - amount);
    }

    public void transfer(String fromAccountId, String toAccountId, double amount) {
        // Метод должен использовать withdraw и deposit для перевода.
        // Если во время перевода произошла ошибка, балансы не должны измениться.
        // TODO: реализовать позже.
        throw new UnsupportedOperationException("Метод перевода в разработке");
    }

    public Account getAccount(String accountId) {
        return accounts.get(accountId);
    }
}