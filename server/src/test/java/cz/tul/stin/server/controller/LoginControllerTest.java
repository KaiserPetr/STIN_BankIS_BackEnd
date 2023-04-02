package cz.tul.stin.server.controller;

import cz.tul.stin.server.config.Const;
import cz.tul.stin.server.model.Bank;
import cz.tul.stin.server.model.User;
import cz.tul.stin.server.service.EmailSenderService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.jupiter.api.Assertions.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class LoginControllerTest {

    @Mock
    private EmailSenderService emailSenderService;

    @InjectMocks
    private LoginController loginController;

    @Test
    public void testLogin_Success() throws Exception {
        // Arrange
        String clientId = "123";
        User user = new User(123, "John", "Doe", "johndoe@example.com");
        Mockito.when(User.getUserData(Integer.parseInt(clientId))).thenReturn(user);
        String expectedCode = "123456";
        Mockito.when(Bank.generateRandomCode()).thenReturn(expectedCode);
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
        Mockito.when(User.getUserData(Integer.parseInt(clientId))).thenReturn(null);

        // Act
        loginController.login(clientId);
    }

}
