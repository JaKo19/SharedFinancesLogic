package main.java.com.sharedfinances.database;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import main.java.com.sharedfinances.logic.Person;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MongoDBConnector {

    private static final Logger LOGGER = Logger.getLogger(MongoDBConnector.class.getName());
    private static MongoDBConnector mongoDBConnector;
    private final String collection;
    private final String database;
    private final int port;
    private final String host;

    private MongoDBConnector(String host, int port, String database, String collection) {
        this.collection = collection;
        this.database = database;
        this.host = host;
        this.port = port;
    }

    public static void start(String host, int port, String database, String collection, List<Person> list) {
        Thread thread = new Thread(() -> {
            if (mongoDBConnector == null) {
                mongoDBConnector = new MongoDBConnector(host, port, database, collection);
            }
            while (true) {
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    LOGGER.info("Interupt!");
                }
                mongoDBConnector.addDocuments(list);
            }
        });
        thread.start();
    }

    public void addDocuments(List<Person> list) {
        try (MongoClient mongoClient = new MongoClient(host, port)) {
            MongoDatabase mongoDatabase = mongoClient.getDatabase(database);
            MongoCollection<Document> mongoCollection = mongoDatabase.getCollection(collection);
            if (mongoCollection.count() != 0) {
                deleteAll(mongoCollection);
            }
            mongoCollection.insertMany(newDocuments(list));
        } catch (Exception e) {
            LOGGER.info("Failed to connect!");
        }
    }

    public void deleteAll(MongoCollection<Document> collection) {
        BasicDBObject document = new BasicDBObject();
        collection.deleteMany(document);
    }

    public List<Document> newDocuments(List<Person> list) {
        List<Document> newDocuments = new ArrayList<>();
        for (Person p : list) {
            Document doc = new Document().append("name", p.getName()).append("amount", p.getAmount());
            newDocuments.add(doc);
        }
        return newDocuments;
    }


}