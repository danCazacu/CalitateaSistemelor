package model.table;

import main.exception.ColumnAlreadyExists;
import main.exception.DoesNotExist;
import main.exception.InvalidValue;
import main.model.Column;
import main.util.DataBuilder;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ColumnTest extends AbstractTableOperationsTest {

    private static final String NEW_COLUMN_NAME = "NEW_COLUMN_NAME";
    private static final String INVALID_NEW_COLUMN_NAME = "INVALID_NEW_COLUMN_NAME";

    @Test
    public void deleteColumn() {
        try {
            table.deleteColumn(table.getColumn(DataBuilder.COLUMNS[0]));
        } catch (DoesNotExist doesNotExist) {
            fail(doesNotExist);
        }

        assertThrows(DoesNotExist.class,()->{
            table.getColumn(DataBuilder.COLUMNS[0]);
        });
    }

    @Test
    public void createColumn(){
        try {
            Column column = new Column(NEW_COLUMN_NAME, Column.Type.INT);
            column.setName(NEW_COLUMN_NAME);
            table.insertColumn(column);
            assertEquals(table.getColumn(NEW_COLUMN_NAME),column);
        } catch (InvalidValue | ColumnAlreadyExists | DoesNotExist e) {
            fail(e);
        }
    }

    @Test
    public void getColumn(){
        assertThrows(DoesNotExist.class,()->{
            table.getColumn(INVALID_NEW_COLUMN_NAME);
        });
        assertThrows(DoesNotExist.class,()->{
            table.getColumn(null);
        });

        try {
            Column column = new Column(NEW_COLUMN_NAME, Column.Type.INT);
            table.insertColumn(column);
            assertEquals(table.getColumn(NEW_COLUMN_NAME),column);
            assertTrue(table.getColumnNames().contains(NEW_COLUMN_NAME));
        } catch (InvalidValue | ColumnAlreadyExists | DoesNotExist e) {
            fail(e);
        }

    }

    @Test
    public void getColumnNames(){
        try {
            Column column = new Column(NEW_COLUMN_NAME, Column.Type.INT);
            table.insertColumn(column);
            assertTrue(table.getColumnNames().contains(NEW_COLUMN_NAME));
            assertFalse(table.getColumnNames().contains(INVALID_NEW_COLUMN_NAME));
        } catch (InvalidValue | ColumnAlreadyExists e) {
            fail(e);
        }

    }

    @Test
    public void equals(){
        try {
            Column first = new Column(NEW_COLUMN_NAME, Column.Type.INT);
            Column second = new Column(NEW_COLUMN_NAME, Column.Type.INT);
            Column third = new Column(INVALID_NEW_COLUMN_NAME, Column.Type.INT);
            assertTrue(first.equals(second));
            assertFalse(first.equals(third));
            assertFalse(first.equals(null));

        } catch (InvalidValue invalidValue) {
           fail(invalidValue);
        }
    }

    @Test
    public void hashCodeTest(){
        try {
            Column first = new Column(NEW_COLUMN_NAME, Column.Type.INT);
            Column second = new Column(NEW_COLUMN_NAME, Column.Type.INT);
            assertTrue(first.hashCode() != second.hashCode());

        } catch (InvalidValue invalidValue) {
            fail(invalidValue);
        }
    }
}
