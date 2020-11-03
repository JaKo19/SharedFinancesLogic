package main.java.com.sharedfinances.logic;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Person implements Comparable<Person>, Serializable {

	private static final long serialVersionUID = -3342841278243320276L;
	private String name;
	private double amount;
	private double total;
	private List<Debtor> debtors;

	public Person(String name) {
		this.name = name;
		debtors = new ArrayList<>();
	}

	public String getName() {
		return name;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public List<Debtor> getDebtors() {
		return this.debtors;
	}

	public Debtor getDebtor(String name){
		return getDebtors().stream().filter(debtor -> debtor.getName().equals(name)).findFirst().get();
	}

	public void addDebtor(Debtor d) {
		debtors.add(d);
	}

	@Override
	public String toString() {
		return String.format("%-11s%s", (getName() + ":"), debtors.toString());
	}

	@Override
	public int compareTo(Person o) {
		char s1 = Character.toLowerCase(o.getName().charAt(0));
		char s2 = Character.toLowerCase(getName().charAt(0));
		if (s1 > s2)
			return -1;
		if (s1 < s2)
			return 1;
		return 0;
	}

	public double getTotal() {
		return total;
	}

	public void addTotal(double total) {
		this.total += total;
	}

	public double income() {
		double income = 0;
		for (Debtor d: debtors)
			income += d.getDebts();
		return income;
	}

}
