package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.*;
import com.techelevator.tenmo.exception.AccountNotFoundException;
import com.techelevator.tenmo.exception.TransferNotFoundException;
import com.techelevator.tenmo.exception.UserNotFoundException;
import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.User;
import org.springframework.boot.autoconfigure.jdbc.JdbcTemplateAutoConfiguration;
import org.springframework.data.jdbc.core.JdbcAggregateTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.List;

@RestController
@PreAuthorize("isAuthenticated()")
public class AccountController {

    private UserDao userDao;
    private AccountDao accountDao;
    private TransferDao transferDao;

    public AccountController(JdbcUserDao userDao, JdbcAccountDao accountDao, JdbcTransferDao transferDao) {
        this.userDao = userDao;
        this.accountDao = accountDao;
        this.transferDao = transferDao;
    }
    /**
    * Return Account Balance
    */
    @RequestMapping(path = "/account/{account_id}/balance", method = RequestMethod.GET)
    public BigDecimal getBalance(@PathVariable int account_id) throws AccountNotFoundException {
        return accountDao.getBalance(account_id);
    }

    /**
     * updates the account balance
     */
    @RequestMapping(path = "/account/{account_id}/updatebalance", method = RequestMethod.PUT)
    public void updateBalance(@PathVariable int account_id, @RequestBody BigDecimal newBalance) throws AccountNotFoundException{
        accountDao.updateBalance(account_id, newBalance);
    }

    /**
     * returns all transfers
     */
    @RequestMapping(path = "/user/{user_id}/transferhistory", method = RequestMethod.GET)
    public List<Transfer> getAllTransfers(@PathVariable long user_id) throws UserNotFoundException {
        return transferDao.getTransfersByUserId(user_id);
    }

    @RequestMapping(path = "/account/transfer/{transferId}", method = RequestMethod.GET)
    public Transfer getTransferById(@PathVariable int transferId) throws TransferNotFoundException {
        return transferDao.getTransferById(transferId);
    }

    @RequestMapping(path = "/account/transfer", method = RequestMethod.POST)
    public void createTransfer(@RequestBody Transfer transfer){
        try {
            transferDao.createTransfer(transfer);
        }catch(Exception e){
            System.out.println(e.getMessage());
        }
    }

    @RequestMapping(path = "/user/{userId}/account", method = RequestMethod.GET)
    public Account getAccountByUserId(@PathVariable("userId") long userId) throws AccountNotFoundException {
        return accountDao.getAccountByUserId(userId);
    }

    @RequestMapping(path = "/account/{accountId}/user", method = RequestMethod.GET)
    public User getUserByAccountId(@PathVariable("accountId") int accountId) throws AccountNotFoundException {
        return userDao.getUserByAccountId(accountId);
    }

    //@PreAuthorize("isUser")
    @RequestMapping(path ="/user", method = RequestMethod.GET)
    public List<User> userList() throws UserNotFoundException {
        return userDao.findAll();
    }
}


