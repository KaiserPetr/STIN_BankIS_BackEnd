package cz.tul.stin.server.model;

import cz.tul.stin.server.config.Const;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static cz.tul.stin.server.model.Account.*;
import static cz.tul.stin.server.model.Account.getUsersCZKAccount;
import static cz.tul.stin.server.model.Bank.getExchangeRate;
import static cz.tul.stin.server.model.User.getUserAccounts;

public class Transaction {

    private int accNum;
    private char operation;
    private float wrbtr;
    private String waers, message;

    public Transaction(int accNum, char operation, Float wrbtr, String waers, String message) {
        this.accNum = accNum;
        this.operation = operation;
        this.wrbtr = wrbtr;
        this.waers = waers;
        this.message = message;
    }

    public Transaction(int accNum,char operation, Float wrbtr, String waers) {
        this.accNum = accNum;
        this.operation = operation;
        this.wrbtr = wrbtr;
        this.waers = waers;
        this.message = "";
    }

    public int getAccNum() {
        return accNum;
    }

    public float getWrbtr() {
        return wrbtr;
    }

    public String getWaers() {
        return waers;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public char getOperation() {
        return operation;
    }

    public String getMessage() {
        return message;
    }

    public static int provideTransaction(char operation, float wrbtr, String waers, int userID ) throws Exception {

        Transaction t = null;
        Account a = null;

        if (!waers.equals("CZK")){
            List<Account> accounts = getUserAccounts(userID);
            for (Account ia : accounts){
                if (ia.getWaers().equals(waers)){
                    a = ia;
                    t =  new Transaction(ia.getAccountNumber(),operation, wrbtr, waers);
                    break;
                }
            }
            if (t == null) {
                a = getUsersCZKAccount(userID);
                t = new Transaction(a.getAccountNumber(), operation, wrbtr, waers);
            }
        } else {
            a = getUsersCZKAccount(userID);
            t =  new Transaction(a.getAccountNumber(),operation, wrbtr, waers);
        }

        switch (t.getOperation()){
            case '+':
                if (a.getWaers().equals(t.getWaers())){
                    updateAccountBalance(t.getAccNum(),a.getWrbtr()+t.getWrbtr());
                    writeTransactionToJson(t);
                    return t.getAccNum();
                } else {
                    updateAccountBalance(a.getAccountNumber(),a.getWrbtr() + t.getWrbtr() * getExchangeRate(t.getWaers()));
                    Float exRate = getExchangeRate(t.getWaers());
                    String msg = String.format("%s:CZK~1:%.2f",t.getWaers(),exRate);
                    writeTransactionToJson(new Transaction(a.getAccountNumber(),t.getOperation(),t.getWrbtr() * exRate,"CZK",msg));
                    return a.getAccountNumber();
                }
            case '-':
                if (a.getWaers().equals(t.getWaers())){
                    if (t.getWrbtr() > a.getWrbtr()){
                        Float exRate = getExchangeRate(t.getWaers());
                        a = getUsersCZKAccount(userID);
                        if (exRate * t.getWrbtr() > a.getWrbtr()){
                            return 0;
                        } else {
                            updateAccountBalance(a.getAccountNumber(),a.getWrbtr() - t.getWrbtr() * exRate);
                            String msg = String.format("%s:CZK~1:%.2f",t.getWaers(),exRate);
                            writeTransactionToJson(new Transaction(a.getAccountNumber(),t.getOperation(),t.getWrbtr() * exRate,"CZK",msg));
                            return a.getAccountNumber();
                        }
                    } else {
                        updateAccountBalance(t.getAccNum(),a.getWrbtr()-t.getWrbtr());
                        writeTransactionToJson(t);
                        return t.getAccNum();
                    }
                } else {
                    Float exRate = getExchangeRate(t.getWaers());
                    a = getUsersCZKAccount(userID);
                    if (exRate * t.getWrbtr() > a.getWrbtr()){
                        return 0;
                    } else {
                        updateAccountBalance(a.getAccountNumber(),a.getWrbtr() - t.getWrbtr() * exRate);
                        String msg = String.format("%s:CZK~1:%.2f",t.getWaers(),exRate);
                        writeTransactionToJson(new Transaction(a.getAccountNumber(),t.getOperation(),t.getWrbtr() * exRate,"CZK",msg));
                        return a.getAccountNumber();
                    }
                }
            default:
                return 1;
        }

    }

    public static void writeTransactionToJson(Transaction t) throws Exception{
        Object obj = new JSONParser().parse(new FileReader(Bank.JSON_FILE));
        JSONObject jo = (JSONObject) obj;
        JSONArray ja = (JSONArray) jo.get(Const.JKEY_TRANSACTIONS);

        JSONObject newTransaction = new JSONObject();
        newTransaction.put(Const.JKEY_ACCOUNT_NUMBER,t.getAccNum());
        newTransaction.put(Const.JKEY_OPERATION,String.valueOf(t.getOperation()));
        newTransaction.put(Const.JKEY_WRBTR,t.getWrbtr());
        newTransaction.put(Const.JKEY_WAERS,t.getWaers());
        newTransaction.put(Const.JKEY_MESSAGE,t.getMessage());
        ja.add(newTransaction);
        try (FileWriter file = new FileWriter(Bank.JSON_FILE)) {
            file.write(jo.toString());
        }
    }

    public static Transaction generateRandomTransaction(int accNum){
        char[] operators = {'+','-'};
        int rndIndexOp = new Random().nextInt(operators.length);
        char rndOperator = operators[rndIndexOp];
        int rndIndexBalance = new Random().nextInt(Bank.CURRENCIES.length);
        String rndWaers = Bank.CURRENCIES[rndIndexBalance];
        float rndWrbtr = (int)Math.round(Math.random() * 1000); //random cele cislo od 0 do 1000
        return new Transaction( accNum, rndOperator, rndWrbtr, rndWaers);
    }

    public static List<Transaction> getTransactions(int accNumber) throws Exception{
        List<Transaction> transactions = new ArrayList<>();
        Object obj = new JSONParser().parse(new FileReader(Bank.JSON_FILE));
        JSONObject jo = (JSONObject) obj;
        JSONArray ja = (JSONArray) jo.get(Const.JKEY_TRANSACTIONS);
        for (Object o : ja) {
            JSONObject joi = (JSONObject) o;
            int jAccNumber = Integer.parseInt(joi.get(Const.JKEY_ACCOUNT_NUMBER).toString());
            if (jAccNumber == accNumber){
                float wrbtr = Float.parseFloat(joi.get(Const.JKEY_WRBTR).toString());
                char operation = joi.get(Const.JKEY_OPERATION).toString().charAt(0);
                String waers = joi.get(Const.JKEY_WAERS).toString();
                String msg = joi.get(Const.JKEY_MESSAGE).toString();
                transactions.add(new Transaction(accNumber,operation,wrbtr,waers,msg));
            }
        }
        return transactions;
    }

}
