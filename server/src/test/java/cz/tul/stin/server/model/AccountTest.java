package cz.tul.stin.server.model;

import cz.tul.stin.server.config.Const;
import org.json.simple.*;
import org.json.simple.parser.JSONParser;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.nio.file.Files;

public class AccountTest {

    @BeforeEach
    public void setup() throws IOException {
        // create a test file with one account
        File copied = new File("src/main/resources/dataTestAccount.json");
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

        JSONObject jo = new JSONObject();
        JSONArray ja = new JSONArray();
        Bank.JSON_FILE = "src/main/resources/dataTestAccount.json";

        JSONObject account = new JSONObject();
        account.put(Const.JKEY_ACCOUNT_NUMBER, 123);
        account.put(Const.JKEY_WRBTR, 1000);
        account.put(Const.JKEY_WAERS, "CZK");
        account.put(Const.JKEY_OWNER_ID, 456);

        ja.add(account);
        jo.put(Const.JKEY_BANK_ACCOUNTS, ja);

        try (FileWriter file = new FileWriter(Bank.JSON_FILE)) {
            file.write(jo.toString());
        }
    }

    @Test
    public void testCreateNewAccount() throws Exception {
        int result = Account.createNewAccount(789, 456, "CZK");
        Assertions.assertEquals(0, result);
        // check that the account was added to the file
        Object obj = new JSONParser().parse(new FileReader(Bank.JSON_FILE));
        JSONObject jo = (JSONObject) obj;
        JSONArray ja = (JSONArray) jo.get(Const.JKEY_BANK_ACCOUNTS);
        JSONObject newAccount = (JSONObject) ja.get(1);
        Assertions.assertEquals(456, Integer.parseInt(newAccount.get(Const.JKEY_ACCOUNT_NUMBER).toString()));
        Assertions.assertEquals(0, Integer.parseInt(newAccount.get(Const.JKEY_WRBTR).toString()));
        Assertions.assertEquals("CZK", newAccount.get(Const.JKEY_WAERS));
        Assertions.assertEquals(789, Integer.parseInt(newAccount.get(Const.JKEY_OWNER_ID).toString()));
    }

    @Test
    public void testCreateExistingAccount() throws Exception {
        int result = Account.createNewAccount(789, 123, "CZK");
        Assertions.assertEquals(1, result);
    }

    @Test
    public void testGetUsersCZKAccount() throws Exception {
        Account account = Account.getUsersCZKAccount(456);
        Assertions.assertEquals(123, account.getAccountNumber());
        Assertions.assertEquals(1000.0, account.getWrbtr());
        Assertions.assertEquals("CZK", account.getWaers());
        Assertions.assertEquals(456, account.getOwnerID());
    }

    @Test
    public void testUpdateAccountBalance() throws Exception {
        Account.updateAccountBalance(123, 2000);
        // check that the balance was updated in the file
        Object obj = new JSONParser().parse(new FileReader(Bank.JSON_FILE));
        JSONObject jo = (JSONObject) obj;
        JSONArray ja = (JSONArray) jo.get(Const.JKEY_BANK_ACCOUNTS);
        JSONObject updatedAccount = (JSONObject) ja.get(0);
        Assertions.assertEquals(2000.0, updatedAccount.get(Const.JKEY_WRBTR));
    }

    @Test
    public void testCheckIfAccountExists() throws Exception {
        Assertions.assertTrue(Account.checkIfAccountExists(123));
        Assertions.assertFalse(Account.checkIfAccountExists(789));
    }

    @Test
    public void testGetAccountFromJson() throws Exception {
        Account account = Account.getAccountFromJson(123);
        Assertions.assertEquals(456, account.getOwnerID());
        Assertions.assertEquals(123, account.getAccountNumber());
        Assertions.assertEquals(1000, account.getWrbtr());
        Assertions.assertEquals("CZK", account.getWaers());
    }

    @Test
    public void testGetAccountFromJsonNotFound() {
        Assertions.assertThrows(RuntimeException.class, () -> Account.getAccountFromJson(999999));
    }

    @AfterEach
    public void cleanup() {
        // delete the test file
        File file = new File("src/main/resources/dataTestAccount.json");
        file.delete();
        Bank.JSON_FILE = "src/main/resources/data.json";
    }
}

