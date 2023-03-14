package cz.tul.stin.server.bank;
import cz.tul.stin.server.config.Const;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.FileNotFoundException;
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

    public String provideTransaction( Transaction t ) throws Exception {
        for ( Currency b : balance ) {
            if (b.getWaers().equals(t.getCurrency().getWaers())){
                switch(t.getOperation()) {
                    case '+':
                        transactions.add(new Transaction(t.getOperation(), t.getCurrency()));
                        b.setWrbtr(b.getWrbtr() + t.getCurrency().getWrbtr());
                        updateJsonData( t, b );
                        return "Transakce probehla uspesne.";
                    case '-':
                        // TODO pri malem zustatku prevod na CZK
                        if (b.getWrbtr() >= t.getCurrency().getWrbtr()) {
                            transactions.add(new Transaction(t.getOperation(), t.getCurrency()));
                            b.setWrbtr(b.getWrbtr() - t.getCurrency().getWrbtr());
                            updateJsonData( t, b );
                            return "Transakce probehla uspesne.";
                        } else {
                            return "Chyba, nedostatecny zustatek.";
                        }
                    default:
                        return "Chyba, zadan spatny operator transakce.";
                }
            }
        }
        return "Chyba, ucet neobsahuje zadanou menu.";
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
                ", ownerName='" + ownerName + '\'' +
                ", email='" + email + '\'' +
                ", balance=" + balance +
                ", transactions=" + transactions +
                '}';
    }
}
