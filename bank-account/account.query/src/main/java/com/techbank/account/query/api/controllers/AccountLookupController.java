package com.techbank.account.query.api.controllers;

import com.techbank.account.query.api.dto.AccountLookupResponse;
import com.techbank.account.query.api.dto.EqualityType;
import com.techbank.account.query.api.queries.FindAccountByHolderQuery;
import com.techbank.account.query.api.queries.FindAccountWithBalanceQuery;
import com.techbank.account.query.api.queries.FindAccountsByIdQuery;
import com.techbank.account.query.api.queries.FindAllAccountsQuery;
import com.techbank.account.query.domain.BankAccount;
import com.techbank.cqrs.core.infrastructure.QueryDispatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.MessageFormat;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@RestController
@RequestMapping(path = "/api/v1/bankAccountLookup")
public class AccountLookupController {
    private final Logger logger = Logger.getLogger(this.getClass().getName());

    @Autowired
    private QueryDispatcher queryDispatcher;

    @GetMapping(path = "/")
    public ResponseEntity<AccountLookupResponse> getAllAccounts(){
        try{
            List<BankAccount> accounts = queryDispatcher.send(new FindAllAccountsQuery());
            if(accounts == null || accounts.isEmpty()){
                return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
            }
            var response = AccountLookupResponse.builder()
                    .bankAccounts(accounts)
                    .message(MessageFormat.format("Successfully returned {0} bank account(s)", accounts.size()))
                    .build();
            return new ResponseEntity(response, HttpStatus.OK);
        }catch (Exception e){
            var safeErrorMessage = "Failed to complete get all accounts request";
            logger.log(Level.SEVERE, safeErrorMessage, e);
            return new ResponseEntity<>(new AccountLookupResponse(safeErrorMessage), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(path = "/byId/{id}")
    public ResponseEntity<AccountLookupResponse> getAccountsById(@PathVariable(value = "id") String id){
        try{
            List<BankAccount> accounts = queryDispatcher.send(new FindAccountsByIdQuery(id));
            if(accounts == null || accounts.isEmpty()){
                return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
            }
            var response = AccountLookupResponse.builder()
                    .bankAccounts(accounts)
                    .message(MessageFormat.format("Successfully returned {0} bank account(s)", accounts.size()))
                    .build();
            return new ResponseEntity(response, HttpStatus.OK);
        }catch (Exception e){
            var safeErrorMessage = "Failed to complete get account by id request";
            logger.log(Level.SEVERE, safeErrorMessage, e);
            return new ResponseEntity<>(new AccountLookupResponse(safeErrorMessage), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(path = "/byHolder/{holder}")
    public ResponseEntity<AccountLookupResponse> getAccountsByAccountHolder(@PathVariable(value = "holder") String accountHolder){
        try{
            List<BankAccount> accounts = queryDispatcher.send(new FindAccountByHolderQuery(accountHolder));
            if(accounts == null || accounts.isEmpty()){
                return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
            }
            var response = AccountLookupResponse.builder()
                    .bankAccounts(accounts)
                    .message(MessageFormat.format("Successfully returned {0} bank account(s)", accounts.size()))
                    .build();
            return new ResponseEntity(response, HttpStatus.OK);
        }catch (Exception e){
            var safeErrorMessage = "Failed to complete get account by holder request";
            logger.log(Level.SEVERE, safeErrorMessage, e);
            return new ResponseEntity<>(new AccountLookupResponse(safeErrorMessage), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(path = "/withBalance/{equalityType}/{balance}")
    public ResponseEntity<AccountLookupResponse> getAccountsWithBalance(
            @PathVariable(value = "equalityType") EqualityType equalityType,
            @PathVariable(value = "balance") double balance){
        try{
            List<BankAccount> accounts = queryDispatcher.send(new FindAccountWithBalanceQuery(equalityType, balance));
            if(accounts == null || accounts.isEmpty()){
                return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
            }
            var response = AccountLookupResponse.builder()
                    .bankAccounts(accounts)
                    .message(MessageFormat.format("Successfully returned {0} bank account(s)", accounts.size()))
                    .build();
            return new ResponseEntity(response, HttpStatus.OK);
        }catch (Exception e){
            var safeErrorMessage = "Failed to complete find accounts by balance request";
            logger.log(Level.SEVERE, safeErrorMessage, e);
            return new ResponseEntity<>(new AccountLookupResponse(safeErrorMessage), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
