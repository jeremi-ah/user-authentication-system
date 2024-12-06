package net.dev.banking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.dev.banking.dto.AccountDto;
import net.dev.banking.exception.AccountException;
import net.dev.banking.service.AccountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class AccountControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private AccountService accountService;

    @InjectMocks
    private AccountController accountController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(accountController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    @DisplayName("POST /api/accounts - Should create new account with valid input")
    void testCreateAccount_WhenValidInput_ShouldReturnCreatedAccount() throws Exception {
        // Given
        AccountDto inputDto = new AccountDto(null, "John Doe", 1000.0);
        AccountDto savedDto = new AccountDto(1L, "John Doe", 1000.0);
        when(accountService.createAccount(any(AccountDto.class))).thenReturn(savedDto);

        // When & Then
        mockMvc.perform(post("/api/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputDto)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.accountHolderName").value("John Doe"))
                .andExpect(jsonPath("$.balance").value(1000.0));
    }

    @Test
    @DisplayName("GET /api/accounts/{id} - Should retrieve existing account")
    void testGetAccount_WhenAccountExists_ShouldReturnAccount() throws Exception {
        // Given
        Long accountId = 1L;
        AccountDto accountDto = new AccountDto(accountId, "John Doe", 1000.0);
        when(accountService.getAccountById(accountId)).thenReturn(accountDto);

        // When & Then
        mockMvc.perform(get("/api/accounts/{id}", accountId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(accountId))
                .andExpect(jsonPath("$.accountHolderName").value("John Doe"))
                .andExpect(jsonPath("$.balance").value(1000.0));
    }

    @Test
    @DisplayName("PUT /api/accounts/{id}/deposit - Should update balance after deposit")
    void testDeposit_WhenValidAmount_ShouldUpdateBalance() throws Exception {
        // Given
        Long accountId = 1L;
        Double depositAmount = 500.0;
        AccountDto updatedAccount = new AccountDto(accountId, "John Doe", 1500.0);
        Map<String, Double> request = new HashMap<>();
        request.put("amount", depositAmount);

        when(accountService.deposit(eq(accountId), eq(depositAmount))).thenReturn(updatedAccount);

        // When & Then
        mockMvc.perform(put("/api/accounts/{id}/deposit", accountId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance").value(1500.0));
    }

    @Test
    @DisplayName("PUT /api/accounts/{id}/withdraw - Should update balance after withdrawal")
    void testWithdraw_WhenSufficientBalance_ShouldUpdateBalance() throws Exception {
        // Given
        Long accountId = 1L;
        Double withdrawAmount = 300.0;
        AccountDto updatedAccount = new AccountDto(accountId, "John Doe", 700.0);
        Map<String, Double> request = new HashMap<>();
        request.put("amount", withdrawAmount);

        when(accountService.withdraw(eq(accountId), eq(withdrawAmount))).thenReturn(updatedAccount);

        // When & Then
        mockMvc.perform(put("/api/accounts/{id}/withdraw", accountId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance").value(700.0));
    }

    @Test
    @DisplayName("GET /api/accounts - Should retrieve all existing accounts")
    void testGetAllAccounts_WhenAccountsExist_ShouldReturnAllAccounts() throws Exception {
        // Given
        AccountDto account1 = new AccountDto(1L, "John Doe", 1000.0);
        AccountDto account2 = new AccountDto(2L, "Jane Doe", 2000.0);
        when(accountService.getAllAccounts()).thenReturn(Arrays.asList(account1, account2));

        // When & Then
        mockMvc.perform(get("/api/accounts"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[0].accountHolderName").value("John Doe"))
                .andExpect(jsonPath("$[1].accountHolderName").value("Jane Doe"));
    }

    @Test
    @DisplayName("DELETE /api/accounts/{id} - Should delete existing account")
    void testDeleteAccount_WhenAccountExists_ShouldRemoveAccount() throws Exception {
        // Given
        Long accountId = 1L;
        doNothing().when(accountService).deleteAccount(accountId);

        // When & Then
        mockMvc.perform(delete("/api/accounts/{id}", accountId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("Account Deleted"));
    }

}