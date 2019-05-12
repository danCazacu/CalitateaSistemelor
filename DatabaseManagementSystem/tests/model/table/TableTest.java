package model.table;

import main.exception.*;
import main.model.*;
import main.util.DataBuilder;
import org.junit.jupiter.api.Test;

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

            assertThrows(InexistentColumn.class, () -> {
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

            assertTrue(firstRow.get(table.getColumn(AGE)).getIntValue() == 0);
            assertTrue(firstRow.get(table.getColumn(HEIGHT)).getIntValue() == 0);
            assertTrue(firstRow.get(table.getColumn(SCHOOL)).getStringValue().equals(""));
        } catch (AlreadyExists | InvalidValue | DoesNotExist | InexistentColumn | TypeMismatchException | FieldValueNotSet e) {
            fail(e);
        }
    }

    @Test
    public void toStringTest() {
        assertTrue(table.toString().contains(Table.toString(table.getData())));
    }

    @Test
    public void selectTrimColumns() {
        ArrayList<Column> columns = new ArrayList<Column>(table.getData().keySet());
        assertTrue(columns.size() > 1);
        columns.remove(1);
        Map<Column, List<Field>> data = table.select(table.getData(), columns);
        assertTrue(data.keySet().size() == columns.size());
        for (Column column : data.keySet()) {
            assertTrue(columns.contains(column));
        }
    }

    @Test
    public void whereAgeLessThan12() {

        try {

            Map<Column, List<Field>> data = table.where(AGE, FieldComparator.Sign.LESS_THAN, new Field(12));
            System.out.println(Table.toString(data));
            List<Field> ageColumn = data.get(table.getColumn(AGE));
            for (Field field : ageColumn) {
                assertTrue(field.getIntValue() < 12);
            }

        } catch (TypeMismatchException e) {
            fail(e);
        } catch (DoesNotExist doesNotExist) {
            fail(doesNotExist);
        } catch (FieldValueNotSet fieldValueNotSet) {
            fail(fieldValueNotSet);
        }

    }

    @Test
    public void whereNameIsNotDan() {

        try {

            Map<Column, List<Field>> data = table.where(NAME, FieldComparator.Sign.DIFFERENT, new Field("dan"));
            System.out.println(Table.toString(data));
            List<Field> nameColumn = data.get(table.getColumn(NAME));
            for (Field field : nameColumn) {
                assertFalse(field.getStringValue().equals("dan"));
            }

        } catch (TypeMismatchException e) {
            fail(e);
        } catch (DoesNotExist doesNotExist) {
            fail(doesNotExist);
        } catch (FieldValueNotSet fieldValueNotSet) {
            fail(fieldValueNotSet);
        } catch (InvalidValue invalidValue) {
            fail(invalidValue);
        }

    }

    @Test
    public void whereTryingToMatchStringToInt() {
        assertThrows(TypeMismatchException.class, () -> {
            Map<Column, List<Field>> data = table.where(NAME, FieldComparator.Sign.DIFFERENT, new Field(12));
        });
    }

    @Test
    public void updateNameWhereNameIsNotDan() {
        try {
            table.updateWhere(NAME, FieldComparator.Sign.DIFFERENT, new Field("dan"), new Field("others"));
            System.out.println(table.toString());
            List<Field> nameColumn = table.getData().get(table.getColumn(NAME));
            for (Field field : nameColumn) {
                assertTrue(field.getStringValue().equals("dan") ? true : field.getStringValue().equals("others") ? true : false);
            }
        } catch (TypeMismatchException | InvalidValue | DoesNotExist | FieldValueNotSet e) {
            fail(e);
        }
    }

    @Test
    public void updateNameSetNameTo10WhereNameIsNotDan() {
        assertThrows(TypeMismatchException.class, () -> {
            table.updateWhere(NAME, FieldComparator.Sign.DIFFERENT, new Field("dan"), new Field(10));
        });
    }

    @Test
    public void deleteWhereAgeLessThan12() {
        try {
            table.deleteWhere(AGE, FieldComparator.Sign.LESS_THAN, new Field(12));
            System.out.println(table.toString());
            List<Field> ageColumn = table.getData().get(table.getColumn(AGE));
            for (Field field : ageColumn) {
                assertTrue(field.getIntValue() >= 12);
            }
        } catch (TypeMismatchException | FieldValueNotSet | DoesNotExist e) {
            fail(e);
        }
    }

    @Test
    public void deleteFirstRow() {
        int rowsBefore = table.getNumberOfRows();
        assertTrue(rowsBefore > 0);
        table.deleteRow(0);
        assertTrue(table.getNumberOfRows() == rowsBefore - 1);

    }

    @Test
    public void deleteRowsByData() {
        try {
            Map<Column, List<Field>> data = table.where(AGE, FieldComparator.Sign.LESS_THAN, new Field(12));
            table.deleteRows(data);
            List<Field> ageColumn = table.getData().get(table.getColumn(AGE));
            for (Field field : ageColumn) {
                assertTrue(field.getIntValue() >= 12);
            }

        } catch (Exception e) {
            fail(e);
        }
    }

    @Test
    public void deleteRowsByDataExpectException() {
        try {
            Map<Column, List<Field>> data = table.where(AGE, FieldComparator.Sign.LESS_THAN, new Field(12));
            data.get(table.getColumn(AGE)).add(new Field(70));
            assertThrows(Exception.class, () -> {
                table.deleteRows(data);
            });
        } catch (Exception e) {
            fail(e);
        }
    }

    @Test
    public void deleteInvalidRowNumber(){
        assertFalse(table.deleteRow(-1));
    }

    @Test
    public void deleteMaxNumberOfRows(){
        assertFalse(table.deleteRow(table.getNumberOfRows()+1));
    }
}
