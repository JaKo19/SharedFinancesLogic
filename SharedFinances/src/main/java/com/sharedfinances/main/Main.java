package main.java.com.sharedfinances.main;

import main.java.com.sharedfinances.broker.RabbitMQ;
import main.java.com.sharedfinances.database.PersistenceException;
import main.java.com.sharedfinances.logic.Debtor;
import main.java.com.sharedfinances.logic.Management;
import main.java.com.sharedfinances.logic.Person;
import org.json.simple.JSONObject;

import java.util.List;
import java.util.logging.Logger;

public class Main {

    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());
    private static final Management management = new Management();

    public static void main(String[] args) {
        //Test Data
        Person p1 = new Person("Jannis");
        Person p2 = new Person("Jonathan");
        p1.addDebtor(new Debtor("Jonathan"));
        p2.addDebtor(new Debtor("Jannis"));
        management.addPerson(p1);
        management.addPerson(p2);


        Thread t1 = new Thread(Main::addAmount);
        Thread t2 = new Thread(Main::extraAmount);
        Thread t3 = new Thread(Main::pay);

        t1.start();
        t2.start();
    }

    public static void addAmount() {
        RabbitMQ rabbit1 = new RabbitMQ("addamount");
        rabbit1.subscribeToAMQP();
        List<JSONObject> messages;
        while (true) {
            messages = rabbit1.getMessages();
            if (!messages.isEmpty()) {
                for (JSONObject message : messages) {
                    String name = (String) message.get("name");
                    long amount = (long) message.get("amount");
                    Person p = management.getPerson(name);
                    management.calculateAmount(p, amount, management.getSize());
                    p.addTotal(amount);
                    management.eliminate(p);
                    management.setPerson(p);

                    //Serialize and Send to Broker
                    try {
                        rabbit1.publishToAMQP(management.saveList());
                    } catch (PersistenceException e) {
                        e.printStackTrace();
                    }

                    LOGGER.info("Successfully added Amount!");
                }
                messages.clear();
            }
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                LOGGER.info("Interrupted addAmount Thread");
                return;
            }
        }
    }

    public static void extraAmount() {
        RabbitMQ rabbit2 = new RabbitMQ("extra");
        rabbit2.subscribeToAMQP();
        List<JSONObject> messages;
        while (true) {
            messages = rabbit2.getMessages();
            if (!messages.isEmpty()) {
                for (JSONObject message : messages) {
                    String name = (String) message.get("name");
                    long amount = (long) message.get("amount");
                    Person p = management.getPerson("Jannis");//ToDo
                    Debtor d = p.getDebtor(name);
                    d.addExtraDebts(amount);
                    p.addTotal(amount);
                    management.eliminate(p);
                    management.setPerson(p);

                    //Serialize and Send to Broker
                    try {
                        rabbit2.publishToAMQP(management.saveList());
                    } catch (PersistenceException e) {
                        e.printStackTrace();
                    }

                    LOGGER.info("Successfully added Extra Amount!");
                }
                messages.clear();
            }
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                LOGGER.info("Interrupted extraAmount Thread");
                return;
            }
        }
    }

    public static void pay() {
        RabbitMQ rabbit3 = new RabbitMQ("extra");
        rabbit3.subscribeToAMQP();
        List<JSONObject> messages;
        while (true) {
            messages = rabbit3.getMessages();
            if (!messages.isEmpty()) {
                for (JSONObject message : messages) {
                    String name = (String) message.get("name");
                    long amount = (long) message.get("amount");
                    Person p = management.getPerson("Jannis");//ToDo
                    Debtor d = p.getDebtor(name);
                    int i = 0;
                    int z;
                    while (!d.getName().equals(management.getPerson(i).getName()))
                        i++;
                    Person pp = management.getPerson(i);
                    if (management.getList().indexOf(p) < i)
                        z = (i - 1);
                    else
                        z = i;
                    Debtor dd = pp.getDebtors().get(z);
                    dd.addExtraDebts(-amount);
                    management.eliminate(pp);
                    management.totalDebts(p);
                    management.setPerson(pp);

                    //Serialize and Send to Broker
                    try {
                        rabbit3.publishToAMQP(management.saveList());
                    } catch (PersistenceException e) {
                        e.printStackTrace();
                    }

                }
                messages.clear();
            }
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                LOGGER.info("Interrupted pay Thread");
            }
            LOGGER.info("Waiting for Messages...");
        }
    }


}
