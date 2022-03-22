package com.techelevator.tenmo.services;

import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.User;
import com.techelevator.util.BasicLogger;
import org.springframework.http.*;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class AccountService {

    private static final String API_BASE_URL = "http://localhost:8080/";
    private final RestTemplate restTemplate = new RestTemplate();

    private String authToken = null;

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    /**
     * Check account Balance by accountId
     */
    public BigDecimal checkBalanceByAccountId(long account_id) {
        BigDecimal balance = null;
        try {
            ResponseEntity<BigDecimal> response =
                    restTemplate.exchange(API_BASE_URL + "account/" + account_id + "/balance",
                    HttpMethod.GET, makeAuthEntity(), BigDecimal.class);
            balance = response.getBody();
        } catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }
        return balance;
    }

    public Transfer[] viewTransferHistory(long user_id){
        Transfer[] transfers = null;
        try{
            ResponseEntity<Transfer[]> response =
                    restTemplate.exchange(API_BASE_URL + "/user/" + user_id + "/transferhistory",
                    HttpMethod.GET, makeAuthEntity(), Transfer[].class);
            transfers = response.getBody();
        }catch (RestClientResponseException e){
            BasicLogger.log(e.getMessage());
        }
        return transfers;
    }

    public void createTransfer(Transfer transfer) {
        /*
        restTemplate.postForObject fails 3/11/2022 8:26 pm,
        internal server error

        also need to create a parallel transfer for the receiving user
         */
        try {
            restTemplate.postForObject(API_BASE_URL + "/account/transfer",
                    makeTransferEntity(transfer), Void.class);
        } catch (RestClientResponseException e){
            BasicLogger.log(e.getMessage());
        }
    }

    public Account getAccountByUserId(long userId){
        Account account = null;
        try{
            ResponseEntity<Account> response =
                    restTemplate.exchange( API_BASE_URL + "/user/" + userId + "/account",
                    HttpMethod.GET, makeAuthEntity(), Account.class);
            account = response.getBody();
        }catch(RestClientResponseException e){
            BasicLogger.log(e.getMessage());
        }
        return account;
    }

    public void updateBalance(int account_id, BigDecimal newBalance){
        try{
            restTemplate.put(API_BASE_URL + "/account/" + account_id + "/updatebalance",
                    makeBigDecimalEntity(newBalance));
        }catch(RestClientResponseException e){
            BasicLogger.log(e.getMessage());
        }
    }

    public Transfer getTransferById(int transferId){
        Transfer transfer = null;
        try{
            ResponseEntity<Transfer> response =
                    restTemplate.exchange(API_BASE_URL + "/account/transfer/" + transferId,
                            HttpMethod.GET, makeAuthEntity(), Transfer.class);
            transfer = response.getBody();
        }catch(RestClientResponseException e){
            BasicLogger.log(e.getMessage());
        }
        return transfer;
    }

    private HttpEntity<BigDecimal> makeBigDecimalEntity(BigDecimal bigDecimal) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(authToken);
        return new HttpEntity<>(bigDecimal, headers);
    }

    private HttpEntity<Transfer> makeTransferEntity(Transfer transfer) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(authToken);
        return new HttpEntity<>(transfer, headers);
    }

    private HttpEntity<Account> makeAccountEntity(Account account) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(authToken);
        return new HttpEntity<>(account, headers);
    }

    private HttpEntity<Void> makeAuthEntity() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(authToken);
        return new HttpEntity<>(headers);
    }
}
