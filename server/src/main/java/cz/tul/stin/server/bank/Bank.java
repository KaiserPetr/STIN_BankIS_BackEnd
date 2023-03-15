package cz.tul.stin.server.bank;

import java.util.*;
import java.io.*;
import java.net.URL;

import cz.tul.stin.server.config.Const;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.*;


public class Bank {

    public static List<Currency>exchangeRates;
    public static String exchangeRatesDate;

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
                    String msg = joTransaction.get(Const.JKEY_MESSAGE).toString();
                    transactions.add(new Transaction(operation.charAt(0), new Currency(waers, wrbtr), msg));
                }
                return new Account(accountNumber, owner, email, balance, transactions);
            }
        }
        throw new Exception("Provided json key was not found in json file!");
    }

    public static void downloadExchangeRates() throws Exception {
        URL url = new URL(Const.CNB_URL);
        try {
            exchangeRates = new ArrayList<Currency>();
            Scanner sc = new Scanner(url.openStream());
            int line = 0;
            String waers, wrbtr;
            while (sc.hasNextLine()){
                String s = sc.nextLine();
                if (line == 0) {
                    exchangeRatesDate = s.split(" ")[0];
                } else if (line == 1) {
                    line++;
                    continue;
                } else {
                    String [] array = s.split("\\|");
                    waers = array[3];
                    wrbtr = array[4].replace(",",".");
                    exchangeRates.add(new Currency(waers, Float.parseFloat(wrbtr)));
                }
                line++;
            }
        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    public static String generateRandomCode(){
        Random random = new Random();
        return String.format("%04d", random.nextInt(10000));
    }

    public static Float getExchangeRate(String waers){
        for (Currency c : exchangeRates) {
            if (c.getWaers().equals(waers)){
                return c.getWrbtr();
            }
        }
        throw new RuntimeException("Aktualni kurz pro zadany klic meny nenalezen");
    }

}
