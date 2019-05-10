package model.database;

import main.exception.AlreadyExists;
import main.exception.InvalidValue;
import main.model.Table;
import main.util.DataBuilder;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class CreateTable extends AbstractDatabaseCreateTest {


    @Test
    public void createNullTable(){
        Table table = null;
        assertThrows(InvalidValue.class,()->{
            database.createTable(null);
        });
    }

    @Test
    public void createTableFromNameNoColumns() {
        try {
            assertFalse(database.exists(TABLE_NAME));
            database.createTable(TABLE_NAME, new ArrayList<>()); //empty list of columns
            assertTrue(database.exists(TABLE_NAME));
        } catch (InvalidValue | AlreadyExists e) {
            fail(e);
        }
    }

    @Test
    public void createTableFromObjectNoColumns() {
        try {

            Table table = new Table(TABLE_NAME, new ArrayList<>());
            assertFalse(database.exists(TABLE_NAME));
            database.createTable(table); //empty list of columns
            assertTrue(database.exists(TABLE_NAME));
        } catch (InvalidValue | AlreadyExists e) {
            fail(e);
        }
    }

    @Test
    public void createTableWithColumnsFromObject() {
        try {
            Table table = DataBuilder.createTable(TABLE_NAME);
            assertFalse(database.exists(TABLE_NAME));
            database.createTable(table);
            assertTrue(database.exists(TABLE_NAME));
        } catch (InvalidValue | AlreadyExists invalidValue) {
            fail(invalidValue);
        }
    }

    @Test
    public void createTableWithColumns() {
        try {
            Table table = DataBuilder.createTable(TABLE_NAME);
            assertFalse(database.exists(TABLE_NAME));
            database.createTable(table.getName(), DataBuilder.buildColumns());
            assertTrue(database.exists(TABLE_NAME));
        } catch (InvalidValue | AlreadyExists invalidValue) {
            fail(invalidValue);
        }
    }

    @Test
    public void createTableWithInvalidTableName() {
        assertThrows(InvalidValue.class, () -> {
            new Table(COMMA_TABLE_NAME, new ArrayList<>());
        });

        assertThrows(InvalidValue.class, () -> {
            new Table(QUOTES_TABLE_NAME, new ArrayList<>());
        });
    }


}
