## Домашнее задание

Твоя задача — разработать модульные тесты для класса, имитирующего сервис управления банковскими счетами. Это максимально приближено к реальной задаче.

### Задание 1: Тестирование сервиса `AccountService`

Представь, у тебя есть класс `AccountService` (напиши его сам в папке `main/java` или протестируй предложенную версию). Он работает со счетами `Account`.

**Класс `Account`:**
```java
package com.bank.core;

import java.util.Objects;
import java.util.UUID;

public class Account {
    private String id;
    private String ownerName;
    private double balance;

    public Account(String ownerName, double initialBalance) {
        if (initialBalance < 0) {
            throw new IllegalArgumentException("Начальный баланс не может быть отрицательным");
        }
        this.id = UUID.randomUUID().toString();
        this.ownerName = ownerName;
        this.balance = initialBalance;
    }

    // Геттеры и сеттеры (баланс менять только через методы сервиса!)
    public String getId() { return id; }
    public String getOwnerName() { return ownerName; }
    public double getBalance() { return balance; }
    public void setBalance(double balance) { this.balance = balance; }
}
```

**Класс `AccountService` (продакшен-код, который ты должен протестировать):**
```java
package com.bank.core;

import java.util.HashMap;
import java.util.Map;

public class AccountService {
    private Map<String, Account> accounts = new HashMap<>();

    public Account createAccount(String ownerName, double initialBalance) {
        Account account = new Account(ownerName, initialBalance);
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
```

**Критерии выполнения домашнего задания (Что нужно сделать):**

1.  **Создай тестовый класс** `AccountServiceTest` в соответствующей директории (`src/test/java/com/bank/core`).
2.  **Используй `@BeforeEach`**: Создавай новый экземпляр `AccountService` и один тестовый аккаунт перед каждым тестом.
3.  **Протестируй `createAccount`:**
    *   Проверь успешное создание счета (проверь `assertNotNull` id, `assertEquals` для имени и баланса).
    *   Проверь, что создание счета с отрицательным балансом выбрасывает `IllegalArgumentException` (используй `assertThrows`).
4.  **Протестируй `deposit`:**
    *   Проверь успешное пополнение баланса.
    *   Проверь пополнение несуществующего счета (исключение).
    *   Проверь пополнение на отрицательную сумму и ноль (исключение).
5.  **Протестируй `withdraw`:**
    *   Проверь успешное снятие средств.
    *   Проверь снятие средств при недостаточном балансе (исключение `IllegalStateException`).
    *   Проверь снятие с несуществующего счета.
    *   Проверь снятие на отрицательную сумму.
6.  **Используй `assertAll`**: В одном из тестов (например, при проверке создания счета) сгруппируй проверки имени, баланса и наличия ID в один `assertAll`.
7.  **Используй `@DisplayName`**: Дай каждому тесту читаемое имя на русском или английском языке.
8.  **Используй `@Disabled`**: Временно отключи тест для метода `transfer`, так как он еще не реализован. Добавь комментарий, почему он отключен.
9. Проанализируй свой тестовый класс `AccountServiceTest`. Представь, что `AccountService` очень "тяжелый" объект — например, он содержит кэш или пул соединений, и создавать его перед каждым тестом слишком накладно.
10. **Рефакторинг с `@TestInstance`**:
- Переключи тестовый класс на `@TestInstance(Lifecycle.PER_CLASS)`.
- Измени метод `setUp` с `@BeforeEach` на `@BeforeAll`. Подумай, какие поля теперь нужно инициализировать один раз, а какие — по-прежнему перед каждым тестом.
- Создай **один** тестовый аккаунт в `@BeforeAll` и используй его во всех тестах.
- **Напиши комментарий** в коде, объясняя, почему такой подход может быть опасен (укажи на проблему изоляции тестов и возможные побочные эффекты).
- Убедись, что все тесты по-прежнему проходят (зеленые). Если какой-то тест упал из-за разделения состояния — исправь его, добавив сброс состояния перед тестом.

Это задание научит тебя не просто использовать `@TestInstance`, но и критически мыслить о цене изоляции и моментах, когда её можно ослабить.

**Критерии проверки:**
*   Все тесты должны быть **зелеными** (кроме `@Disabled`).
*   Код тестов должен быть чистым и читаемым.
*   Должен соблюдаться паттерн **Arrange-Act-Assert** (разделенный пустыми строками).
*   Не должно быть дублирования кода для создания сервиса и аккаунта (используй `@BeforeEach`).