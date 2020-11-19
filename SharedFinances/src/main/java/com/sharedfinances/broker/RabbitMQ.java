package main.java.com.sharedfinances.broker;

import com.rabbitmq.client.*;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

public class RabbitMQ {

    public static final String ADD_AMOUNT_QUEUE = "addamount";
    public static final String EXTRA_AMOUNT_QUEUE = "extraamount";
    public static final String PAY_AMOUNT_QUEUE = "pay";
    public static final String ADD_EVENT_QUEUE = "addevent";
    public static final String JOIN_EVENT_QUEUE = "joinevent";

    private static final Logger LOGGER = Logger.getLogger(RabbitMQ.class.getName());
    private static final String HOST = "raspijk.ddns.net";
    private static final int PORT = 5672;
    private final List<JSONObject> jsonObjects;

    public RabbitMQ() {
        jsonObjects = new LinkedList<>();
    }

    public void subscribeToAMQP(String queue) {
        LOGGER.info("Connecting to " + HOST + ":" + PORT + "...");
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost(HOST);
        connectionFactory.setPort(PORT);
        connectionFactory.setPassword("rabbit");
        connectionFactory.setUsername("rabbit");
        try {
            Connection connection = connectionFactory.newConnection();
            Channel channel = connection.createChannel();
            channel.queueDeclare(queue, false, false, false, null);
            channel.basicConsume(queue, true, new DefaultConsumer(channel) {
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) {
                    JSONParser jsonParser = new JSONParser();
                    String message = new String(body, StandardCharsets.UTF_8);
                    try {
                        synchronized (jsonObjects) {
                            jsonObjects.add((JSONObject) jsonParser.parse(message));
                        }
                    } catch (ParseException e) {
                        LOGGER.info("Parsing failed!");
                    }
                }
            });
        } catch (Exception e) {
            LOGGER.info("Failed to connect!");
        }
    }

    public List<JSONObject> getMessages() {
        synchronized (jsonObjects) {
            return jsonObjects;
        }
    }
}
