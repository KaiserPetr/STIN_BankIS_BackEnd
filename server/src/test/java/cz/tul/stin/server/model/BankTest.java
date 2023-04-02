package cz.tul.stin.server.model;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;

public class BankTest {

    @BeforeEach
    public void setup() throws IOException {

        File copied = new File("src/main/resources/dataTestBank.json");
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

        Bank.JSON_FILE = "src/main/resources/dataTestBank.json";
    }

    @Test
    public void testDownloadExchangeRates() {
        Assertions.assertDoesNotThrow(Bank::downloadExchangeRates);
        File file = new File(Bank.JSON_FILE);
        Assertions.assertTrue(file.exists() && !file.isDirectory());
    }

    @Test
    public void testGetExchanegRateDate() throws Exception {
        Assertions.assertEquals(Bank.getExchanegRateDate(), "31.03.2023");
    }

    @Test
    public void testUpdateExchanegRateDate() throws Exception {
        String newDate = "30.03.2023";
        Bank.updateExchanegRateDate(newDate);
        Assertions.assertEquals(Bank.getExchanegRateDate(), newDate);
    }

    @Test
    public void testUpdateExchangeRate() throws Exception {
        Bank.updateExchangeRate(1.5f, "EUR");
        Assertions.assertEquals(Bank.getExchangeRate("EUR"), 1.5f);
    }

    @Test
    public void testGetExchangeRate() throws Exception {
        List<String> currencies = Arrays.asList(Bank.CURRENCIES);
        for (String currency : currencies) {
            Assertions.assertTrue(Bank.getExchangeRate(currency) > 0);
        }
        Assertions.assertThrows(RuntimeException.class, () -> Bank.getExchangeRate("XXX"));
    }

    @Test
    public void testGenerateRandomCode() {
        String code = Bank.generateRandomCode();
        Assertions.assertNotNull(code);
        Assertions.assertEquals(code.length(), 4);
    }

    @AfterEach
    public void cleanup() {
        // delete the test file
        File file = new File("src/main/resources/dataTestBank.json");
        file.delete();
        Bank.JSON_FILE = "src/main/resources/data.json";
    }

}
