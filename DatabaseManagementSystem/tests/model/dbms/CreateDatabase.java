package model.dbms;

import main.exception.AlreadyExists;
import main.exception.InvalidValue;
import main.model.Database;
import main.model.DatabaseManagementSystem;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CreateDatabase {

    @Test
    public void nullSafe() {
        DatabaseManagementSystem dbms = DatabaseManagementSystem.getInstance();
        String nullTest = null;
        assertThrows(InvalidValue.class, () -> {
            dbms.createDatabase(nullTest);
        });
    }

    @Test
    public void createWithInvalidCharacters() {
        DatabaseManagementSystem dbms = DatabaseManagementSystem.getInstance();
        String commaCase = "bla,bla";
        assertThrows(InvalidValue.class, () -> {
            dbms.createDatabase(commaCase);
        });

        String quotesCase = "bla\"bla";
        assertThrows(InvalidValue.class, () -> {
            dbms.createDatabase(quotesCase);
        });

        assertFalse(dbms.exists(commaCase));
        assertFalse(dbms.exists(quotesCase));
    }

    @Test
    public void createDatabase() {
        DatabaseManagementSystem dbms = DatabaseManagementSystem.getInstance();
        String database = "createDatabaseTestCase";
        assertFalse(dbms.exists(database));
        try {
            dbms.createDatabase(database);
        } catch (AlreadyExists | InvalidValue e) {
            fail(e);
        }

        assertTrue(dbms.exists(database));
    }

    @Test
    public void createAlreadyExistDatabase() {
        DatabaseManagementSystem dbms = DatabaseManagementSystem.getInstance();
        String database = "createAlreadyExistsTestCase";
        assertFalse(dbms.exists(database));
        try {
            dbms.createDatabase(database);
        } catch (AlreadyExists | InvalidValue e) {
            fail(e);
        }
        assertTrue(dbms.exists(database));
        assertThrows(AlreadyExists.class, () -> {
            dbms.createDatabase(database);
        });
    }

    @Test
    public void createAlreadyExistDatabaseFromObject() {
        DatabaseManagementSystem dbms = DatabaseManagementSystem.getInstance();
        String database = "createAlreadyExistsWithObjectTestCase";
        assertFalse(dbms.exists(database));
        try {
            Database database1 = new Database(database);
            dbms.createDatabase(database1);
            assertTrue(dbms.exists(database1.getName()));
            assertThrows(AlreadyExists.class, () -> {
                dbms.createDatabase(database1);
            });
        } catch (AlreadyExists | InvalidValue e) {
            fail(e);
        }

    }


}
