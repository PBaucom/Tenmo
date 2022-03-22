package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Account;
import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class JdbcAccountDao implements AccountDao {

    private JdbcTemplate jdbcTemplate;
    private UserDao userDao;
    private AccountDao accountDao;

    private BasicDataSource basicDataSource = setupBasicDataSource();

    private BasicDataSource setupBasicDataSource() {
        basicDataSource = new BasicDataSource();
        basicDataSource.setUrl("jdbc:postgresql://localhost:5432/tenmo");
        basicDataSource.setUsername("postgres");
        basicDataSource.setPassword("postgres1");
        return basicDataSource;
    }

    public JdbcAccountDao() {
        this.jdbcTemplate = new JdbcTemplate(basicDataSource);
    }

    @Override
    public BigDecimal getBalance(int user_id) {
        Account account = new Account();
        String sql = "SELECT * FROM account WHERE account.user_id = ?";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, user_id);
        if (results.next()) {
            account = mapRowToAccount(results);
        }
        return account.getBalance();
    }


    @Override
    public Account getAccount(int account_id) {
        Account account = new Account();
        String sql = "SELECT * FROM account WHERE account.account_id = ?";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, account_id);
        if(results.next()) {
            account = mapRowToAccount(results);
        }
        return account;
    }

    @Override
    public void updateBalance(int account_id, BigDecimal newBalance) {
        String sql = "UPDATE account SET balance = ? " +
                     "WHERE account_id = ? ";
        jdbcTemplate.update(sql,
                newBalance,
                account_id);
    }

    @Override
    public Account getAccountByAccountId(int account_id){
        Account account = new Account();
        String sql = "SELECT * FROM account WHERE account.account_id = ?";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, account_id);
        if(results.next()) {
            account = mapRowToAccount(results);
        }
        return account;
    }

    @Override
    public Account getAccountByUserId(long userId){
        Account account = new Account();
        String sql = "SELECT * FROM account WHERE account.user_id = ?";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, userId);
        if(results.next()) {
            account = mapRowToAccount(results);
        }
        return account;
    }

    public Account mapRowToAccount(SqlRowSet rowSet) {
        Account account = new Account();

        account.setAccount_id(rowSet.getInt("account_id"));
        account.setUser_id(rowSet.getInt("user_id"));
        account.setBalance(rowSet.getBigDecimal("balance"));

        return account;
    }
}
