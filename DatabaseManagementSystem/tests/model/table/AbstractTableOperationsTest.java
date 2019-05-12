package model.table;

import main.exception.DoesNotExist;
import main.model.Database;
import main.model.DatabaseManagementSystem;
import main.model.Table;
import main.util.DataBuilder;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

import static org.junit.jupiter.api.Assertions.fail;

public abstract class AbstractTableOperationsTest {

    protected static Table table;
    protected static String NEW_TABLE_NAME = "NEW_TABLE_NAME";
    @BeforeEach
    public void prepare(){
        DataBuilder.buildTestData();
        try {
            table = DatabaseManagementSystem.getInstance().getDatabase(DataBuilder.DATABASE).getTable(DataBuilder.TABLE);
        } catch (DoesNotExist doesNotExist) {
            fail(doesNotExist);
        }
    }

    @AfterEach
    public void clean(){
        try {
            DatabaseManagementSystem.getInstance().deleteDatabase(DataBuilder.DATABASE);
        } catch (DoesNotExist ignored) {

        }
    }

}
