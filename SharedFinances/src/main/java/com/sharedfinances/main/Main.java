package main.java.com.sharedfinances.main;

import main.java.com.sharedfinances.logic.Person;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Main {

    private static final Logger LOGGER = LogManager.getLogger(Main.class);

    public static void main(String[] args) {
        Person p = new Person("Jannis");
        p.setAmount(100);
        LOGGER.error(p.getAmount());
    }


}
