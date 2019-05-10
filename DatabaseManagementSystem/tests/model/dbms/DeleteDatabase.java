package model.dbms;

import main.exception.AlreadyExists;
import main.exception.DoesNotExist;
import main.exception.InvalidValue;
import main.model.DatabaseManagementSystem;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
public class DeleteDatabase {
    private static final String DATABASE_NAME = "GET_DATABASE_TEST";
    private static final String INVALID_DATABASE_NAME = "INVALID_DATABASE_NAME";
    private static DatabaseManagementSystem dbms = DatabaseManagementSystem.getInstance();
    @BeforeEach
    public  void build(){
        try {
            dbms.createDatabase(DATABASE_NAME);
        } catch (AlreadyExists | InvalidValue alreadyExists) {
            fail(alreadyExists);
        }
    }
    @AfterEach
    public  void clean(){
        try {
            dbms.deleteDatabase(DATABASE_NAME);
        } catch (DoesNotExist doesNotExist) {
//            doesNotExist.printStackTrace();
        }
    }
    @Test
    public void deleteDatabase(){
        assertTrue(dbms.exists(DATABASE_NAME));
        try {
            dbms.deleteDatabase(DATABASE_NAME);
        } catch (DoesNotExist doesNotExist) {
            fail(doesNotExist);
        }
    }

    @Test
    public void deleteDatabaseObject(){
        assertTrue(dbms.exists(DATABASE_NAME));
        try {
            dbms.deleteDatabase(dbms.getDatabase(DATABASE_NAME));
        } catch (DoesNotExist doesNotExist) {
            fail(doesNotExist);
        }
    }

    @Test
    public void deleteInvalidDatabase(){
        assertFalse(dbms.exists(INVALID_DATABASE_NAME));
        assertThrows(DoesNotExist.class,()->{
           dbms.deleteDatabase(INVALID_DATABASE_NAME);
        });
    }

}
