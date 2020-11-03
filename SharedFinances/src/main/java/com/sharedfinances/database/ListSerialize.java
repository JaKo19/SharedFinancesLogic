package main.java.com.sharedfinances.database;

import main.java.com.sharedfinances.logic.Person;

import java.io.*;
import java.util.List;

public class ListSerialize {

    public static byte[] save(List<Person> list) throws PersistenceException {
        try (ByteArrayOutputStream bais = new ByteArrayOutputStream(); ObjectOutputStream oos = new ObjectOutputStream(bais)) {
            oos.writeObject(list);
            return bais.toByteArray();
        } catch (Exception e) {
            throw new PersistenceException();
        }
    }
}
