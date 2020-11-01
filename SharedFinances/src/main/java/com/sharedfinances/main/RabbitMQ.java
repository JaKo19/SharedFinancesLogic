package main.java.com.sharedfinances.main;

import com.rabbitmq.client.*;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

public class RabbitMQ {

    private static final Logger LOGGER = Logger.getLogger(RabbitMQ.class.getName());
    private static final List<JSONObject> jsonObjects = new LinkedList<>();
    private static final String HOST = "raspijk.ddns.net";
    private static final int PORT = 5672;

    public RabbitMQ(String queue) {
        subscribeToAMQP(queue);
    }

    public static void subscribeToAMQP(String queue) {
        Thread subscribeThread = new Thread(() -> {
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
                    public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
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
        });
        subscribeThread.start();
    }

    public List<JSONObject> getMessages() {
        synchronized (jsonObjects) {
            return jsonObjects;
        }
    }

}
