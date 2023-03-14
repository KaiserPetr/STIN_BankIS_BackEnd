package cz.tul.stin.server.bank;

import java.util.*;
import java.io.*;
import java.net.URL;

import cz.tul.stin.server.config.Const;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.*;

public class Bank {

    private List<Currency>currencies;

    public Bank() {
        this.currencies = new ArrayList<Currency>();
    }

    public static Account loadAccountDataFromFile(int accountNumber) throws Exception {
        Object obj = new JSONParser().parse(new FileReader(Const.JSON_FILE));
        JSONObject jo = (JSONObject) obj;

        JSONArray ja = (JSONArray) jo.get(Const.JKEY_BANK_ACCOUNTS);

        for (Object o : ja) {
            JSONObject joi = (JSONObject) o;
            int currAccNumber = Integer.parseInt(joi.get(Const.JKEY_ACCOUNT_NUMBER).toString());
            if (accountNumber == currAccNumber) {
                String owner = joi.get(Const.JKEY_OWNER_NAME).toString();
                String email = joi.get(Const.JKEY_EMAIL).toString();
                List<Currency> balance = new ArrayList<Currency>();
                JSONArray jaBalance = (JSONArray) joi.get(Const.JKEY_ACCOUNT_BALANCE);
                for (Object oBalance : jaBalance) {
                    JSONObject joBalance = (JSONObject) oBalance;
                    float wrbtr = Float.parseFloat(joBalance.get(Const.JKEY_WRBTR).toString());
                    String waers = joBalance.get(Const.JKEY_WAERS).toString();
                    balance.add(new Currency(waers, wrbtr));
                }
                List<Transaction> transactions = new ArrayList<Transaction>();
                JSONArray jaTransactions = (JSONArray) joi.get(Const.JKEY_TRANSACTIONS);
                for (Object oTransaction : jaTransactions) {
                    JSONObject joTransaction = (JSONObject) oTransaction;
                    String operation = joTransaction.get(Const.JKEY_OPERATION).toString();
                    float wrbtr = Float.parseFloat(joTransaction.get(Const.JKEY_WRBTR).toString());
                    String waers = joTransaction.get(Const.JKEY_WAERS).toString();
                    transactions.add(new Transaction(operation.charAt(0), new Currency(waers, wrbtr)));
                }
                return new Account(accountNumber, owner, email, balance, transactions);
            }
        }
        throw new Exception("Provided json key was not found in json file!");
    }


    public void currenciesParser(String urlString) throws Exception {
        URL url = new URL(urlString);
        try {
            Scanner sc = new Scanner(url.openStream());
            int i = 0;
            String date, waers, wrbtr;
            while (sc.hasNextLine()){
                if (i == 0) {
                    sc.useDelimiter(" ");
                    date = sc.next();
                } else if (i == 1) {
                    i++;
                    continue;
                } else {
                    sc.useDelimiter("\\|");
                    sc.next();
                    sc.next();
                    sc.next();
                    waers = sc.next();
                    wrbtr = sc.next();
                    currencies.add(new Currency(waers, Float.parseFloat(wrbtr)));
                }
                i++;
            }
        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }

}
