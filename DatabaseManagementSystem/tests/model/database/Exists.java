package model.database;

import main.exception.AlreadyExists;
import main.exception.DoesNotExist;
import main.exception.InvalidValue;
import main.util.DataBuilder;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
public class Exists extends AbstractDatabaseTest{

    @Test
    public void exists(){
        try {
            assertFalse(database.exists(INVALID_TABLE_NAME));
            database.createTable(DataBuilder.createTable(INVALID_TABLE_NAME));
            assertTrue(database.exists(INVALID_TABLE_NAME));
            database.deleteTable(INVALID_TABLE_NAME);
        } catch (AlreadyExists | InvalidValue | DoesNotExist e) {
            fail(e);
        }

    }
}
