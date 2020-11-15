package main.java.com.sharedfinances.main;

import com.google.gson.Gson;
import main.java.com.sharedfinances.broker.RabbitMQ;
import main.java.com.sharedfinances.logic.Debtor;
import main.java.com.sharedfinances.logic.Management;
import main.java.com.sharedfinances.logic.Person;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Logger;

public class Main {

    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());
    private static final Management management = new Management();
    private static final int INTERVAL = 5;

    public static void main(String[] args) {
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
            //Get Json Data from the Rest API
            try {
                management.setList(getFromRestAPI());
            } catch (IOException | ParseException e) {
                LOGGER.info("Failed1");
            }
            //Button message from RabbitMQ
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
                    messages.clear();

                    //Send Json Data to Rest API
                    try {
                        sendToRestAPI(management.getList());
                    } catch (IOException e) {
                        LOGGER.info("Failed1");
                    }
                }

            }
            try {
                Thread.sleep(INTERVAL);
            } catch (InterruptedException e) {
                LOGGER.info("Add Amount Thread interrupt!");
                Thread.currentThread().interrupt();
            }
        }
    }

    public static void extraAmount() {
        RabbitMQ rabbit2 = new RabbitMQ("extra");
        rabbit2.subscribeToAMQP();
        List<JSONObject> messages;
        while (true) {
            //Get Json Data from the Rest API
            try {
                management.setList(getFromRestAPI());
            } catch (IOException | ParseException e) {
                e.printStackTrace();
            }
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
                    messages.clear();

                    //Send Json Data to Rest API
                    try {
                        sendToRestAPI(management.getList());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            try {
                Thread.sleep(INTERVAL);
            } catch (InterruptedException e) {
                LOGGER.info("Extra Amount Thread interrupt!");
                Thread.currentThread().interrupt();
            }
        }
    }

    public static void pay() {
        RabbitMQ rabbit3 = new RabbitMQ("extra");
        rabbit3.subscribeToAMQP();
        List<JSONObject> messages;
        while (true) {
            try {
                management.setList(getFromRestAPI());
            } catch (IOException | ParseException e) {
                e.printStackTrace();
            }
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
                    messages.clear();
                }
            }
            try {
                Thread.sleep(INTERVAL);
            } catch (InterruptedException e) {
                LOGGER.info("Pay Thread interrupt!");
                Thread.currentThread().interrupt();
            }
        }
    }

    public static List<Person> getFromRestAPI() throws IOException, ParseException {
        URL url = new URL("http://raspijk.ddns.net:8095/api/persons/list");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        con.connect();
        int responseCode = con.getResponseCode();
        if (responseCode != 200) {
            throw new RuntimeException("HttpResponseCode " + responseCode);
        } else {
            Scanner sc = new Scanner(url.openStream());
            StringBuilder inline = new StringBuilder();
            while (sc.hasNext()) {
                inline.append(sc.nextLine());
            }
            LOGGER.info(inline.toString());
            sc.close();
            JSONParser jsonParser = new JSONParser();
            JSONArray jsonArray = (JSONArray) jsonParser.parse(inline.toString());
            List<Person> list = new ArrayList<>();
            Gson gson = new Gson();
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                list.add(gson.fromJson(jsonObject.toJSONString(), Person.class));
            }
            return list;
        }
    }

    public static void sendToRestAPI(List<Person> list) throws IOException {
        URL url = new URL("http://raspijk.ddns.net:8095/api/persons/list/all");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setDoOutput(true);
        con.setRequestMethod("PUT");
        con.connect();
        int responseCode = con.getResponseCode();
        if (responseCode != 200) {
            throw new RuntimeException("HttpResponseCode " + responseCode);
        } else {
            Gson gson = new Gson();
            String jsonString = gson.toJson(list);
            OutputStreamWriter out = new OutputStreamWriter(con.getOutputStream());
            out.write(jsonString);
            out.close();
        }
    }

}
