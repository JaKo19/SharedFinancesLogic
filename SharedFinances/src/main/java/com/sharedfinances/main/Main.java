package main.java.com.sharedfinances.main;

import main.java.com.sharedfinances.database.MongoDBConnector;
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
        //LOGGER.info("Starting MongoDB-Connector...");
        //MongoDBConnector.start("raspijk.ddns.net", 27017, "sharedfinances", "persons", null);

        management.addPerson(new Person("Jannis")); //Test Data
        management.addPerson(new Person("Lea")); //Test Data

        Thread t1 = new Thread(Main::addAmount);
        Thread t2 = new Thread(Main::extraAmount);
        Thread t3 = new Thread(Main::pay);

        t1.start();
        t2.start();
    }

    public static void addAmount() {
        RabbitMQ rabbitMQ = new RabbitMQ("addamount");
        List<JSONObject> messages;
        while (true) {
            messages = rabbitMQ.getMessages();
            if (!messages.isEmpty()) {
                for (JSONObject message : messages) {
                    String name = (String) message.get("name");
                    long amount = (long) message.get("amount");
                    synchronized (management) {
                        Person p = management.getPerson(name);
                        management.calculateAmount(p, amount, management.getSize());
                        p.addTotal(amount);
                        management.eliminate(p);
                        management.setPerson(p);
                        LOGGER.info("Successfully added Amount!");
                    }
                }
            }
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                LOGGER.info("Interrupted addAmount Thread");
            }
            LOGGER.info("Waiting for Messages...");
        }
    }

    public static void extraAmount() {
        RabbitMQ rabbitMQ = new RabbitMQ("extra");
        List<JSONObject> messages;
        while (true) {
            messages = rabbitMQ.getMessages();
            if (!messages.isEmpty()) {
                for (JSONObject message : messages) {
                    String name = (String) message.get("name");
                    long amount = (long) message.get("amount");
                    synchronized (management) {
                        Person p = management.getPerson("Jannis");//ToDo
                        Debtor d = p.getDebtor(name);
                        d.addExtraDebts(amount);
                        p.addTotal(amount);
                        management.eliminate(p);
                        management.setPerson(p);
                    }
                }
            }
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                LOGGER.info("Interrupted extraAmount Thread");
            }
            LOGGER.info("Waiting for Messages...");
        }
    }

    public static void pay() {
        RabbitMQ rabbitMQ = new RabbitMQ("extra");
        List<JSONObject> messages;
        while (true) {
            messages = rabbitMQ.getMessages();
            if (!messages.isEmpty()) {
                for (JSONObject message : messages) {
                    String name = (String) message.get("name");
                    long amount = (long) message.get("amount");
                    synchronized (management) {
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
                    }
                }
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
