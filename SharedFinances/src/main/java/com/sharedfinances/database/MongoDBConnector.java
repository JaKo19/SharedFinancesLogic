package main.java.com.sharedfinances.database;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import main.java.com.sharedfinances.logic.Person;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

public class MongoDBConnector {

    private final String collection;
    private final String database;
    private final int port;
    private final String host;

    public MongoDBConnector(String host, int port, String database, String collection) {
        this.collection = collection;
        this.database = database;
        this.host = host;
        this.port = port;
    }

    public void deleteAll(MongoCollection<Document> collection) {
        try {
            BasicDBObject document = new BasicDBObject();
            collection.deleteMany(document);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addDocuments(List<Person> list) {
        try (MongoClient mongoClient = new MongoClient(host, port)) {
            MongoDatabase mongoDatabase = mongoClient.getDatabase(database);
            MongoCollection<Document> mongoCollection = mongoDatabase.getCollection(collection);
            if (mongoCollection.count() != 0) {
                deleteAll(mongoCollection);
            }
            mongoCollection.insertMany(newDocuments(list));
        }
    }

    public List<Document> newDocuments(List<Person> list) {
        List<Document> newdocs = new ArrayList<>();
        for (Person p : list) {
            Document doc = new Document().append("name", p.getName()).append("amount", p.getAmount());
            newdocs.add(doc);
        }
        return newdocs;
    }
}