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
    public static List<Account>bankAccounts;
    public static List<User>users;
    public static String exchangeRatesDate;

    public static void loadDataFromFile() throws IOException, ParseException {
        bankAccounts = new ArrayList<>();
        users = new ArrayList<>();
        Object obj = null;

        obj = new JSONParser().parse(new FileReader(Const.JSON_FILE));
        JSONObject jo = (JSONObject) obj;

        JSONArray ja = (JSONArray) jo.get(Const.JKEY_BANK_ACCOUNTS);

        for (Object o : ja) {
            JSONObject joi = (JSONObject) o;
            int accNumber = Integer.parseInt(joi.get(Const.JKEY_ACCOUNT_NUMBER).toString());

            int ownerID = Integer.parseInt(joi.get(Const.JKEY_OWNER_ID).toString());
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
            bankAccounts.add(new Account(ownerID, balance, transactions, accNumber));
        }

        ja = (JSONArray) jo.get(Const.JKEY_USERS);
        for (Object o : ja) {
            JSONObject joi = (JSONObject) o;
            int id = Integer.parseInt(joi.get(Const.JKEY_ID).toString());
            String firstname = joi.get(Const.JKEY_FIRSTNAME).toString();
            String surname = joi.get(Const.JKEY_SURNAME).toString();
            String email = joi.get(Const.JKEY_EMAIL).toString();
            users.add(new User(id, firstname, surname, email));
        }


    }

    public static User getClient(int clientId){
        for (User u : users){
            if(u.getId() == clientId)
                return u;
        }
        return null;
    }

    public static Account getAccount(int clientId) {
        for (Account a : bankAccounts) {
            if(clientId == a.getOwnerID()){
                return a;
            }
        }
        return null;
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
