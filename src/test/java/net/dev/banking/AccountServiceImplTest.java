package net.dev.banking;

import net.dev.banking.dto.AccountDto;
import net.dev.banking.entity.Account;
import net.dev.banking.exception.AccountException;
import net.dev.banking.repository.AccountRepository;
import net.dev.banking.service.impl.AccountServiceIml;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AccountServiceImplTest {

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private AccountServiceIml accountService;

    private Account account;
    private AccountDto accountDto;

    @BeforeEach
    void setUp() {
        account = new Account();
        account.setId(1L);
        account.setAccountHolderName("John Doe");
        account.setBalance(1000.0);

        accountDto = new AccountDto(1L, "John Doe", 1000.0);
    }

    @Test
    @DisplayName("Test for creating account")
    void givenAccountDto_whenCreateAccount_thenReturnSavedAccount() {
        // Given
        given(accountRepository.save(any(Account.class))).willReturn(account);

        // When
        AccountDto savedAccount = accountService.createAccount(accountDto);

        // Then
        assertNotNull(savedAccount);
        assertEquals(accountDto.accountHolderName(), savedAccount.accountHolderName());
        verify(accountRepository, times(1)).save(any(Account.class));
    }

    @Test
    @DisplayName("Test for getting account by ID")
    void givenAccountId_whenGetAccountById_thenReturnAccountDto() {
        // Given
        given(accountRepository.findById(1L)).willReturn(Optional.of(account));

        // When
        AccountDto foundAccount = accountService.getAccountById(1L);

        // Then
        assertNotNull(foundAccount);
        assertEquals(account.getId(), foundAccount.id());
        verify(accountRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Test for account not found exception")
    void givenInvalidAccountId_whenGetAccountById_thenThrowException() {
        // Given
        given(accountRepository.findById(1L)).willReturn(Optional.empty());

        // When/Then
        assertThrows(AccountException.class, () -> accountService.getAccountById(1L));
        verify(accountRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Test for depositing money")
    void givenAccountIdAndAmount_whenDeposit_thenReturnUpdatedAccount() {
        // Given
        double depositAmount = 500.0;
        double expectedBalance = account.getBalance() + depositAmount;

        Account updatedAccount = new Account();
        updatedAccount.setId(1L);
        updatedAccount.setAccountHolderName("John Doe");
        updatedAccount.setBalance(expectedBalance);

        given(accountRepository.findById(1L)).willReturn(Optional.of(account));
        given(accountRepository.save(any(Account.class))).willReturn(updatedAccount);

        // When
        AccountDto result = accountService.deposit(1L, depositAmount);

        // Then
        assertNotNull(result);
        assertEquals(expectedBalance, result.balance());
        verify(accountRepository, times(1)).findById(1L);
        verify(accountRepository, times(1)).save(any(Account.class));
    }

    @Test
    @DisplayName("Test for withdrawing money")
    void givenAccountIdAndAmount_whenWithdraw_thenReturnUpdatedAccount() {
        // Given
        double withdrawAmount = 500.0;
        double expectedBalance = account.getBalance() - withdrawAmount;

        Account updatedAccount = new Account();
        updatedAccount.setId(1L);
        updatedAccount.setAccountHolderName("John Doe");
        updatedAccount.setBalance(expectedBalance);

        given(accountRepository.findById(1L)).willReturn(Optional.of(account));
        given(accountRepository.save(any(Account.class))).willReturn(updatedAccount);

        // When
        AccountDto result = accountService.withdraw(1L, withdrawAmount);

        // Then
        assertNotNull(result);
        assertEquals(expectedBalance, result.balance());
        verify(accountRepository, times(1)).findById(1L);
        verify(accountRepository, times(1)).save(any(Account.class));
    }

    @Test
    @DisplayName("Test for insufficient balance when withdrawing")
    void givenInsufficientBalance_whenWithdraw_thenThrowException() {
        // Given
        double withdrawAmount = 2000.0;
        given(accountRepository.findById(1L)).willReturn(Optional.of(account));

        // When/Then
        assertThrows(RuntimeException.class, () -> accountService.withdraw(1L, withdrawAmount));
        verify(accountRepository, times(1)).findById(1L);
        verify(accountRepository, never()).save(any(Account.class));
    }

    @Test
    @DisplayName("Test for getting all accounts")
    void givenAccounts_whenGetAllAccounts_thenReturnAccountsList() {
        // Given
        Account account2 = new Account();
        account2.setId(2L);
        account2.setAccountHolderName("Jane Doe");
        account2.setBalance(2000.0);

        given(accountRepository.findAll()).willReturn(List.of(account, account2));

        // When
        List<AccountDto> accountList = accountService.getAllAccounts();

        // Then
        assertNotNull(accountList);
        assertEquals(2, accountList.size());
        assertEquals("John Doe", accountList.get(0).accountHolderName());
        assertEquals("Jane Doe", accountList.get(1).accountHolderName());
        verify(accountRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Test for deleting account")
    void givenAccountId_whenDeleteAccount_thenNothing() {
        // Given
        given(accountRepository.findById(1L)).willReturn(Optional.of(account));
        doNothing().when(accountRepository).delete(any(Account.class));

        // When
        accountService.deleteAccount(1L);

        // Then
        verify(accountRepository, times(1)).findById(1L);
        verify(accountRepository, times(1)).delete(account);
    }
}
