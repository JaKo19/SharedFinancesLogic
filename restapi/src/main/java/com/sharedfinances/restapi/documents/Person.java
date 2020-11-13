package com.sharedfinances.restapi.documents;

import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document
public class Person {

    private String name;
    private double amount;
    private double total;
    private List<Debtor> debtors;

    public Person(String name, double amount, double total, List<Debtor> debtors) {
        this.name = name;
        this.amount = amount;
        this.total = total;
        this.debtors = debtors;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }


    public List<Debtor> getDebtors() {
        return debtors;
    }

    public void setDebtors(List<Debtor> debtors) {
        this.debtors = debtors;
    }
}
