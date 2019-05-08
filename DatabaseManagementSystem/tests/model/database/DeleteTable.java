package model.database;

import main.exception.DoesNotExist;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class DeleteTable extends AbstractDatabaseTest {

    @Test
    public void deleteTable(){
        try {
            assertTrue(database.exists(TABLE_NAME));
            database.deleteTable(TABLE_NAME);
            assertFalse(database.exists(TABLE_NAME));
        } catch (DoesNotExist doesNotExist) {
            fail(doesNotExist);
        }
    }
    @Test
    public void deleteInvalidTable(){
            assertFalse(database.exists(INVALID_TABLE_NAME));
            assertThrows(DoesNotExist.class,()->{
                database.deleteTable(INVALID_TABLE_NAME);
            });
            assertFalse(database.exists(INVALID_TABLE_NAME));
    }
    @Test
    public void deleteNullTable(){
        assertThrows(DoesNotExist.class,()->{
            String tableName = null;
           database.deleteTable(tableName);
        });
    }
}
