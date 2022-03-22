package com.techelevator.tenmo;

import com.techelevator.tenmo.model.*;
import com.techelevator.tenmo.services.AccountService;
import com.techelevator.tenmo.services.AuthenticationService;
import com.techelevator.tenmo.services.ConsoleService;
import com.techelevator.tenmo.services.UserService;

import java.math.BigDecimal;
import java.text.NumberFormat;

public class App {

    private static final String API_BASE_URL = "http://localhost:8080/";

    private final ConsoleService consoleService = new ConsoleService();
    private final AuthenticationService authenticationService = new AuthenticationService(API_BASE_URL);
    private final AccountService accountService = new AccountService();
    private final UserService userService = new UserService();

    private AuthenticatedUser currentUser;

    public static void main(String[] args) {
        App app = new App();
        app.run();
    }

    private void run() {
        consoleService.printGreeting();
        loginMenu();
        if (currentUser != null) {
            mainMenu();
        }
    }
    private void loginMenu() {
        int menuSelection = -1;
        while (menuSelection != 0 && currentUser == null) {
            consoleService.printLoginMenu();
            menuSelection = consoleService.promptForMenuSelection("Please choose an option: ");
            if (menuSelection == 1) {
                handleRegister();
            } else if (menuSelection == 2) {
                handleLogin();
            } else if (menuSelection != 0) {
                System.out.println("Invalid Selection");
                consoleService.pause();
            }
        }
    }

    private void handleRegister() {
        System.out.println("Please register a new user account");
        UserCredentials credentials = consoleService.promptForCredentials();
        if (authenticationService.register(credentials)) {
            System.out.println("Registration successful. You can now login.");
        } else {
            consoleService.printErrorMessage();
        }
    }

    private void handleLogin() {
        UserCredentials credentials = consoleService.promptForCredentials();
        currentUser = authenticationService.login(credentials);
        String token = currentUser.getToken();
        if (currentUser == null) {
            consoleService.printErrorMessage();
        }
        accountService.setAuthToken(token);
        userService.setAuthToken(token);

    }

    private void mainMenu() {
        int menuSelection = -1;
        while (menuSelection != 0) {
            consoleService.printMainMenu();
            menuSelection = consoleService.promptForMenuSelection("Please choose an option: ");
            if (menuSelection == 1) {
                viewCurrentBalance();
            } else if (menuSelection == 2) {
                viewTransferHistory();
            } else if (menuSelection == 3) {
                viewPendingRequests();
            } else if (menuSelection == 4) {
                sendBucks();
            } else if (menuSelection == 5) {
                requestBucks();
            } else if (menuSelection == 0) {
                continue;
            } else {
                System.out.println("Invalid Selection");
            }
            consoleService.pause();
        }
    }

	private void viewCurrentBalance() {
        BigDecimal balance = accountService.checkBalanceByAccountId(currentUser.getUser().getId());
        if(balance != null) {
            System.out.println("Your current balance is: $" + (balance));
        }else{
            consoleService.printErrorMessage();
        }
	}

    private void viewTransferHistory(){
        Transfer[] transfers = null;
        transfers = accountService.viewTransferHistory(currentUser.getUser().getId());
        for(int i = 0; i < transfers.length; i++){
            System.out.print(transfers[i].getTransferId() + "     ");

            long userFromId = userService.getUserByAccountId(transfers[i].getAccountFrom()).getId();
            long currentUserId = currentUser.getUser().getId();
            if(userFromId == currentUserId){
                System.out.print("To: " + userService.getUserByAccountId(transfers[i].getAccountTo()).getUsername());
            }else{
                System.out.print("From: " + userService.getUserByAccountId(transfers[i].getAccountFrom()).getUsername());
            }

            System.out.println(" $" + transfers[i].getAmount());
        }
        viewTransferDetails();
    }

	private void viewTransferDetails() {
        int transferId = consoleService.promptForInt("Please enter a transfer ID to view details (0 to cancel): ");

        if(transferId == 0) {
            mainMenu();
        }else{
            Transfer transfer = accountService.getTransferById(transferId);
            System.out.println("");
            System.out.println("-------------------------------");
            System.out.println("Transfer Details");
            System.out.println("-------------------------------");
            System.out.println("Id: " + transfer.getTransferId());
            System.out.println("From: " + userService.getUserByAccountId(transfer.getAccountFrom()).getUsername());
            System.out.println("To: " + userService.getUserByAccountId(transfer.getAccountTo()).getUsername());
            System.out.println("Type: Send");
            if(transfer.getTransferStatusId() == 2) {
                System.out.println("Status: Approved");
            }else{
                System.out.println("Status: Pending");
            }
            System.out.println("Amount: $" + transfer.getAmount());
        }
    }

	private void sendBucks() {
        User[] users = userService.listUsers();
		consoleService.printUsers(users);
        int toUserId = consoleService.promptForInt("Enter Id of user you are sending to (0 to cancel): ");
        //User userToTransferTo = userService.getUserById(userId);
        BigDecimal amountToTransfer = consoleService.promptForBigDecimal("Enter amount: ");

        if(toUserId != currentUser.getUser().getId()){
            Account accountFrom = accountService.getAccountByUserId(currentUser.getUser().getId());
            Account accountTo = accountService.getAccountByUserId(toUserId);

            Transfer transfer = new Transfer();
            transfer.setTransferTypeId(2);
            transfer.setTransferStatusId(2);
            transfer.setAccountFrom(accountFrom.getAccount_id());
            transfer.setAccountTo(accountTo.getAccount_id());
            transfer.setAmount(amountToTransfer);

            accountFrom.setBalance(accountFrom.getBalance().subtract(amountToTransfer));
            accountTo.setBalance(accountTo.getBalance().add(amountToTransfer));

            accountService.updateBalance(accountFrom.getAccount_id(),
                    accountFrom.getBalance());
            accountService.updateBalance(accountTo.getAccount_id(),
                    accountTo.getBalance());

            accountService.createTransfer(transfer);
        }
    }

	private void requestBucks() {
		// TODO Auto-generated method stub
		
	}

    private void viewPendingRequests() {
        // TODO Auto-generated method stub

    }

}
