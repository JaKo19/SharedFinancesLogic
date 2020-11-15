package main.java.com.sharedfinances.logic;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Management {

    private List<Person> list;
    private double tamount;

    public Management() {
        list = new ArrayList<>();
    }

    public void setList(List<Person> list){
        this.list = list;
    }

    public List<Person> getList() {
        return list;
    }

    public int getSize() {
        return list.size();
    }

    public void addPerson(Person p) {
        list.add(p);
    }

    public Person getPerson(String name) {
        return list.stream().filter(person -> person.getName().equals(name)).findFirst().get();
    }

    public Person getPerson(int i) {
        return list.get(i);
    }

    // update
    public void setPerson(Person p) {
        int i = getList().indexOf(p);
        getList().set(i, p);
    }

    public void calculateAmount(Person p, double amount, int i) {
        p.setAmount(p.getAmount() + amount);
        for (Debtor d : p.getDebtors())
            d.addDebts(p.getAmount() / i);
    }

    public void manageDebtors(Person p) {
        List<Person> persons = getList().stream().filter(pe -> !pe.getName().equals(p.getName()))
                .collect(Collectors.toList());
        for (int i = 0; i < persons.size(); i++) {
            if (i == p.getDebtors().size())
                p.addDebtor(new Debtor(persons.get(i).getName()));
        }
    }

    public void eliminate(Person p) {
        int temp = 0;
        int z = list.indexOf(p) - 1;
        for (int i = 0; i < p.getDebtors().size(); i++) {
            if (i == list.indexOf(p)) {
                temp++;
                z++;
            }
            Person pp = list.get(temp);
            Debtor d = pp.getDebtors().get(z);
            if (d.getDebts() <= p.getDebtors().get(i).getDebts()) {
                double j = d.getDebts();
                p.getDebtors().get(i).addExtraDebts(-j);
                d.addExtraDebts(-j);
            } else {
                double j = p.getDebtors().get(i).getDebts();
                d.addExtraDebts(-j);
                p.getDebtors().get(i).addExtraDebts(-j);
            }

            if (d.getDebts() < 0) {
                double j = -d.getDebts();
                d.addExtraDebts(j);
                p.getDebtors().get(i).addExtraDebts(j);
            }
            if (p.getDebtors().get(i).getDebts() < 0) {
                double j = -p.getDebtors().get(i).getDebts();
                d.addExtraDebts(j);
                p.getDebtors().get(i).addExtraDebts(j);
            }

            setPerson(pp);
            temp++;
        }
    }

    public double totalDebts(Person p) {
        int temp = 0;
        int z = list.indexOf(p) - 1;
        double td = 0;
        for (int i = 0; i < p.getDebtors().size(); i++) {
            if (i == list.indexOf(p)) {
                temp++;
                z++;
            }
            td += list.get(temp).getDebtors().get(z).getDebts();
            temp++;
        }
        return td;
    }

    public double totalAmount(double amount) {
        tamount += amount;
        return tamount;
    }
}
