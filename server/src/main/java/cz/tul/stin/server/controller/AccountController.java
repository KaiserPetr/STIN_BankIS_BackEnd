package cz.tul.stin.server.controller;
import cz.tul.stin.server.model.*;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static cz.tul.stin.server.model.Account.createNewAccount;
import static cz.tul.stin.server.model.Bank.getExchangeRate;
import static cz.tul.stin.server.model.Transaction.*;
import static cz.tul.stin.server.model.User.getUserAccounts;
import static cz.tul.stin.server.model.User.getUserData;


@RestController
@CrossOrigin("http://localhost:3000")
public class AccountController {

    @PostMapping("/getUserData")
    public User getUser(@RequestBody String clientId) throws Exception {
        return getUserData(Integer.parseInt(clientId.replace("=","")));
    }
    @PostMapping("/getAccountsData")
    public List<Account> getAccountsData(@RequestBody String clientId) throws Exception {
        return getUserAccounts(Integer.parseInt(clientId.replace("=","")));
    }

    @PostMapping("/getExchangeRate")
    public Float getExRate(@RequestBody String waers) throws Exception {return getExchangeRate(waers.replace("=","")); }

    @PostMapping("/newTransaction")
    public int newTrans(@RequestBody Object params) {
        try {
            String[] p = params.toString().replace("[","").replace("]","").split(",");
            for (int i = 0; i < p.length; i++)
                p[i] = p[i].trim();
            return provideTransaction(p[1].charAt(0),Float.parseFloat(p[2]),p[3],Integer.parseInt(p[0]));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/getAllCurrencies")
    public List<String>getAllCurrencies() throws Exception {
        return new ArrayList<>( Arrays.asList(Bank.CURRENCIES) );
    }

    @PostMapping("/getTransactions")
    public List<Transaction>getAccTransactions(@RequestBody String accNum) throws Exception {
        return getTransactions(Integer.parseInt(accNum.replace("=","")));
    }

    @PostMapping("/generateRandomTransaction")
    public Transaction genRndTrans(@RequestBody String accNum){ return generateRandomTransaction(Integer.parseInt(accNum.replace("=",""))); }

    @GetMapping("/downloadExchangeRates")
    public String downloadExchangeRates() throws Exception {
        Bank.downloadExchangeRates();
        return Bank.getExchanegRateDate();
    }

    @PostMapping("/createNewAccount")
    public int createNewAcc (@RequestBody Object params) throws Exception{
        String[] p = params.toString().replace("[","").replace("]","").split(",");
        for (int i = 0; i < p.length; i++) {
            p[i] = p[i].trim();
        }
        return createNewAccount(Integer.parseInt(p[0]),Integer.parseInt(p[1]),p[2]);
    }
}
