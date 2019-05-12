package model.dbms;
import main.exception.AlreadyExists;
import main.exception.DoesNotExist;
import main.exception.InvalidValue;
import main.model.Database;
import main.model.DatabaseManagementSystem;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static model.database.AbstractDatabaseCreateTest.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;
public class UpdateDatabaseName {
    private static final String DATABASE_NAME = "DATABASE_TEST_NAME";
    private static final String NEW_DATABASE_NAME = "NEW_DATABASE_NAME";
    private static final String COMMA_DATABASE_NAME = "NEW_DAT,ABASE_NAME";
    private static final String QUOTES_DATABASE_NAME = "NEW_DA\"TABASE_NAME";
    private static Database database;

    @BeforeAll
    public static void prepare(){
        try {
            database  = DatabaseManagementSystem.getInstance().createDatabase(DATABASE_NAME);
        } catch (AlreadyExists | InvalidValue alreadyExists) {
            fail(alreadyExists);
        }
    }
    @Test
    public void changeDatabaseName(){
        try {
            database.setName(NEW_DATABASE_NAME);
        } catch (InvalidValue invalidValue) {
            fail(invalidValue);
        }

    }
    @Test
    public void invalidChangeDatabaseName(){
        assertThrows(InvalidValue.class,()->{
            database.setName(COMMA_DATABASE_NAME);
        });
        assertThrows(InvalidValue.class,()->{
            database.setName(QUOTES_DATABASE_NAME);
        });
    }
}
