package model.table;

import main.exception.*;
import main.model.Column;
import main.model.DatabaseManagementSystem;
import main.model.Field;
import main.model.Table;
import main.util.DataBuilder;
import org.junit.jupiter.api.Test;

import javax.xml.crypto.Data;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static main.util.DataBuilder.*;
import static org.junit.jupiter.api.Assertions.*;

public class TableTest extends AbstractTableOperationsTest {

    @Test
    public void changeName() {
        try {
            table.setName(NEW_TABLE_NAME);
            assertTrue(table.getName().equals(NEW_TABLE_NAME));
            table.setName(TABLE);
        } catch (InvalidValue invalidValue) {
            fail(invalidValue);
        }
    }

    @Test
    public void getColumnByColumnNames() {
        try {
            List<Column> columns = table.getColumnsByColumnNames(table.getColumnNames());
            for (Column column : columns) {
                assertTrue(table.getColumnNames().contains(column.getName()));

            }

        } catch (DoesNotExist doesNotExist) {
            fail(doesNotExist);
        }
    }

    @Test
    public void getFirstRow() {
        try {
            assertTrue(table.getNumberOfRows() > 0);
            Map<Column, Field> firstRow = table.getRow(0);
            Map<String, Field> row = new HashMap<>();
            row.put(NAME, new Field("dan"));
            row.put(AGE, new Field(10));
            row.put(HEIGHT, new Field(187));
            row.put(SCHOOL, new Field("INFORMATICA"));

            for (String column : row.keySet()) {
                try {
                    Column col = table.getColumn(column);
                    assertTrue(firstRow.get(col).equals(row.get(column)));
                } catch (DoesNotExist doesNotExist) {
                    fail(doesNotExist);
                }
            }


        } catch (InvalidValue invalidValue) {
            invalidValue.printStackTrace();
        }
    }

    @Test
    public void insert() {
        try {
            Map<String, Field> row = new HashMap<>();
            row.put(NAME, new Field("dan"));
            row.put(AGE, new Field(10));
            row.put(HEIGHT, new Field(187));
            row.put(SCHOOL, new Field("INFORMATICA"));
            Table table = DataBuilder.createTable(NEW_TABLE_NAME);
            DatabaseManagementSystem.getInstance().getDatabase(DATABASE).createTable(table);
            table.insert(row);
            assertTrue(table.getNumberOfRows() == 1);
            Map<Column, Field> firstRow = table.getRow(0);
            for (String column : row.keySet()) {
                try {
                    Column col = table.getColumn(column);
                    assertTrue(firstRow.get(col).equals(row.get(column)));
                } catch (DoesNotExist doesNotExist) {
                    fail(doesNotExist);
                }
            }
        } catch (AlreadyExists | InvalidValue | DoesNotExist | InexistentColumn | TypeMismatchException e) {
            fail(e);
        }
    }

    @Test
    public void insertWithInexistentColumn() {
        try {
            Map<String, Field> row = new HashMap<>();
            row.put(NAME, new Field("dan"));
            row.put(AGE, new Field(10));
            row.put(HEIGHT, new Field(187));
            row.put(SCHOOL, new Field("INFORMATICA"));
            row.put("INEXISTENT_COLUMN", new Field("INFORMATICA"));
            Table table = DataBuilder.createTable(NEW_TABLE_NAME);
            DatabaseManagementSystem.getInstance().getDatabase(DATABASE).createTable(table);

            assertThrows(InexistentColumn.class,()->{
                table.insert(row);
            });
        } catch (AlreadyExists | InvalidValue | DoesNotExist e) {
            fail(e);
        }
    }

    @Test
    public void insertDefaultData() {
        try {
            Map<String, Field> row = new HashMap<>();
            row.put(NAME, new Field("dan"));
//            row.put(SCHOOL, new Field("INFORMATICA"));
            Table table = DataBuilder.createTable(NEW_TABLE_NAME);
            DatabaseManagementSystem.getInstance().getDatabase(DATABASE).createTable(table);
            table.insert(row);
            assertTrue(table.getNumberOfRows() == 1);
            Map<Column, Field> firstRow = table.getRow(0);
            for (String column : row.keySet()) {
                try {
                    Column col = table.getColumn(column);
                    assertTrue(firstRow.get(col).equals(row.get(column)));
                } catch (DoesNotExist doesNotExist) {
                    fail(doesNotExist);
                }
            }

            assertTrue(firstRow.get(AGE).getIntValue() == 0);
            assertTrue(firstRow.get(HEIGHT).getIntValue() == 0);
            assertTrue(firstRow.get(SCHOOL).getStringValue().equals(""));
        } catch (AlreadyExists | InvalidValue | DoesNotExist | InexistentColumn | TypeMismatchException | FieldValueNotSet e) {
            fail(e);
        }
    }

    @Test
    public void toStringTest() {
        assertEquals(table.toString(), Table.toString(table.getData()));
    }


}
