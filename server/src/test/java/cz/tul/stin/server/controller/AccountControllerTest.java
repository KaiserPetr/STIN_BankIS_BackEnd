package cz.tul.stin.server.controller;

import cz.tul.stin.server.config.Const;
import cz.tul.stin.server.model.Bank;
import cz.tul.stin.server.model.User;
import cz.tul.stin.server.service.EmailSenderService;
import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

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

    @RunWith(MockitoJUnitRunner.class)
    public static class LoginControllerTest {

        @Mock
        private EmailSenderService emailSenderService;

        @InjectMocks
        private LoginController loginController;

        @org.junit.Test
        public void testLogin_Success() throws Exception {
            // Arrange
            String clientId = "123";
            User user = new User(123, "John", "Doe", "johndoe@example.com");
            when(User.getUserData(Integer.parseInt(clientId))).thenReturn(user);
            String expectedCode = "123456";
            when(Bank.generateRandomCode()).thenReturn(expectedCode);
            ArgumentCaptor<String> emailCaptor = ArgumentCaptor.forClass(String.class);
            ArgumentCaptor<String> subjectCaptor = ArgumentCaptor.forClass(String.class);
            ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);

            // Act
            String result = loginController.login(clientId);

            // Assert
            assertEquals(expectedCode, result);
            Mockito.verify(emailSenderService, Mockito.times(1)).sendSimpleEmail(emailCaptor.capture(),
                    subjectCaptor.capture(), messageCaptor.capture());
            assertEquals(user.getEmail(), emailCaptor.getValue());
            assertEquals(Const.EMAIL_SUBJECT, subjectCaptor.getValue());
            assertEquals(String.format("Váš kód pro přilášení je: %s", expectedCode), messageCaptor.getValue());
        }

        @Test(expected = Exception.class)
        public void testLogin_Fail() throws Exception {
            // Arrange
            String clientId = "123";
            when(User.getUserData(Integer.parseInt(clientId))).thenReturn(null);

            // Act
            loginController.login(clientId);
        }

    }
}

