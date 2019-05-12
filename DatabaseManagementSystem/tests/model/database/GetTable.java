package model.database;

import main.exception.DoesNotExist;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class GetTable extends AbstractDatabaseTest {

    @Test
    public void getNullTable() {
        assertThrows(DoesNotExist.class, () -> {
            String tableName = null;
            database.getTable(tableName);
        });
    }

    @Test
    public void getTable() {
        try {
            database.getTable(TABLE_NAME);
        } catch (DoesNotExist doesNotExist) {
            fail(doesNotExist);
        }
    }

    @Test
    public void getInvalidTable() {
        assertThrows(DoesNotExist.class, () -> {
            database.getTable(INVALID_TABLE_NAME);
        });
    }

}
