package cz.tul.stin.server.bank;
import java.util.*;
public class Currency {
    private String waers;
    public static final List<String> waersList = Arrays.asList("CZK","EUR","USD","GBP");
    private float wrbtr;

    public Currency(String currency, float wrbtr) {
        this.waers = currency;
        this.wrbtr = wrbtr;
    }

    public Currency(String currency) {
        this.waers = currency;
        this.wrbtr = 0;
    }

    public String getWaers() {
        return waers;
    }

    public void setWaers(String currency) {
        this.waers = currency;
    }

    public float getWrbtr() {
        return wrbtr;
    }

    public void setWrbtr(float wrbtr) {
        this.wrbtr = wrbtr;
    }

    @Override
    public String toString() {
        return "Currency{" +
                "waers='" + waers + '\'' +
                ", wrbtr=" + wrbtr +
                '}';
    }
}
