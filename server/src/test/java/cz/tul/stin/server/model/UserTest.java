package cz.tul.stin.server.model;

import cz.tul.stin.server.model.Account;
import cz.tul.stin.server.model.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class UserTest {

    @Test
    public void testGetUserData() throws Exception {
        // Test valid user ID
        User user = User.getUserData(1234);
        Assertions.assertEquals(1234, user.getId());
        Assertions.assertEquals("Petr", user.getFirstname());
        Assertions.assertEquals("Kaiser", user.getSurname());
        Assertions.assertEquals("petr.kaiser@tul.cz", user.getEmail());

        // Test invalid user ID
        Assertions.assertThrows(RuntimeException.class, () -> User.getUserData(999));
    }

    @Test
    public void testGetUserAccounts() throws Exception {
        // Test user with no accounts
        List<Account> accounts = User.getUserAccounts(3);
        Assertions.assertEquals(0, accounts.size());

        // Test user with one account
        accounts = User.getUserAccounts(4321);
        Assertions.assertEquals(1, accounts.size());
        Account account = accounts.get(0);
        Assertions.assertEquals(4321, account.getOwnerID());
        Assertions.assertEquals(753195423, account.getAccountNumber());
        Assertions.assertEquals(200, account.getWrbtr());
        Assertions.assertEquals("CZK", account.getWaers());

        // Test user with multiple accounts
        accounts = User.getUserAccounts(1234);
        Assertions.assertEquals(2, accounts.size());
    }

}

