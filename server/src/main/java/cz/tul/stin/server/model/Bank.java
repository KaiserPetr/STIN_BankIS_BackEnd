package cz.tul.stin.server.model;

import java.util.*;
import java.io.*;
import java.net.URL;
import cz.tul.stin.server.config.Const;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.*;

public class Bank {

    public static final String [] CURRENCIES = {"AUD","CAD","CZK","EUR","GBP","HUF","PLN","CHF","USD"};
    public static String JSON_FILE = "src/main/resources/data.json";

    public static void downloadExchangeRates() throws Exception {
        URL url = new URL(Const.CNB_URL);
        try {
            Scanner sc = new Scanner(url.openStream());
            int line = 0;
            String waers, wrbtr, menge;
            while (sc.hasNextLine()){
                String s = sc.nextLine();
                if (line == 0) {
                    updateExchanegRateDate(s.split(" ")[0]);
                } else if (line == 1) {
                    line++;
                    continue;
                } else {
                    String [] array = s.split("\\|");
                    menge = array[2];
                    waers = array[3];
                    wrbtr = array[4].replace(",",".");
                    updateExchangeRate(Float.valueOf(wrbtr)/Float.valueOf(menge),waers);
                }
                line++;
            }
        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    public static String getExchanegRateDate() throws Exception{
        Object obj = new JSONParser().parse(new FileReader(JSON_FILE));
        JSONObject jo = (JSONObject) obj;
        return jo.get(Const.JKEY_CNB_DATE).toString();
    }
    public static void updateExchanegRateDate(String newDate) throws Exception{
        Object obj = new JSONParser().parse(new FileReader(JSON_FILE));
        JSONObject jo = (JSONObject) obj;
        jo.replace(Const.JKEY_CNB_DATE,newDate);

        try (FileWriter file = new FileWriter(JSON_FILE)) {
            file.write(jo.toString());
        }
    }

    public static void updateExchangeRate(Float wrbtr, String waers) throws Exception{
        Object obj = new JSONParser().parse(new FileReader(JSON_FILE));
        JSONObject jo = (JSONObject) obj;

        JSONArray ja = (JSONArray) jo.get(Const.JKEY_CNB);
        for (Object o : ja) {
            JSONObject joi = (JSONObject) o;
            String jWaers = joi.get(Const.JKEY_WAERS).toString();
            if (jWaers.equals(waers)) {
                String newWrbtr = String.format("%.3f",wrbtr);
                joi.replace(Const.JKEY_WRBTR,newWrbtr);
                break;
            }

        }

        try (FileWriter file = new FileWriter(JSON_FILE)) {
            file.write(jo.toString());
        }
    }

    public static float getExchangeRate(String waers) throws Exception {
        Object obj = new JSONParser().parse(new FileReader(JSON_FILE));
        JSONObject jo = (JSONObject) obj;

        JSONArray ja = (JSONArray) jo.get(Const.JKEY_CNB);

        for (Object o : ja) {
            JSONObject joi = (JSONObject) o;
            String jWaers = joi.get(Const.JKEY_WAERS).toString();
            if (jWaers.equals(waers)) {
                return Float.parseFloat(joi.get(Const.JKEY_WRBTR).toString().replace(",","."));
            }
        }

        throw new RuntimeException("Mena nenalezena.");
    }

    public static String generateRandomCode(){
        Random random = new Random();
        return String.format("%04d", random.nextInt(10000));
    }
}
