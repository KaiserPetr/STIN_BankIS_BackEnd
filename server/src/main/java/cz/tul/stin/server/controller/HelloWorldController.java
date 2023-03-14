package cz.tul.stin.server.controller;

import cz.tul.stin.server.bank.Bank;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.FileNotFoundException;
import java.util.Objects;

@RestController
public class HelloWorldController {

    Bank bank = new Bank();
    private static final String CNB = "https://www.cnb.cz/cs/financni-trhy/devizovy-trh/kurzy-devizoveho-trhu/kurzy-devizoveho-trhu/denni_kurz.txt";

    @CrossOrigin(origins = "http://localhost:3000")
    @RequestMapping("/")
    public String hello() throws Exception {
        return Objects.requireNonNull(Bank.loadAccountDataFromFile(123456789)).toString();
    }

    /*
    @RequestMapping("/time")
    public String time() {
        //return System.time
        return "";
    }
    */
}

