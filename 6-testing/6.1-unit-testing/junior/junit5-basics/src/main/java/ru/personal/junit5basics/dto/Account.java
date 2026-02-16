package ru.personal.junit5basics.dto;

public class Account {
    private String id;
    private String ownerName;
    private double balance;

    public Account(String accountId, String ownerName, double initialBalance) {
        if (initialBalance < 0) {
            throw new IllegalArgumentException("Начальный баланс не может быть отрицательным");
        }
        this.id = accountId;
        this.ownerName = ownerName;
        this.balance = initialBalance;
    }

    // Геттеры и сеттеры (баланс менять только через методы сервиса!)
    public String getId() {
        return id;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }
}