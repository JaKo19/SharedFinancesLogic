package main.java.com.sharedfinances.database;

import main.java.com.sharedfinances.logic.Person;

import java.io.*;
import java.util.List;

public class ListSerialize {

    private File dir = new File(".\\src\\main\\resources");
    private File f = new File(".\\src\\main\\resources\\List.ser");

    public ListSerialize() {
        createDir();
    }

    public void createDir() {
        if (!dir.exists())
            dir.mkdir();
    }

    @SuppressWarnings("unchecked")
    public List<Person> load() throws PersistenceException {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f))) {
            return (List<Person>) ois.readObject();
        } catch (Exception e) {
            throw new PersistenceException();
        }
    }

    public void save(List<Person> list) throws PersistenceException {
        try (FileOutputStream fos = new FileOutputStream(f); ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(list);
        } catch (Exception e) {
            throw new PersistenceException();
        }
    }
}
