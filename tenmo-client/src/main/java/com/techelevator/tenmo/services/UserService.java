package com.techelevator.tenmo.services;

import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.User;
import com.techelevator.util.BasicLogger;
import org.apiguardian.api.API;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class UserService {

    private static final String API_BASE_URL = "http://localhost:8080/";
    private final RestTemplate restTemplate = new RestTemplate();

    private String authToken = null;

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    /**
     * Array of all users
     *
     * @return an array of all users
     */
    public User[] listUsers(){
        User[] userArray = null;
        try{
            ResponseEntity<User[]> response =
                    restTemplate.exchange(API_BASE_URL + "user",
                    HttpMethod.GET, makeAuthEntity(), User[].class);
            userArray = response.getBody();
        }catch (RestClientResponseException e){
            BasicLogger.log(e.getMessage());
        }
        return userArray;
    }

    public User getUserById(long id){
        User user = null;
        try{
            user = restTemplate.getForObject(API_BASE_URL + "/user/" + id, User.class);
        }catch (RestClientResponseException e){
            BasicLogger.log(e.getMessage());
        }
        return user;
    }

    public User getUserByAccountId(int accountId){
        User user = null;
        try{
            ResponseEntity<User> response =
                    restTemplate.exchange(API_BASE_URL + "/account/" + accountId + "/user",
                    HttpMethod.GET, makeAuthEntity(), User.class);
            user = response.getBody();
        }catch (RestClientResponseException e){
            BasicLogger.log(e.getMessage());
        }
        return user;
    }

    private HttpEntity<Void> makeAuthEntity() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(authToken);
        return new HttpEntity<>(headers);
    }
}
