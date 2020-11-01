package main.java.com.sharedfinances.main;

import main.java.com.sharedfinances.database.MongoDBConnector;
import main.java.com.sharedfinances.logic.Management;
import main.java.com.sharedfinances.logic.Person;
import org.json.simple.JSONObject;

import java.util.logging.Logger;

public class Main {

    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) {
        LOGGER.info("Starting MongoDB-Connector...");
        MongoDBConnector.start("raspijk.ddns.net", 27017, "sharedfinances", "persons", null);

        RabbitMQ rabbitMQ = new RabbitMQ("addamount");

        Management management = new Management();
        //Test Data
        management.addPerson(new Person("Jannis"));
        while (true) {
            for (JSONObject message : rabbitMQ.getMessages()) {
                String name = (String) message.get("name");
                float amount = (float) message.get("amount");
                Person p = management.getPerson(name);
                management.calculateAmount(p, amount, management.getSize());
                p.addTotal(amount);
                management.eliminate(p);
                management.setPerson(p);
            }
        }
    }


}
