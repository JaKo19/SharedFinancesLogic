package com.sharedfinances.restapi.documents;

import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class Debtor {

    private String name;
    private double debts;
    private double extra;

    public Debtor(String name, double debts, double extra) {
        this.debts = debts;
        this.extra = extra;
        this.name = name;
    }

    public double getDebts() {
        return debts;
    }

    public void setDebts(double debts) {
        this.debts = debts;
    }

    public double getExtra() {
        return extra;
    }

    public void setExtra(double extra) {
        this.extra = extra;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
