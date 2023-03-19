package cz.tul.stin.server.controller;
import cz.tul.stin.server.bank.*;
import cz.tul.stin.server.config.Const;
import cz.tul.stin.server.service.EmailSenderService;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

import java.util.ArrayList;
import java.util.List;

@RestController
@CrossOrigin("http://localhost:3000")
public class AccountController {
    /*
    @PostMapping("/getAllUserAccounts")
    public List<Account> loadUserAccounts(@RequestBody String clientId) {
        clientId = clientId.replace("=","");
        return Bank.getAllUserAccounts(Integer.parseInt(clientId));
    }
    */
    Account a;
    @PostMapping("/getUserData")
    public User getUser(@RequestBody String clientId){
        return Bank.getClient(Integer.parseInt(clientId.replace("=","")));
    }

    @PostMapping("/getAccountData")
    public Account getAccountData(@RequestBody String clientId){
        a = Bank.getAccount(Integer.parseInt(clientId.replace("=","")));
        return a;
    }
    @PostMapping("/getAccountBalance")
    public Currency getBalance(@RequestBody String waers){
        return a.getBalance(waers.replace("=",""));
    }
}
