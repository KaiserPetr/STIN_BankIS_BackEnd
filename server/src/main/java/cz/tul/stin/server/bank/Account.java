package cz.tul.stin.server.bank;
import cz.tul.stin.server.config.Const;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;


import java.io.*;
import java.util.*;
public class Account {
    private int ownerID, accountNumber;
    private List<Currency>balance;
    private List<Transaction>transactions;

    public Account(int ownerID, List<Currency> balance, List<Transaction> transactions, int accountNumber) {
        this.ownerID = ownerID;
        this.balance = balance;
        this.transactions = transactions;
        this.accountNumber = accountNumber;
    }

    public int provideTransaction(Transaction t) throws Exception {
        int tResult;
        switch(t.getOperation()) {
            case '+':
                // ucet obsahuje danou menu
                if (containsCurrency(t.getCurrency().getWaers())) {
                    transactions.add(new Transaction(t.getId(), t.getOperation(), t.getCurrency(), t.getMessage() ));
                    setBalance(new Currency(t.getCurrency().getWaers(), getBalance(t.getCurrency().getWaers()).getWrbtr() + t.getCurrency().getWrbtr()));
                    tResult = 0;
                } else {
                    float exchangeRate = Bank.getExchangeRate(t.getCurrency().getWaers());
                    float wrbtrCZK = t.getCurrency().getWrbtr() * exchangeRate;
                    Currency balanceCZK = getBalance("CZK");
                    transactions.add(new Transaction(t.getId(), t.getOperation(), t.getCurrency(), t.getMessage() ));
                    setBalance(new Currency("CZK",balanceCZK.getWrbtr()+wrbtrCZK));
                    tResult = 1;
                }
                updateJsonData(t, getBalance(t.getCurrency().getWaers()));
                return tResult;
            case '-':
                // v dane mene je dostatecny zustatek
                if (getBalance(t.getCurrency().getWaers()).getWrbtr() >= t.getCurrency().getWrbtr() ){
                    transactions.add(new Transaction(t.getId(), t.getOperation(), t.getCurrency(), t.getMessage()));
                    setBalance(new Currency(t.getCurrency().getWaers(),getBalance(t.getCurrency().getWaers()).getWrbtr()-t.getCurrency().getWrbtr()));
                    updateJsonData( t, getBalance(t.getCurrency().getWaers()) );
                    tResult = 0;
                    break;
                } else {
                    if (!t.getCurrency().getWaers().equals("CZK")) {
                        //dana mena nema dostatecny zustatek, prevod na czk
                        float exchangeRate = Bank.getExchangeRate(t.getCurrency().getWaers());
                        float wrbtrCZK = t.getCurrency().getWrbtr() * exchangeRate;
                        Currency balanceCZK = getBalance("CZK");
                        if (balanceCZK.getWrbtr() >= wrbtrCZK) {
                            //transactions.add(new Transaction(t.getId(), t.getOperation(), new Currency("CZK", wrbtrCZK), msg ));
                            transactions.add(new Transaction(t.getId(), t.getOperation(), t.getCurrency(), t.getMessage() ));
                            setBalance(new Currency("CZK",balanceCZK.getWrbtr()-wrbtrCZK));
                            updateJsonData( t, getBalance("CZK") );
                            tResult = 1;
                        } else {
                            tResult = 2;
                        }
                    } else {
                        tResult = 2;
                    }
                    break;
                }
            default:
                tResult = 3;
                break;
        }
        return tResult;
    }

    public Transaction generateRandomTransaction(){
        char[] operators = {'+','-'};
        int rndIndexOp = new Random().nextInt(operators.length);
        char rndOperator = operators[rndIndexOp];
        int rndIndexBalance = new Random().nextInt(operators.length);
        String rndWaers = Bank.exchangeRates.get(rndIndexBalance).getWaers();
        int rndWrbtr = (int)Math.round(Math.random() * 1000); //random cele cislo od 0 do 1000
        int id = transactions.size() + 1;
        return new Transaction(String.format("%04d",id), rndOperator, new Currency(rndWaers,rndWrbtr));
    }

    public boolean containsCurrency(String waers){
        for (Currency b : balance) {
            if (b.getWaers().equals(waers)){
                return true;
            }
        }
        return false;
    }

    public Currency getBalance(String waers){
        for (Currency b : balance) {
            if (b.getWaers().equals(waers)){
                return b;
            }
        }
        return new Currency(waers,-1);
    }
    
    public List<String>getCurrencies(){
        List<String>currencies = new ArrayList<>();
        for (Currency b : balance) {
            currencies.add(b.getWaers());
        }
        return currencies;
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

    public int getAccountNumber() {
        return accountNumber;
    }

    public int getOwnerID() {
        return ownerID;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public void updateJsonData(Transaction t, Currency b ) throws Exception {
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
                newTransaction.put(Const.JKEY_ID,t.getId());
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

    @Override
    public String toString() {
        return "Account{" +
                "accountNumber=" + accountNumber +
                ", ownerID='" + ownerID + '\'' +
                ", balance=" + balance.toString() +
                ", transactions=" + transactions.toString() +
                '}';
    }
}
