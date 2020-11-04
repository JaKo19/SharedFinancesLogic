package main.java.com.sharedfinances.database;

import com.google.gson.Gson;
import main.java.com.sharedfinances.logic.Person;

import java.io.*;
import java.util.List;

public class ListSerialize {

    public static String save(List<Person> list) {
        return new Gson().toJson(list);
    }
}
