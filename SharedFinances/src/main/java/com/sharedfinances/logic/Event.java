package main.java.com.sharedfinances.logic;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Event {

    private String code;
    private String name;
    private double totalAmount;
    private List<Person> persons;

    public Event(String code, String name) {
        this.code = code;
        this.name = name;
        this.totalAmount = 0;
        this.persons = new ArrayList<>();
    }

    public int getSize() {
        return persons.size();
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Person> getPersons() {
        return persons;
    }

    public void setPersons(List<Person> persons) {
        this.persons = persons;
    }

    public void addPerson(Person person) {
        persons.add(person);
    }

    public Person getPerson(String name) {
        return persons.stream().filter(person -> person.getName().equals(name)).findFirst().get();
    }

    public Person getPerson(int index) {
        return persons.get(index);
    }

    public void setPerson(Person p) {
        int i = persons.indexOf(p);
        persons.set(i, p);
    }

    public void calculateAmount(Person p, double amount) {
        p.setAmount(p.getAmount() + amount);
        for (Debtor d : p.getDebtors())
            d.addDebts(p.getAmount() / getSize());
    }

    public void manageDebtors(Person p) {
        List<Person> pers = persons.stream().filter(pe -> !pe.getName().equals(p.getName()))
                .collect(Collectors.toList());
        for (int i = 0; i < pers.size(); i++) {
            if (i == p.getDebtors().size())
                p.addDebtor(new Debtor(pers.get(i).getName()));
        }
    }

    public void eliminate(Person p) {
        int temp = 0;
        int z = persons.indexOf(p) - 1;
        for (int i = 0; i < p.getDebtors().size(); i++) {
            if (i == persons.indexOf(p)) {
                temp++;
                z++;
            }
            Person pp = persons.get(temp);
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
        int z = persons.indexOf(p) - 1;
        double td = 0;
        for (int i = 0; i < p.getDebtors().size(); i++) {
            if (i == persons.indexOf(p)) {
                temp++;
                z++;
            }
            td += persons.get(temp).getDebtors().get(z).getDebts();
            temp++;
        }
        return td;
    }

    public double addTotalAmount(double amount) {
        totalAmount += amount;
        return totalAmount;
    }
}
