package cz.tul.stin.server.controller;
import cz.tul.stin.server.bank.*;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@CrossOrigin("http://localhost:3000")
public class AccountController {
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

    @PostMapping("/getExchangeRate")
    public Float getExRate(@RequestBody String waers) {return Bank.getExchangeRate(waers.replace("=","")); }

    @PostMapping("/newTransaction")
    public int newTrans(@RequestBody Object params) {
        try {
            String[] p = params.toString().replace("[","").replace("]","").split(",");
            for (int i = 0; i < p.length; i++)
                p[i] = p[i].trim();
            Transaction t;
            if (p.length > 4) {
                t = new Transaction(p[0], p[1].charAt(0),
                        new Currency(p[3], Float.parseFloat(p[2])),
                        p[4]);
            } else {
                t = new Transaction(p[0], p[1].charAt(0),
                        new Currency(p[3], Float.parseFloat(p[2])));
            }
            return a.provideTransaction(t);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    @GetMapping("/getCurrencies")
    public List<String>getCurrencies(){ return a.getCurrencies();}

    @GetMapping("/getAllCurrencies")
    public List<String>getAllCurrencies() throws Exception {
        Bank.downloadExchangeRates();
        List<String>list=Bank.getAllCurrencies();
        list.add(0,"CZK");
        return list;}

    @GetMapping("/getTransactions")
    public List<Transaction>getTransactions(){ return a.getTransactions(); }

    @GetMapping("/generateRandomTransaction")
    public Transaction genRndTrans(){ return a.generateRandomTransaction(); }

    @GetMapping("/getNewID")
    public String getNewId(){
        return String.format("%04d",a.getTransactions().size() + 1);
    }

    @GetMapping("/downloadExchangeRates")
    public String downloadExchangeRates() throws Exception {
        Bank.downloadExchangeRates();
        return Bank.exchangeRatesDate;
    }
}
