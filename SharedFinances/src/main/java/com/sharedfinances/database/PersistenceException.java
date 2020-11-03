package main.java.com.sharedfinances.database;

public class PersistenceException extends Exception {
    private static final long serialVersionUID = -819711352539534949L;

    public PersistenceException() {
        super("Error in Persistence!");
    }
}
