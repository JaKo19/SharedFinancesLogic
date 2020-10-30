package main.java.com.sharedfinances.logic;

import java.io.Serializable;

public class Debtor implements Serializable {

    private static final long serialVersionUID = -1776416725477627327L;
    private double debts;
    private double extra;
    private final String name;

    public Debtor(String name) {
        this.name = name;
    }

    public double getDebts() {
        return (debts + extra);
    }

    public void addDebts(double debts) {
        this.debts = debts;
    }

    public void addExtraDebts(double debts) {
        extra += debts;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return getDebts() != 0 ? String.format("%10s (%.2f €)", getName(), getDebts()) : "";
    }
}
