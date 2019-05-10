package model.dbms;

import main.exception.AlreadyExists;
import main.exception.DoesNotExist;
import main.exception.InvalidValue;
import main.model.DatabaseManagementSystem;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class GetDatabase {

    private static final String DATABASE_NAME = "GET_DATABASE_TEST";
    public static final String INVALID_DATABASE_NAME = "INVALID_DATABASE_NAME";

    @BeforeAll
    public static void createDatabase(){
        DatabaseManagementSystem dbms = DatabaseManagementSystem.getInstance();
        try {
            dbms.createDatabase(DATABASE_NAME);
        } catch (AlreadyExists | InvalidValue alreadyExists) {
            alreadyExists.printStackTrace();
        }
    }

    @Test
    public void getDatabase(){
        DatabaseManagementSystem dbms = DatabaseManagementSystem.getInstance();
        try {
            dbms.getDatabase(DATABASE_NAME);
        } catch (DoesNotExist doesNotExist) {
            fail();
        }
        assertTrue(dbms.exists(DATABASE_NAME));
    }

    @Test
    public void getInvalidDatabase(){
        DatabaseManagementSystem dbms = DatabaseManagementSystem.getInstance();
        assertThrows(DoesNotExist.class,()->{
            dbms.getDatabase(INVALID_DATABASE_NAME);
        });
        assertFalse(dbms.exists(INVALID_DATABASE_NAME));
    }

    @Test
    public void getNullDatabase(){
        DatabaseManagementSystem dbms = DatabaseManagementSystem.getInstance();
        String nullCheck = null;
        assertThrows(DoesNotExist.class,()->{
            dbms.getDatabase(nullCheck);
        });
    }

}
