package cz.tul.stin.server.bank;
import cz.tul.stin.server.config.Const;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.*;
import java.util.*;
public class Account {
    private int accountNumber;
    private String ownerName, email;
    private List<Currency>balance;
    private List<Transaction>transactions;

    public Account(int accountNumber, String ownerName, String email, List<Currency> balance, List<Transaction> transactions) {
        this.accountNumber = accountNumber;
        this.ownerName = ownerName;
        this.email = email;
        this.balance = balance;
        this.transactions = transactions;
    }

    public String provideTransaction(Transaction t) throws Exception {
        switch(t.getOperation()) {
            case '+':
                transactions.add(new Transaction(t.getOperation(), t.getCurrency()));
                setBalance(new Currency(t.getCurrency().getWaers(),getBalance(t.getCurrency().getWaers()).getWrbtr()+t.getCurrency().getWrbtr()));
                updateJsonData( t, getBalance(t.getCurrency().getWaers()) );
                return "Transakce probehla uspesne.";
            case '-':
                // v dane mene je dostatecny zustatek
                if (getBalance(t.getCurrency().getWaers()).getWrbtr() >= t.getCurrency().getWrbtr() ){
                    transactions.add(new Transaction(t.getOperation(), t.getCurrency()));
                    setBalance(new Currency(t.getCurrency().getWaers(),getBalance(t.getCurrency().getWaers()).getWrbtr()-t.getCurrency().getWrbtr()));
                    updateJsonData( t, getBalance(t.getCurrency().getWaers()) );
                    return "Transakce probehla uspesne.";
                } else {
                    if (!t.getCurrency().getWaers().equals("CZK")) {
                        //dana mena nema dostatecny zustatek, prevod na czk
                        float exchangeRate = Bank.getExchangeRate(t.getCurrency().getWaers());
                        float wrbtrCZK = t.getCurrency().getWrbtr() * exchangeRate;
                        Currency balanceCZK = getBalance("CZK");
                        if (balanceCZK.getWrbtr() >= wrbtrCZK) {
                            String msg = String.format("%s:CZK 1:%.2f",t.getCurrency().getWaers(),exchangeRate);
                            transactions.add(new Transaction(t.getOperation(), new Currency("CZK", wrbtrCZK), msg ));
                            setBalance(new Currency("CZK",balanceCZK.getWrbtr()-wrbtrCZK));
                            t.setMessage(msg);
                            updateJsonData( t, getBalance("CZK") );
                            return "Transakce probehla uspesne.";
                        } else {
                            return "Chyba, nedostatecny zustatek.";
                        }
                    } else {
                        return "Chyba, nedostatecny zustatek.";
                    }
                }
            default:
                return "Chyba, zadan spatny operator transakce.";
        }
    }

    public Transaction generateRandomTransaction(){
        char[] operators = {'+','-'};
        int rndIndexOp = new Random().nextInt(operators.length);
        char rndOperator = operators[rndIndexOp];
        int rndIndexBalance = new Random().nextInt(operators.length);
        String rndWaers = balance.get(rndIndexBalance).getWaers();
        int rndWrbtr = (int)Math.round(Math.random() * 1000); //random cele cislo od 0 do 1000

        return new Transaction(rndOperator, new Currency(rndWaers,rndWrbtr));
    }

    public Currency getBalance(String waers){
        for (Currency b : balance) {
            if (b.getWaers().equals(waers)){
                return b;
            }
        }
        throw new RuntimeException("Ucet neobsahuje zustatek v teto mene");
    }

    public void setBalance(Currency c){
        for (Currency b : balance) {
            if (b.getWaers().equals(c.getWaers())){
                b.setWrbtr(c.getWrbtr());
                return;
            }
        }
        throw new RuntimeException("Ucet neobsahuje zustatek v teto mene");
    }

    public void updateJsonData( Transaction t, Currency b ) throws Exception {
        Object obj = new JSONParser().parse(new FileReader(Const.JSON_FILE));
        JSONObject jo = (JSONObject) obj;
        JSONArray ja = (JSONArray) jo.get(Const.JKEY_BANK_ACCOUNTS);
        boolean changed = false;

        for (Object o : ja) {
            JSONObject joi = (JSONObject) o;
            int currAccNumber = Integer.parseInt(joi.get(Const.JKEY_ACCOUNT_NUMBER).toString());
            if (accountNumber == currAccNumber) {
                JSONArray jaBalance = (JSONArray) joi.get(Const.JKEY_ACCOUNT_BALANCE);
                int index = 0;
                for (Object oBalance : jaBalance) {
                    JSONObject joBalance = (JSONObject) oBalance;
                    if (joBalance.get(Const.JKEY_WAERS).toString().equals(b.getWaers())){
                        joBalance.put(Const.JKEY_WRBTR,String.valueOf(b.getWrbtr()));
                        jaBalance.set(index, joBalance);
                    }
                    index++;
                }

                JSONArray jaTransactions = (JSONArray) joi.get(Const.JKEY_TRANSACTIONS);
                JSONObject newTransaction = new JSONObject();
                newTransaction.put(Const.JKEY_WAERS,t.getCurrency().getWaers());
                newTransaction.put(Const.JKEY_WRBTR,String.valueOf(t.getCurrency().getWrbtr()));
                newTransaction.put(Const.JKEY_OPERATION,String.valueOf(t.getOperation()));
                newTransaction.put(Const.JKEY_MESSAGE,t.getMessage());
                jaTransactions.add(newTransaction);
                changed = true;
                break;
            }
        }
        if (changed) {
            try (FileWriter file = new FileWriter(Const.JSON_FILE)) {
                file.write(jo.toString());
            }
        }
    }

    public String getEmail() {
        return email;
    }

    @Override
    public String toString() {
        return "Account{" +
                "accountNumber=" + accountNumber +
                ", ownerName='" + ownerName + '\'' +
                ", email='" + email + '\'' +
                ", balance=" + balance +
                ", transactions=" + transactions +
                '}';
    }
}
