package cz.tul.stin.server.model;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.*;
import java.nio.file.Files;
import java.util.List;

@SpringBootTest
public class UserTest {

    @BeforeEach
    public void setup() throws IOException {
        // create a test file with one account
        File copied = new File("src/main/resources/dataTestUser.json");
        File original = new File("src/main/resources/dataTest.json");

        try (
                InputStream in = new BufferedInputStream(
                        Files.newInputStream(original.toPath()));
                OutputStream out = new BufferedOutputStream(
                        Files.newOutputStream(copied.toPath()))) {

            byte[] buffer = new byte[1024];
            int lengthRead;
            while ((lengthRead = in.read(buffer)) > 0) {
                out.write(buffer, 0, lengthRead);
                out.flush();
            }
        }

        Bank.JSON_FILE = "src/main/resources/dataTestUser.json";
    }

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

    @AfterEach
    public void cleanup() {
        // delete the test file
        File file = new File("src/main/resources/dataTestUser.json");
        file.delete();
        Bank.JSON_FILE = "src/main/resources/data.json";
    }

}

