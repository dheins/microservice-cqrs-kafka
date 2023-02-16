package com.techbank.account.query.api.queries;

import com.techbank.account.query.api.dto.EqualityType;
import com.techbank.account.query.domain.AccountRepository;
import com.techbank.account.query.domain.BankAccount;
import com.techbank.cqrs.core.domain.BaseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AccountQueryHandler implements QueryHandler{
    @Autowired
    private AccountRepository repository;

    @Override
    public List<BaseEntity> handle(FindAllAccountsQuery query) {
        Iterable<BankAccount> bankAccounts = repository.findAll();
        var bankAccountList = new ArrayList<BaseEntity>();
        bankAccounts.forEach(bankAccountList::add);
        return bankAccountList;
    }

    @Override
    public List<BaseEntity> handle(FindAccountsByIdQuery query) {
        var bankAccount = repository.findById(query.getId());
        if(bankAccount.isEmpty()){
            return null;
        }
        List<BaseEntity> bankAccountList = new ArrayList<>();
        bankAccountList.add(bankAccount.get());
        return bankAccountList;
    }

    @Override
    public List<BaseEntity> handle(FindAccountByHolderQuery query) {
        var bankAccount = repository.findByAccountHolder(query.getAccountHolder());
        if(bankAccount.isEmpty()){
            return null;
        }
        List<BaseEntity> bankAccountList = new ArrayList<>();
        bankAccountList.add(bankAccount.get());
        return bankAccountList;
    }

    @Override
    public List<BaseEntity> handle(FindAccountWithBalanceQuery query) {
        var bankAccountList = query.getEqualityType() == EqualityType.GREATER_THAN
                ? repository.findByBalanceGreaterThan(query.getBalance())
                : repository.findByBalanceLessThan(query.getBalance());
        if(bankAccountList.isEmpty()){
            return null;
        }
        return bankAccountList;
    }
}
