package com.techbank.account.query.infrastructure.handlers;

import com.techbank.account.common.event.AccountClosedEvent;
import com.techbank.account.common.event.AccountOpenedEvent;
import com.techbank.account.common.event.FundsDepositedEvent;
import com.techbank.account.common.event.FundsWithdrawnEvent;
import com.techbank.account.query.domain.AccountRepository;
import com.techbank.account.query.domain.BankAccount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AccountEventHandler implements EventHandler{
    @Autowired
    private AccountRepository repository;

    @Override
    public void on(AccountOpenedEvent event) {
        var bankAccount = BankAccount.builder()
                .id(event.getId())
                .accountHolder(event.getAccountHolder())
                .creationDate(event.getCreatedDate())
                .accountType(event.getAccountType())
                .balance(event.getOpeningBalance())
                .build();
        repository.save(bankAccount);
    }

    @Override
    public void on(FundsDepositedEvent event) {
        var bankAccount = repository.findById(event.getId());
        if(bankAccount.isEmpty()){
            return;
        }
        var currentBalance = bankAccount.get().getBalance();
        var latestBalance = currentBalance + event.getAmount();
        bankAccount.get().setBalance(latestBalance);
        repository.save(bankAccount.get());
    }

    @Override
    public void on(FundsWithdrawnEvent event) {
        var bankAccount = repository.findById(event.getId());
        if(bankAccount.isEmpty()){
            return;
        }
        var currentBalance = bankAccount.get().getBalance();
        var latestBalance = currentBalance - event.getAmount();
        bankAccount.get().setBalance(latestBalance);
        repository.save(bankAccount.get());
    }

    @Override
    public void on(AccountClosedEvent event) {
        repository.deleteById(event.getId());
    }
}
