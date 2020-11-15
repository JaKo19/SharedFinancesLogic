package main.java.com.sharedfinances.logic;

public class Debtor {

    private final String name;
    private double debts;
    private double extra;

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
