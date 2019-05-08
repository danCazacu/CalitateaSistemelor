package model.database;

import main.exception.AlreadyExists;
import main.exception.DoesNotExist;
import main.exception.InvalidValue;
import main.model.Database;
import main.model.DatabaseManagementSystem;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;

import static org.junit.jupiter.api.Assertions.fail;

public class AbstractDatabaseCreateTest {

    public static final String TABLE_NAME = "TABLE_TEST_JUNIT";
    public static final String INVALID_TABLE_NAME = "INVALID_TABLE_TEST_JUNIT";
    public static final String COMMA_TABLE_NAME = "TA,BLE_TEST_JUNIT";
    public static final String QUOTES_TABLE_NAME = "TA\"BLE_TEST_JUNIT";
    public static final String DATABASE_NAME = "DATABASE_TEST_JUNIT";
    public static final DatabaseManagementSystem dbms = DatabaseManagementSystem.getInstance();
    public static Database database;

    @BeforeAll
    public static void prepare() {
        try {
            database = dbms.createDatabase(DATABASE_NAME);
        } catch (AlreadyExists | InvalidValue alreadyExists) {
            fail(alreadyExists);
        }
    }

    @AfterAll
    public static void cleanUp() {
        try {
            dbms.deleteDatabase(DATABASE_NAME);
        } catch (DoesNotExist doesNotExist) {
            fail(doesNotExist);
        }
    }

    @AfterEach
    public void cleanUpAfterEach(){
        try {
            database.deleteTable(TABLE_NAME);
        } catch (DoesNotExist doesNotExist) {

        }
    }




}
