package model.dbms;

import main.exception.AlreadyExists;
import main.exception.InvalidValue;
import main.model.DatabaseManagementSystem;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
public class Exists {
    private static final String DATABASE_NAME = "EXISTS_DATABASE_TEST";
    private static DatabaseManagementSystem dbms = DatabaseManagementSystem.getInstance();
    @BeforeAll
    public static void createDatabase(){
        assertFalse(dbms.exists(DATABASE_NAME));
        try {
            dbms.createDatabase(DATABASE_NAME);
        } catch (AlreadyExists | InvalidValue alreadyExists) {
            alreadyExists.printStackTrace();
        }
    }
    @Test
    public void exists(){
        assertTrue(dbms.exists(DATABASE_NAME));
    }
}
