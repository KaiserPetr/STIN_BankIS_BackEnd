package cz.tul.stin.server.model;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.*;

class TransactionTest {

    @BeforeEach
    public void setup() throws IOException {
        // create a test file with one account
        File copied = new File("src/main/resources/dataTestTransaction.json");
        File original = new File("src/main/resources/data.json");

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

        Bank.JSON_FILE = "src/main/resources/dataTestTransaction.json";
    }

    @Test
    void testTransactionConstructor() {
        Transaction t = new Transaction(123, '+', 100.0f, "USD", "Test message");
        assertNotNull(t);
        assertEquals(123, t.getAccNum());
        assertEquals('+', t.getOperation());
        assertEquals(100.0f, t.getWrbtr());
        assertEquals("USD", t.getWaers());
        assertEquals("Test message", t.getMessage());
    }

    @Test
    void testTransactionConstructorWithoutMessage() {
        Transaction t = new Transaction(456, '-', 50.0f, "EUR");
        assertNotNull(t);
        assertEquals(456, t.getAccNum());
        assertEquals('-', t.getOperation());
        assertEquals(50.0f, t.getWrbtr());
        assertEquals("EUR", t.getWaers());
        assertEquals("", t.getMessage());
    }

    @Test
    void testSetMessage() {
        Transaction t = new Transaction(789, '-', 200.0f, "GBP");
        t.setMessage("New message");
        assertEquals("New message", t.getMessage());
    }

    @AfterEach
    public void cleanup() {
        // delete the test file
        File file = new File("src/main/resources/dataTestTransaction.json");
        file.delete();
        Bank.JSON_FILE = "src/main/resources/data.json";
    }

}

