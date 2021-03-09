package com.db.awmd.challenge.service;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.domain.TransferAmountRequest;
import com.db.awmd.challenge.exception.InsufficientBalanceException;
import com.db.awmd.challenge.repository.AccountsRepository;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AccountsService {

  @Getter
  private final AccountsRepository accountsRepository;
  
  private NotificationService notificationService;

  @Autowired
  public AccountsService(AccountsRepository accountsRepository, NotificationService notificationService) {
    this.accountsRepository = accountsRepository;
    this.notificationService = notificationService;
  }

  public void createAccount(Account account) {
    this.accountsRepository.createAccount(account);
  }

  public Account getAccount(String accountId) {
    return this.accountsRepository.getAccount(accountId);
  }
  
  public void transferAmount(TransferAmountRequest transferAmountRequest) throws InsufficientBalanceException, NoSuchElementException {
	Account transferFromAccount = this.accountsRepository.getAccount(transferAmountRequest.getAccountFromId());
    Optional.ofNullable(transferFromAccount).orElseThrow(() -> new NoSuchElementException("Account id " + transferAmountRequest.getAccountFromId() + " does not exist!"));
	
    if (transferFromAccount.getBalance().intValue() < transferAmountRequest.getAmount()) {
    	throw new InsufficientBalanceException("Account id " + transferFromAccount.getAccountId() + " does not have sufficient balance!");
    }
    
	BigDecimal balance = transferFromAccount.getBalance().subtract(new BigDecimal(transferAmountRequest.getAmount()));
	transferFromAccount.setBalance(balance);
	
	Account transferToAccount = this.accountsRepository.getAccount(transferAmountRequest.getAccountToId());
	Optional.ofNullable(transferToAccount)
    .orElseThrow(() -> new NoSuchElementException("Account id " + transferAmountRequest.getAccountToId() + " does not exist!"));
	balance = transferToAccount.getBalance().add(new BigDecimal(transferAmountRequest.getAmount()));
	transferToAccount.setBalance(balance);
	
	this.accountsRepository.updateAccount(transferFromAccount, transferToAccount);
	this.notificationService.notifyAboutTransfer(transferFromAccount, "Amount " + transferFromAccount.getBalance() + " is debited from your account!");
	this.notificationService.notifyAboutTransfer(transferToAccount, "Amount " + transferToAccount.getBalance() + " is credited to your account!");
  }
}
