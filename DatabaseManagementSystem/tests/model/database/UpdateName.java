package model.database;

import main.exception.DoesNotExist;
import main.exception.InvalidValue;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;
public class UpdateName extends AbstractDatabaseTest {
    @Test
    public void changeTableName(){
        try {
            database.getTable(TABLE_NAME).setName(INVALID_TABLE_NAME);
        } catch (DoesNotExist | InvalidValue doesNotExist) {
            fail(doesNotExist);
        }
    }
    @Test
    public void InvalidChangeTableName(){
        assertThrows(InvalidValue.class,()->{
            database.getTable(TABLE_NAME).setName(COMMA_TABLE_NAME);
        });
        assertThrows(InvalidValue.class,()->{
            database.getTable(TABLE_NAME).setName(QUOTES_TABLE_NAME);
        });
    }

}
