package ru.personal.junit5basics.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertAll;

/**
 * Убирает изолированность тестов.
 * Как следствие каждый тест начинает влиять на работу других, что приводит к несогласованности и избыточности данных
 */
//@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AccountServiceTest {

    public static final String ACCOUNT_ID = "accountId";
    public static final String OWNER_NAME = "ownerName";
    public static final double INITIAL_BALANCE = 100.0;


    private AccountService accountService;

//    @BeforeAll
//    void setup() {
//        accountService = new AccountService();
//    }

    @BeforeEach
    void setUp() {
        accountService = new AccountService();
        accountService.createAccount(ACCOUNT_ID, OWNER_NAME, INITIAL_BALANCE);
    }

    @Test
    void createAccount_shouldReturnAccountWhenDataCorrect() {
        //given
        var accountId = "accountIdNew";
        //when
        var account = accountService.createAccount(accountId, OWNER_NAME, INITIAL_BALANCE);
        //then
        assertAll(() -> {
            assertThat(account).isNotNull();
            assertThat(account.getId()).isEqualTo(accountId);
            assertThat(account.getBalance()).isEqualTo(INITIAL_BALANCE);
            assertThat(account.getOwnerName()).isEqualTo(OWNER_NAME);
        });
    }

    @Test
    void deposit_shouldThrowExceptionWhenAmountIsNegative() {
        //given
        var amount = -100;
        //when
        //then
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> accountService.deposit(ACCOUNT_ID, amount))
                .withMessage("Сумма депозита должна быть положительной");
    }

    @Test
    void deposit_shouldThrowExceptionWhenAccountNotFound() {
        //given
        var accountId = "accountIdNotFound";
        var amount = 100;
        //when
        //then
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> accountService.deposit(accountId, amount))
                .withMessage("Счет не найден");
    }

    @Test
    void deposit_shouldUpdateBalanceWhenDataCorrect() {
        //given
        var amount = 100;
        //when
        var initialBalance = accountService.getAccount(ACCOUNT_ID).getBalance();
        accountService.deposit(ACCOUNT_ID, amount);
        var accountAfterDeposit = accountService.getAccount(ACCOUNT_ID);
        //then
        assertThat(accountAfterDeposit.getBalance()).isEqualTo(initialBalance + amount);
    }

    @Test
    void withdraw_shouldThrowExceptionWhenAmountIsNegative() {
        //given
        var amount = -100;
        //when
        //then
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> accountService.withdraw(ACCOUNT_ID, amount))
                .withMessage("Сумма снятия должна быть положительной");
    }

    @Test
    void withdraw_shouldThrowExceptionWhenAccountNotFound() {
        //given
        var accountId = "accountIdNotFound";
        var amount = 100;
        //when
        //then
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> accountService.withdraw(accountId, amount))
                .withMessage("Счет не найден");
    }

    @Test
    void withdraw_shouldThrowExceptionWhenBalanceLessAmount() {
        //given
        //when
        var initialBalance = accountService.getAccount(ACCOUNT_ID).getBalance();
        //then
        assertThatExceptionOfType(IllegalStateException.class)
                .isThrownBy(() -> accountService.withdraw(ACCOUNT_ID, initialBalance * 2))
                .withMessage("Недостаточно средств");
    }

    @Test
    void withdraw_shouldUpdateBalanceWhenDataCorrect() {
        //given
        var amount = 100;
        var amountWithdraw = amount / 2;
        //when
        var initialBalance = accountService.getAccount(ACCOUNT_ID).getBalance();
        accountService.withdraw(ACCOUNT_ID, amountWithdraw);
        var accountAfterWithdraw = accountService.getAccount(ACCOUNT_ID);
        //then
        assertThat(accountAfterWithdraw.getBalance()).isEqualTo(initialBalance - amountWithdraw);
    }

    @Test
    void getAccount_shouldReturnAccountWhenDataCorrect() {
        //given
        //when
        var account = accountService.getAccount(ACCOUNT_ID);
        //then
        assertAll(() -> {
            assertThat(account).isNotNull();
            assertThat(account.getId()).isEqualTo(ACCOUNT_ID);
        });
    }

    @Test
    void getAccount_shouldReturnAccountNotFoundWhenDataCorrect() {
        //given
        var accountId = "accountIdNotExist";
        //when
        var account = accountService.getAccount(accountId);
        //then
        assertThat(account).isNull();
    }

    @Test
    @Disabled("Метод еще не реализован")
    void transfer_shouldThrowUnsupportedOperationException() {
        //given
        var fromAccountId = "accountId1";
        var toAccountId = "accountId2";
        var amount = 100;
        //when
        //then
        assertThatExceptionOfType(UnsupportedOperationException.class)
                .isThrownBy(() -> accountService.transfer(fromAccountId, toAccountId, amount))
                .withMessage("Метод перевода в разработке");
    }

}