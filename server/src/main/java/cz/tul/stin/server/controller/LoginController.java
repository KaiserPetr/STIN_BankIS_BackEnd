package cz.tul.stin.server.controller;
import cz.tul.stin.server.bank.*;
import cz.tul.stin.server.config.Const;
import cz.tul.stin.server.service.EmailSenderService;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

import java.io.IOException;
import java.util.Objects;

@RestController
@CrossOrigin("http://localhost:3000")
public class LoginController {
    @Autowired
    private EmailSenderService service;

    @EventListener(ApplicationReadyEvent.class)
    public void loadData() {
        try {
            Bank.loadDataFromFile();
        } catch (IOException | ParseException e) {
            throw new RuntimeException(e);
        }
    }
    @PostMapping("/")
    public String login(@RequestBody String accNum) {
        accNum = accNum.replace("=","");
        Account a = Bank.getAccount(Integer.parseInt(accNum));
        if (a != null) {
            String code = Bank.generateRandomCode();
            String msg = String.format("Váš kód pro přilášení je: %s",code);
            service.sendSimpleEmail(Objects.requireNonNull(Bank.getAccountOwner(a)).getEmail(), Const.EMAIL_SUBJECT,msg);
            return code;
        } else {
            return "-1";
        }
    }

    public static boolean isNumeric(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch(NumberFormatException e){
            return false;
        }
    }
    // TODO toto volat jen, kdyz je MD kazdou minutu mezi 14:20-14:50
    //Bank.downloadExchangeRates();
}

