package cz.tul.stin.server.bank;
import java.util.*;
public class Currency {
    private String waers;
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
