package main.java.com.sharedfinances.main;

import com.google.gson.Gson;
import main.java.com.sharedfinances.broker.RabbitMQ;
import main.java.com.sharedfinances.logic.Event;
import main.java.com.sharedfinances.logic.Management;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.logging.Logger;

public class Main {

    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());
    private static final long INTERVAL_MS = 5000;
    private static final Management management = new Management();

    public static void main(String[] args) {

    }

    public static void addEvent() {
        RabbitMQ rabbitMQ = new RabbitMQ();
        rabbitMQ.subscribeToAMQP(RabbitMQ.ADD_EVENT_QUEUE);
        List<JSONObject> messages;
        while (true) {
            messages = rabbitMQ.getMessages();
            if (!messages.isEmpty()) {
                for (JSONObject message : messages) {
                    String code = (String) message.get("code");
                    String name = (String) message.get("name");
                    management.addEvent(new Event(code, name));
                }
                try {
                    addEventToAPI(management.getEvents());
                } catch (IOException | ParseException e) {
                    LOGGER.info("Failed to POST to RestAPI!");
                }
            }
            try {
                Thread.sleep(INTERVAL_MS);
            } catch (InterruptedException interruptedException) {
                LOGGER.info("Add Event Thread interrupt!");
                Thread.currentThread().interrupt();
            }
        }
    }

    public static void addEventToAPI(List<Event> events) throws IOException, ParseException {
        URL url = new URL("http://raspijk.ddns.net:8095/api/events/list");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/json; utf-8");
        con.setRequestProperty("Accept", "application/json; utf-8");
        con.setDoOutput(true);
        Gson gson = new Gson();
        String jsonString = gson.toJson(events);
        try (OutputStream os = con.getOutputStream()) {
            byte[] output = jsonString.getBytes("utf-8");
            os.write(output, 0, output.length);
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        try (BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), "utf-8"))) {
            StringBuilder response = new StringBuilder();
            String responseLine = null;
            while((responseLine = br.readLine())!=null)
                response.append(responseLine.trim());
            LOGGER.info(response.toString());
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
