package cz.tul.stin.server.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import cz.tul.stin.server.model.Account;
import cz.tul.stin.server.model.Bank;
import cz.tul.stin.server.model.Transaction;
import cz.tul.stin.server.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class AccountControllerTest {
    /*
    private MockMvc mockMvc;

    @Mock
    private Bank bank;

    @Mock
    private User user;

    @Mock
    private Account account;

    @Mock
    private Transaction transaction;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(new AccountController()).build();
    }

    @Test
    public void testGetUserData() throws Exception {
        when(user.getId()).thenReturn(1);
        when(user.getFirstname()).thenReturn("John");
        when(user.getSurname()).thenReturn("Doe");
        when(user.getEmail()).thenReturn("johndoe@gmail.com");

        mockMvc.perform(MockMvcRequestBuilders.post("/getUserData")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("clientId=1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId", is(1)))
                .andExpect(jsonPath("$.firstName", is("John")))
                .andExpect(jsonPath("$.lastName", is("Doe")))
                .andExpect(jsonPath("$.email", is("johndoe@gmail.com")));
    }
    /*
    @Test
    public void testGetAccountsData() throws Exception {
        List<Account> accounts = new ArrayList<>();
        when(account.getAccountNumber()).thenReturn(123456);
        when(account.getWaers()).thenReturn("CZK");
        accounts.add(account);
        when(User.getUserAccounts(anyInt())).thenReturn(accounts);

        mockMvc.perform(MockMvcRequestBuilders.post("/getAccountsData")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("clientId=1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].accountNumber", is(123456)))
                .andExpect(jsonPath("$[0].accountName", is("Savings Account")));
    }

    @Test
    public void testGetExchangeRate() throws Exception {
        when(Bank.getExchangeRate(anyString())).thenReturn(1.2f);

        mockMvc.perform(MockMvcRequestBuilders.post("/getExchangeRate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("waers=EUR"))
                .andExpect(status().isOk())
                .andExpect(content().string("1.2"));
    }
    */
}

