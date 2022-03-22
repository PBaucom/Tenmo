package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Account;

import java.math.BigDecimal;

public interface AccountDao {

    BigDecimal getBalance(int account_id);

    void updateBalance(int account_id, BigDecimal amount);

    Account getAccount(int account_id);

    Account getAccountByUserId(long userId);

    Account getAccountByAccountId(int account_id);
}
