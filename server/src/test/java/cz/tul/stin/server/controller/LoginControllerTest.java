package cz.tul.stin.server.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import cz.tul.stin.server.config.Const;
import cz.tul.stin.server.model.Bank;
import cz.tul.stin.server.model.User;
import cz.tul.stin.server.service.EmailSenderService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@RunWith(MockitoJUnitRunner.class)
public class LoginControllerTest {
    /*
    private MockMvc mockMvc;

    @Mock
    private EmailSenderService emailSenderService;

    @InjectMocks
    private LoginController loginController;

    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(loginController).build();
    }

    @Test
    public void testLogin() throws Exception {
        // Mock User.getUserData()
        User mockUser = mock(User.class);
        when(User.getUserData(1234)).thenReturn(mockUser);
        when(mockUser.getEmail()).thenReturn("petr.kaiser@tul.cz");

        // Mock Bank.generateRandomCode()
        String mockCode = "1234";
        when(Bank.generateRandomCode()).thenReturn(mockCode);

        // Perform POST request
        RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/")
                .contentType("application/json")
                .content("clientId=1234");

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        // Assert response code and body
        int status = result.getResponse().getStatus();
        String response = result.getResponse().getContentAsString();
        assertEquals(200, status);
        assertEquals(mockCode, response);

        // Verify EmailSenderService.sendSimpleEmail()
        String expectedSubject = Const.EMAIL_SUBJECT;
        String expectedMessage = String.format("Váš kód pro přilášení je: %s", mockCode);
        verify(emailSenderService).sendSimpleEmail("test@example.com", expectedSubject, expectedMessage);
    }

     */
}
