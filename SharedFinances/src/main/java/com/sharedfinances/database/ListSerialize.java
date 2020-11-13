package main.java.com.sharedfinances.database;

import com.google.gson.Gson;
import main.java.com.sharedfinances.logic.Person;

import java.util.List;

public class ListSerialize {

    public static String save(List<Person> list) {
        Gson gson = new Gson();
        return gson.toJson(list);
    }
}
