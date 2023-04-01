package cz.tul.stin.server.model;

import cz.tul.stin.server.config.Const;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class User {
    private int id;
    private String firstname, surname, email;

    public User(int id, String firstname, String surname, String email) {
        this.id = id;
        this.firstname = firstname;
        this.surname = surname;
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public int getId() {
        return id;
    }

    public String getFirstname() {
        return firstname;
    }

    public String getSurname() {
        return surname;
    }

    public static User getUserData(int id) throws Exception {
        Object obj = new JSONParser().parse(new FileReader(Bank.JSON_FILE));
        JSONObject jo = (JSONObject) obj;

        JSONArray ja = (JSONArray) jo.get(Const.JKEY_USERS);

        for (Object o : ja) {
            JSONObject joi = (JSONObject) o;
            int jId = Integer.parseInt(joi.get(Const.JKEY_ID).toString());
            if(jId == id){

                String firstName = joi.get(Const.JKEY_FIRSTNAME).toString();
                String surName = joi.get(Const.JKEY_SURNAME).toString();
                String email = joi.get(Const.JKEY_EMAIL).toString();
                return new User(id, firstName, surName, email);
            }
        }
        throw new RuntimeException("Uzivatel nenalezen");
    }

    public static List<Account> getUserAccounts(int userId) throws Exception {
        List<Account> accounts = new ArrayList<>();
        Object obj = new JSONParser().parse(new FileReader(Bank.JSON_FILE));
        JSONObject jo = (JSONObject) obj;
        JSONArray ja = (JSONArray) jo.get(Const.JKEY_BANK_ACCOUNTS);
        for (Object o : ja) {
            JSONObject joi = (JSONObject) o;
            int jOwnerId = Integer.parseInt(joi.get(Const.JKEY_OWNER_ID).toString());
            if (jOwnerId == userId){
                int accNumber = Integer.parseInt(joi.get(Const.JKEY_ACCOUNT_NUMBER).toString());
                float wrbtr = Float.parseFloat(joi.get(Const.JKEY_WRBTR).toString());
                String waers = joi.get(Const.JKEY_WAERS).toString();

                accounts.add(new Account(userId,accNumber,wrbtr,waers));
            }

        }

        return accounts;
    }
}
