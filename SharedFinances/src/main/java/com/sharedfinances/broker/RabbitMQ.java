package main.java.com.sharedfinances.broker;

import com.rabbitmq.client.*;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

public class RabbitMQ {

    private static final Logger LOGGER = Logger.getLogger(RabbitMQ.class.getName());
    private static final String HOST = "raspijk.ddns.net";
    private static final int PORT = 5672;
    private static final String LISTDATA_QUEUE = "listdata";
    private final List<JSONObject> jsonObjects;
    private final String queue;

    public RabbitMQ(String queue) {
        this.queue = queue;
        jsonObjects = new LinkedList<>();
    }

    public void subscribeToAMQP() {
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

    public void publishToAMQP(String json) {
        Thread publishThread = new Thread(() -> {
            ConnectionFactory connectionFactory = new ConnectionFactory();
            connectionFactory.setHost(HOST);
            connectionFactory.setPort(PORT);
            connectionFactory.setPassword("rabbit");
            connectionFactory.setUsername("rabbit");
            try (Connection connection = connectionFactory.newConnection()) {
                Channel channel = connection.createChannel();
                channel.queueDeclare(LISTDATA_QUEUE, false, false, false, null);
                channel.basicPublish("", LISTDATA_QUEUE, null, json.getBytes());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        publishThread.start();
    }

}
