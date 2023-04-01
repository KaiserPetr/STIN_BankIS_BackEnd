package cz.tul.stin.server.model;

import cz.tul.stin.server.config.Const;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class BankTest {

    @Test
    public void testDownloadExchangeRates() {
        Assertions.assertDoesNotThrow(Bank::downloadExchangeRates);
        File file = new File(Const.JSON_FILE);
        Assertions.assertTrue(file.exists() && !file.isDirectory());
    }

    @Test
    public void testGetExchanegRateDate() throws Exception {
        Assertions.assertEquals(Bank.getExchanegRateDate(), Const.JKEY_CNB_DATE);
    }

    @Test
    public void testUpdateExchanegRateDate() throws Exception {
        String newDate = "2022-04-01";
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
}
