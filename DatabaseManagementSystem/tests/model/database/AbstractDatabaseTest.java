package model.database;

import main.exception.AlreadyExists;
import main.exception.InvalidValue;
import main.util.DataBuilder;
import org.junit.jupiter.api.BeforeEach;

import static org.junit.jupiter.api.Assertions.*;

public class AbstractDatabaseTest extends AbstractDatabaseCreateTest {
    @BeforeEach
    public void createTable(){
        try {
            database.createTable(DataBuilder.createTable(TABLE_NAME));
        } catch (AlreadyExists | InvalidValue alreadyExists) {
            fail(alreadyExists);
        }
    }
}
